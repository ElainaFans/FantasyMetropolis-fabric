package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.client.TooltipRenderer;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
    @Inject(method = "renderTooltipBackground", at=@At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void renderTooltipBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, int z, CallbackInfo ci) {
        TooltipRenderer.setGuiGraphicsContext(guiGraphics);
        if (TooltipRenderer.shouldRender()) {
            TooltipRenderer.renderBackground(x, y, width, height);
            TooltipRenderer.renderTitle(x, y, width);
            TooltipRenderer.renderCharacter(x, y);
            ci.cancel();
        }
    }
}
