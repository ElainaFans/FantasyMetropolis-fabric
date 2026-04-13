#version 330 compatibility

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform TransformConfig {
    mat4 InverseTransformMatrix;
};

layout(std140) uniform StrikeConfig {
    vec3 CameraPosition;
    float Time;
    vec3 BlockPosition;
    float Padding;
};

in vec2 texCoord;
out vec4 fragColor;

const float MIN_DIST = 0.001;
const float MAX_DIST = 280.0;
const int STEPS = 180;

vec3 rotateY(vec3 p, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec3(p.x * c - p.z * s, p.y, p.x * s + p.z * c);
}

float smoothMin(float a, float b, float k) {
    float h = max(k - abs(a - b), 0.0) / k;
    return min(a, b) - h * h * h * k * (1.0 / 6.0);
}

float strikeShape(vec3 p) {
    float localTime = Time * 2.6;
    vec3 q = rotateY(p, localTime * 0.55);

    float beam = length(q.xz) - (0.9 + 0.2 * sin(localTime * 5.0));
    float shell = abs(length(q) - (12.0 + sin(localTime * 1.7) * 1.5));
    float ring = abs(length(q.xz) - (6.0 + localTime * 3.8)) - 0.45;
    float column = length(vec2(length(q.xz) - 2.8, max(abs(q.y - 14.0) - 20.0, 0.0))) - 0.65;

    float shape = smoothMin(beam, shell, 2.8);
    shape = smoothMin(shape, ring, 2.5);
    shape = smoothMin(shape, column, 2.0);
    return shape;
}

vec3 worldPos(vec3 ndcPoint) {
    vec4 homPos = InverseTransformMatrix * vec4(ndcPoint * 2.0 - 1.0, 1.0);
    vec3 viewPos = homPos.xyz / homPos.w;
    return viewPos + CameraPosition;
}

vec2 raymarch(vec3 origin, vec3 dir) {
    float traveled = 0.0;
    float glow = 0.0;
    for (int i = 0; i < STEPS; i++) {
        vec3 pos = origin + dir * traveled;
        float dist = strikeShape(pos);
        glow += 0.02 / (0.08 + abs(dist));
        if (dist < MIN_DIST || traveled > MAX_DIST) {
            return vec2(traveled, glow);
        }
        traveled += clamp(dist, 0.03, 6.0);
    }
    return vec2(MAX_DIST, glow);
}

void main() {
    vec3 original = texture(InSampler, texCoord).rgb;
    float depth = texture(DepthSampler, texCoord).r;

    vec3 startPoint = worldPos(vec3(texCoord, 0.0)) - BlockPosition;
    vec3 endPoint = worldPos(vec3(texCoord, depth)) - BlockPosition;
    vec3 dir = normalize(endPoint - startPoint);

    vec2 result = raymarch(startPoint, dir);
    vec3 hitPoint = startPoint + dir * result.x;
    float hit = step(strikeShape(hitPoint), MIN_DIST * 2.0);
    hit *= step(distance(startPoint, hitPoint), distance(startPoint, endPoint) + 0.001);

    float pulse = 0.5 + 0.5 * sin(Time * 16.0);
    vec3 beamColor = mix(vec3(0.28, 0.72, 1.0), vec3(0.92, 0.98, 1.0), pulse);
    vec3 shock = beamColor * result.y * 0.45;
    vec3 composed = original + shock;
    composed = mix(composed, beamColor * 1.45, hit);

    fragColor = vec4(composed, 1.0);
}
