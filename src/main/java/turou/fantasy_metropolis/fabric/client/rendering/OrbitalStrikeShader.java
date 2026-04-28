package turou.fantasy_metropolis.fabric.client.rendering;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import ladysnake.satin.api.managed.uniform.Uniform3f;
import ladysnake.satin.api.managed.uniform.UniformMat4;
import ladysnake.satin.api.util.GlMatrices;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrbitalStrikeShader implements PostWorldRenderCallback, ClientTickEvents.EndTick {

    private static final ResourceLocation SHADER_ID =
            new ResourceLocation("fantasy_metropolis", "shaders/post/fantasy_metropolis.json");

    public static final OrbitalStrikeShader INSTANCE = new OrbitalStrikeShader();

    private final Minecraft client = Minecraft.getInstance();
    private final Matrix4f projectionMatrix = new Matrix4f();

    private final ManagedShaderEffect shader =
            ShaderEffectManager.getInstance().manage(SHADER_ID, s ->
                    s.setSamplerUniform("DepthSampler",
                            ((ReadableDepthFramebuffer) client.getMainRenderTarget()).getStillDepthMap()));

    private final UniformMat4 uniformInverseTransformMatrix = shader.findUniformMat4("InverseTransformMatrix");
    private final Uniform3f uniformCameraPosition = shader.findUniform3f("CameraPosition");
    private final Uniform1f uniformiTime = shader.findUniform1f("iTime");
    private final Uniform3f uniformBlockPosition = shader.findUniform3f("BlockPosition");

    private Vector3f blockPosition = null;
    private ResourceKey<Level> dimension = null;
    private int ticks = 0;

    public void trigger(Vector3f pos, ResourceKey<Level> dim) {
        this.blockPosition = pos;
        this.dimension = dim;
        this.ticks = 0;
    }

    public void stopAnimation() {
        this.blockPosition = null;
        this.dimension = null;
        this.ticks = 0;
    }

    private boolean shouldRender() {
        var world = client.level;
        return blockPosition != null && world != null && world.dimension() == dimension;
    }

    @Override
    public void onEndTick(Minecraft minecraft) {
        if (ticks >= 1600
                || minecraft.level == null
                || minecraft.level.dimension() != dimension) {
            blockPosition = null;
            dimension = null;
        }

        if (shouldRender()) {
            ticks++;
        } else {
            ticks = 0;
        }
    }

    @Override
    public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
        if (shouldRender()) {
            uniformBlockPosition.set(blockPosition);
            uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));
            uniformCameraPosition.set(camera.getPosition().toVector3f());
            uniformiTime.set((ticks + tickDelta) / 20f);

            shader.render(tickDelta);
        }
    }
}
