package turou.fantasy_metropolis.fabric.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class OrbitalRailgunShader extends AbstractOrbitalRailgunShader {
    public static final ResourceLocation ORBITAL_RAILGUN_SHADER = ResourceLocation.fromNamespaceAndPath("fantasy_metropolis", "shaders/post/orbital_railgun.json");
    public static final OrbitalRailgunShader INSTANCE = new OrbitalRailgunShader();

    public Vector3f BlockPosition = null;
    public ResourceKey<Level> Dimension = null;

    @Override
    protected ResourceLocation getIdentifier() {
        return ORBITAL_RAILGUN_SHADER;
    }

    @Override
    protected boolean shouldRender() {
        var world = Minecraft.getInstance().level;
        return BlockPosition != null && world != null && world.dimension() == Dimension;
    }

    @Override
    public void onEndTick(Minecraft minecraft) {
        if (ticks >= 1600 || minecraft.level == null || minecraft.level.dimension() != Dimension) {
            BlockPosition = null;
            Dimension = null;
        }

        super.onEndTick(minecraft);
    }

    @Override
    public void onWorldRendered(Camera camera, float tickDelta) {
        if (shouldRender()) {
            uniformBlockPosition.set(BlockPosition);
        }

        super.onWorldRendered(camera, tickDelta);
    }
}
