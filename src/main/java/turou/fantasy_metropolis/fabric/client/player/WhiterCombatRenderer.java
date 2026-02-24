package turou.fantasy_metropolis.fabric.client.player;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;
import turou.fantasy_metropolis.fabric.state.ContainerState;

public class WhiterCombatRenderer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final ItemStackRenderState itemRenderState = new ItemStackRenderState();

    public WhiterCombatRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
            AvatarRenderState renderState, float yRot, float xRot) {
        var clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null)
            return;
        var entity = clientLevel.getEntity(renderState.id);
        if (!(entity instanceof LivingEntity livingEntity))
            return;

        var container = ContainerState.getClientContainer(entity.getUUID());
        var item = container.getItem(0);
        if (item.getItem() instanceof ItemSwordWhiter) {
            poseStack.pushPose();
            poseStack.translate(0, 0.35, 0.25);
            Minecraft.getInstance().getItemModelResolver().updateForLiving(itemRenderState, item,
                    ItemDisplayContext.FIXED, livingEntity);
            itemRenderState.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
