package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.client.TooltipRenderer;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltip(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY,
            ClientTooltipPositioner tooltipPositioner, Identifier tooltipStyle, CallbackInfo ci) {
        if (TooltipRenderer.shouldRender()) {
            int size = components.size();
            int delSize = Math.min(size, 5);
            components.subList(0, delSize).clear();
            components.addAll(0, TooltipRenderer.getComponents());
        }
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltipEnd(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY,
            ClientTooltipPositioner tooltipPositioner, Identifier tooltipStyle, CallbackInfo ci) {
        TooltipRenderer.setItemStackContext(null); // we clear context when render tooltip is end.
    }
}
