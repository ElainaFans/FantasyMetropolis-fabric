package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.client.TooltipRenderer;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Shadow
    protected Slot hoveredSlot;

    @Inject(method = "render", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.hoveredSlot == null || !this.hoveredSlot.hasItem()) {
            TooltipRenderer.setItemStackContext(null);
        }
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemStack = this.hoveredSlot.getItem();
            TooltipRenderer.setItemStackContext(itemStack);
        }
    }
}
