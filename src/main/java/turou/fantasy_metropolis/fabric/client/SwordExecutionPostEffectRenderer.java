package turou.fantasy_metropolis.fabric.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;
import turou.fantasy_metropolis.fabric.mixin.PostChainAccessor;
import turou.fantasy_metropolis.fabric.mixin.PostPassAccessor;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SwordExecutionPostEffectRenderer {
    private static final Identifier EFFECT_ID = Identifier.fromNamespaceAndPath(FantasyMetropolis.MODID,
            "sword_execution");
    private static final String TRANSFORM_BLOCK = "TransformConfig";
    private static final String STRIKE_BLOCK = "StrikeConfig";
    private static final Set<Identifier> TARGETS = Set.of(PostChain.MAIN_TARGET_ID);

    private SwordExecutionPostEffectRenderer() {
    }

    public static void render(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        SwordExecutionEffectManager.ActiveEffect effect = SwordExecutionEffectManager.INSTANCE.getActiveEffect();
        if (effect == null || minecraft.level == null || minecraft.gameRenderer == null) {
            return;
        }

        ShaderManager shaderManager = minecraft.getShaderManager();
        PostChain chain = shaderManager.getPostChain(EFFECT_ID, TARGETS);
        if (chain == null) {
            return;
        }

        updateUniforms(chain, minecraft, effect, partialTick);
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        chain.process(mainTarget, GraphicsResourceAllocator.UNPOOLED);
    }

    private static void updateUniforms(PostChain chain, Minecraft minecraft,
            SwordExecutionEffectManager.ActiveEffect effect, float partialTick) {
        Matrix4f inverseTransform = new Matrix4f();
        inverseTransform.set(minecraft.gameRenderer.getProjectionMatrix(1.0f));
        inverseTransform.mul(new Matrix4f().rotation(minecraft.gameRenderer.getMainCamera().rotation()));
        inverseTransform.invert();

        Vector3f cameraPosition = minecraft.gameRenderer.getMainCamera().position().toVector3f();
        Vector3f blockPosition = effect.centerVec();
        float time = (effect.age() + partialTick) / 20.0f;

        List<PostPass> passes = ((PostChainAccessor) chain).fantasyMetropolis$getPasses();
        for (PostPass pass : passes) {
            Map<String, GpuBuffer> uniforms = ((PostPassAccessor) pass).fantasyMetropolis$getCustomUniforms();
            replaceUniformBuffer(uniforms, TRANSFORM_BLOCK, createTransformBlock(inverseTransform));
            replaceUniformBuffer(uniforms, STRIKE_BLOCK, createStrikeBlock(cameraPosition, blockPosition, time));
        }
    }

    private static GpuBuffer createTransformBlock(Matrix4f value) {
        ByteBuffer bytes = ByteBuffer.allocateDirect(64);
        Std140Builder.intoBuffer(bytes).putMat4f(value);
        bytes.rewind();
        return com.mojang.blaze3d.systems.RenderSystem.getDevice().createBuffer(
                () -> "fantasy_metropolis_sword_execution_transform",
                GpuBuffer.USAGE_UNIFORM,
                bytes);
    }

    private static GpuBuffer createStrikeBlock(Vector3f cameraPosition, Vector3f blockPosition, float time) {
        ByteBuffer bytes = ByteBuffer.allocateDirect(64);
        Std140Builder builder = Std140Builder.intoBuffer(bytes);
        builder.putVec3(cameraPosition);
        builder.putFloat(time);
        builder.putVec3(blockPosition);
        builder.putFloat(0.0f);
        bytes.rewind();
        return com.mojang.blaze3d.systems.RenderSystem.getDevice().createBuffer(
                () -> "fantasy_metropolis_sword_execution_strike",
                GpuBuffer.USAGE_UNIFORM,
                bytes);
    }

    private static void replaceUniformBuffer(Map<String, GpuBuffer> uniforms, String name, GpuBuffer replacement) {
        GpuBuffer oldBuffer = uniforms.put(name, replacement);
        if (oldBuffer != null) {
            oldBuffer.close();
        }
    }
}
