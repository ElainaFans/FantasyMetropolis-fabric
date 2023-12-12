package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.client.TooltipRenderer;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci) {
        if (TooltipRenderer.shouldRender()) {
            components.subList(0, 4).clear();
            components.addAll(0, TooltipRenderer.getComponents());
        }
    }

    @Inject(method = "renderTooltipInternal", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltipInternalEnd(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci) {
        TooltipRenderer.setItemStackContext(null); // we clear context when render tooltip is end.
    }
}
