package turou.fantasy_metropolis.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SwordExecutionEffectRenderer {
    private static final Identifier BEAM_TEXTURE = Identifier.withDefaultNamespace("textures/entity/beacon_beam.png");

    private SwordExecutionEffectRenderer() {
    }

    public static void render(WorldRenderContext context) {
        SwordExecutionEffectManager.ActiveEffect effect = SwordExecutionEffectManager.INSTANCE.getActiveEffect();
        if (effect == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameRenderer == null || minecraft.player == null || context.consumers() == null
                || context.matrices() == null) {
            return;
        }

        PoseStack poseStack = context.matrices();
        MultiBufferSource consumers = context.consumers();
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().position();
        Vec3 center = effect.centerVec3();
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float age = effect.age() + partialTick;
        float progress = effect.progress(partialTick);
        float warmup = Mth.clamp(progress * 4.0f, 0.0f, 1.0f);
        float fade = 1.0f - Mth.clamp((progress - 0.68f) / 0.32f, 0.0f, 1.0f);
        float beamHeight = 104.0f + effect.range() * 3.5f;
        float coreRadius = 0.65f + effect.range() * 0.02f;
        float outerRadius = coreRadius * (2.6f + warmup * 0.4f);
        float shellRadius = effect.range() * 0.72f + 2.4f;
        float apertureRadius = Mth.lerp(Mth.clamp(progress * 1.15f, 0.0f, 1.0f), coreRadius * 2.5f,
                effect.range() + 4.8f);
        float shockRadius = Mth.lerp(progress, coreRadius * 4.0f, effect.range() * 1.65f + 8.0f);
        int beamAlpha = (int) (255.0f * warmup * fade);
        int auraAlpha = (int) (200.0f * warmup * fade);
        int ringAlpha = (int) (225.0f * fade);
        int flashAlpha = (int) (190.0f * Mth.clamp(1.2f - progress * 1.5f, 0.0f, 1.0f));

        poseStack.pushPose();
        poseStack.translate(center.x() - cameraPos.x(), center.y() - cameraPos.y(), center.z() - cameraPos.z());

        VertexConsumer beam = consumers.getBuffer(RenderTypes.beaconBeam(BEAM_TEXTURE, true));
        renderBeam(beam, poseStack, beamHeight, coreRadius, beamAlpha, age * 0.05f, 170, 240, 255);
        renderBeam(beam, poseStack, beamHeight, outerRadius, auraAlpha, -age * 0.018f, 90, 170, 255);

        VertexConsumer swirl = consumers.getBuffer(RenderTypes.energySwirl(BEAM_TEXTURE, age * 0.015f, progress * 0.3f));
        renderBeam(swirl, poseStack, beamHeight * 0.92f, shellRadius, flashAlpha, -age * 0.01f, 180, 250, 255);

        VertexConsumer lightning = consumers.getBuffer(RenderTypes.lightning());
        renderShockRing(lightning, poseStack, shockRadius, 0.03f, 1.1f, ringAlpha, 120, 230, 255);
        renderShockRing(lightning, poseStack, apertureRadius, 0.02f, 0.55f, ringAlpha / 2, 170, 250, 255);
        renderCross(lightning, poseStack, apertureRadius * 0.78f, ringAlpha);
        renderImpactColumn(lightning, poseStack, shellRadius * 0.92f, 18.0f + effect.range() * 0.45f, flashAlpha);
        renderOrbitalSpikes(lightning, poseStack, shellRadius, beamHeight, auraAlpha);
        renderConvergingLances(lightning, poseStack, shellRadius * 1.45f, beamHeight, beamAlpha);

        poseStack.popPose();
    }

    private static void renderBeam(VertexConsumer consumer, PoseStack poseStack, float height, float radius, int alpha,
            float uvOffset, int red, int green, int blue) {
        addBeamFace(consumer, poseStack, -radius, -radius, radius, -radius, height, red, green, blue, alpha, uvOffset);
        addBeamFace(consumer, poseStack, radius, -radius, radius, radius, height, red, green, blue, alpha, uvOffset);
        addBeamFace(consumer, poseStack, radius, radius, -radius, radius, height, red, green, blue, alpha, uvOffset);
        addBeamFace(consumer, poseStack, -radius, radius, -radius, -radius, height, red, green, blue, alpha, uvOffset);
    }

    private static void addBeamFace(VertexConsumer consumer, PoseStack poseStack, float x1, float z1, float x2,
            float z2, float height, int red, int green, int blue, int alpha, float uvOffset) {
        consumer.addVertex(poseStack.last(), x1, height, z1).setColor(red, green, blue, alpha).setUv(1.0f, uvOffset)
                .setOverlay(0).setLight(LightTexture.FULL_BRIGHT).setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);
        consumer.addVertex(poseStack.last(), x1, 0.0f, z1).setColor(red, green, blue, alpha)
                .setUv(1.0f, height / 4.0f + uvOffset).setOverlay(0).setLight(LightTexture.FULL_BRIGHT)
                .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);
        consumer.addVertex(poseStack.last(), x2, 0.0f, z2).setColor(red, green, blue, alpha)
                .setUv(0.0f, height / 4.0f + uvOffset).setOverlay(0).setLight(LightTexture.FULL_BRIGHT)
                .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);
        consumer.addVertex(poseStack.last(), x2, height, z2).setColor(red, green, blue, alpha).setUv(0.0f, uvOffset)
                .setOverlay(0).setLight(LightTexture.FULL_BRIGHT).setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);
    }

    private static void renderShockRing(VertexConsumer consumer, PoseStack poseStack, float radius, float y,
            float thickness, int alpha, int red, int green, int blue) {
        int segments = 64;
        float inner = Math.max(radius - thickness, 0.08f);
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (Math.PI * 2.0 * i / segments);
            float angle2 = (float) (Math.PI * 2.0 * (i + 1) / segments);
            float x1 = Mth.cos(angle1) * radius;
            float z1 = Mth.sin(angle1) * radius;
            float x2 = Mth.cos(angle2) * radius;
            float z2 = Mth.sin(angle2) * radius;
            float ix1 = Mth.cos(angle1) * inner;
            float iz1 = Mth.sin(angle1) * inner;
            float ix2 = Mth.cos(angle2) * inner;
            float iz2 = Mth.sin(angle2) * inner;

            addLightningQuad(consumer, poseStack, x1, y, z1, x2, y, z2, ix2, y, iz2, ix1, y, iz1,
                    red, green, blue, alpha);
        }
    }

    private static void renderCross(VertexConsumer consumer, PoseStack poseStack, float radius, int alpha) {
        float width = 0.42f;
        addLightningQuad(consumer, poseStack, -width, 0.01f, -radius, width, 0.01f, -radius, width, 0.01f, radius,
                -width, 0.01f, radius, 170, 240, 255, alpha / 2);
        addLightningQuad(consumer, poseStack, -radius, 0.01f, -width, radius, 0.01f, -width, radius, 0.01f, width,
                -radius, 0.01f, width, 170, 240, 255, alpha / 2);
    }

    private static void renderImpactColumn(VertexConsumer consumer, PoseStack poseStack, float radius, float height,
            int alpha) {
        addLightningQuad(consumer, poseStack, -radius, height, 0.0f, radius, height, 0.0f, 0.0f, 0.0f, radius,
                0.0f, 0.0f, -radius, 170, 245, 255, alpha);
        addLightningQuad(consumer, poseStack, 0.0f, height, -radius, 0.0f, height, radius, -radius, 0.0f, 0.0f,
                radius, 0.0f, 0.0f, 170, 245, 255, alpha);
    }

    private static void renderOrbitalSpikes(VertexConsumer consumer, PoseStack poseStack, float radius, float height,
            int alpha) {
        int spikeCount = 10;
        for (int i = 0; i < spikeCount; i++) {
            float angle = (float) (Math.PI * 2.0 * i / spikeCount);
            float nextAngle = angle + 0.22f;
            float x1 = Mth.cos(angle) * radius;
            float z1 = Mth.sin(angle) * radius;
            float x2 = Mth.cos(nextAngle) * radius * 0.52f;
            float z2 = Mth.sin(nextAngle) * radius * 0.52f;
            addLightningQuad(consumer, poseStack, 0.0f, height, 0.0f, x1, height * 0.38f, z1, x2,
                    height * 0.14f, z2, 0.0f, height * 0.22f, 0.0f, 120, 220, 255, alpha / 2);
        }
    }

    private static void renderConvergingLances(VertexConsumer consumer, PoseStack poseStack, float radius, float height,
            int alpha) {
        int lanceCount = 6;
        for (int i = 0; i < lanceCount; i++) {
            float angle = (float) (Math.PI * 2.0 * i / lanceCount);
            float x = Mth.cos(angle) * radius;
            float z = Mth.sin(angle) * radius;
            addLightningQuad(consumer, poseStack, x, height * 0.95f, z, x * 0.58f, height * 0.48f, z * 0.58f,
                    x * 0.18f, 0.0f, z * 0.18f, 0.0f, height * 0.2f, 0.0f, 110, 210, 255, alpha / 2);
        }
    }

    private static void addLightningQuad(VertexConsumer consumer, PoseStack poseStack, float x1, float y1, float z1,
            float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int red,
            int green, int blue, int alpha) {
        consumer.addVertex(poseStack.last(), x1, y1, z1).setColor(red, green, blue, alpha)
                .setLight(LightTexture.FULL_BRIGHT);
        consumer.addVertex(poseStack.last(), x2, y2, z2).setColor(red, green, blue, alpha)
                .setLight(LightTexture.FULL_BRIGHT);
        consumer.addVertex(poseStack.last(), x3, y3, z3).setColor(red, green, blue, 0)
                .setLight(LightTexture.FULL_BRIGHT);
        consumer.addVertex(poseStack.last(), x4, y4, z4).setColor(red, green, blue, 0)
                .setLight(LightTexture.FULL_BRIGHT);
    }
}
