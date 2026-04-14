package turou.fantasy_metropolis.fabric.client.rendering;

import org.ladysnake.satin.api.event.PostWorldRenderCallback;
import org.ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.ladysnake.satin.api.managed.uniform.Uniform3f;
import org.ladysnake.satin.api.managed.uniform.UniformMat4;
import org.ladysnake.satin.api.util.GlMatrices;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class AbstractOrbitalRailgunShader implements PostWorldRenderCallback, ClientTickEvents.EndTick {
    protected final Minecraft client = Minecraft.getInstance();

    private final Matrix4f projectionMatrix = new Matrix4f();

    protected final ManagedShaderEffect SHADER = ShaderEffectManager.getInstance().manage(getIdentifier(), shader -> {
        shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)client.getMainRenderTarget()).getStillDepthMap());
    });
    private final UniformMat4 uniformInverseTransformMatrix = SHADER.findUniformMat4("InverseTransformMatrix");
    private final Uniform3f uniformCameraPosition = SHADER.findUniform3f("CameraPosition");
    private final Uniform1f uniformiTime = SHADER.findUniform1f("iTime");
    protected final Uniform3f uniformBlockPosition = SHADER.findUniform3f("BlockPosition");

    protected int ticks = 0;

    protected abstract ResourceLocation getIdentifier();
    protected abstract boolean shouldRender();

    @Override
    public void onEndTick(Minecraft minecraft) {
        if (shouldRender()) {
            ticks++;
        } else {
            ticks = 0;
        }
    }

    @Override
    public void onWorldRendered(Camera camera, float tickDelta) {
        if (shouldRender()) {
            uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));
            Vec3 cameraPos = camera.getPosition();
            uniformCameraPosition.set((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
            uniformiTime.set((ticks + tickDelta)/20f);

            SHADER.render(tickDelta);
        }
    }
}
