package turou.fantasy_metropolis.fabric.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;

public class TooltipRenderer {
    private static GuiGraphics guiGraphicsContext;

    public static void setGuiGraphicsContext(GuiGraphics guiGraphics) {
        guiGraphicsContext = guiGraphics;
    }

    private static ResourceLocation getPath(String part) {
        return new ResourceLocation(FantasyMetropolis.MODID, "textures/tooltip/" + part + ".png");
    }

    private static ResourceLocation getBorderPath(String borderPart) {
        return getPath("border_" + borderPart);
    }

    private static void innerBlit(ResourceLocation pAtlasLocation, int pX, int pY, int pWidth, int pHeight) {
        // duration maybe negative, we don't render negative width
        if (pWidth > 0) guiGraphicsContext.blit(pAtlasLocation, pX, pY, 0, 0, pWidth, pHeight, pWidth, pHeight);
    }

    public static void renderCharacter(int x, int y) {
        guiGraphicsContext.pose().pushPose();
        guiGraphicsContext.pose().translate(0, 0, 400);
        RenderSystem.enableBlend();
        innerBlit(getPath("character_left"), x - 68, y - 16, 60, 83);
        RenderSystem.disableBlend();
        guiGraphicsContext.pose().popPose();
    }

    public static void renderBackground(int x, int y, int width, int height) {
        guiGraphicsContext.pose().pushPose();
        guiGraphicsContext.pose().translate(0, 0, 400); // beneath the text and upon other items
        RenderSystem.enableBlend();

        // render for border
        var widthOffset = 2;
        width += widthOffset;
        var horizonStart = x - 16;
        var horizonDuration = width - 55;
        var horizonEnd = x - 30 + width;
        var verticalStart = y - 14;
        var verticalDuration = height - 38;
        var verticalEnd = y - 20 + height;
        var horizonCenter = (int) (horizonStart + 0.5 * width);
        innerBlit(getBorderPath("left_top"), horizonStart, verticalStart, 41, 37);
        innerBlit(getBorderPath("top"), horizonStart + 41, verticalStart + 7, horizonDuration, 4);
        innerBlit(getBorderPath("right_top"), horizonEnd, verticalStart, 40, 37);
        innerBlit(getBorderPath("left_bottom"), horizonStart, verticalEnd, 46, 32);
        innerBlit(getBorderPath("left"), horizonStart + 9, verticalStart + 37, 1, verticalDuration);
        innerBlit(getBorderPath("right"), horizonEnd + 30, verticalStart + 37, 1, verticalDuration);
        innerBlit(getBorderPath("right_bottom"), horizonEnd - 6, verticalEnd, 46, 32);
        innerBlit(getBorderPath("bottom"), horizonStart + 46, verticalEnd + 25, width - 66, 2);
        innerBlit(getBorderPath("stars"), horizonCenter + 7, verticalStart + 8, 12, 3);

        // render for background
        guiGraphicsContext.pose().translate(0, 0, -1); // beneath the text and upon other items (under the border)
        innerBlit(getPath("fancy_bg"), x - 6, y - 3, width + 8, height + 6);

        RenderSystem.disableBlend();
        guiGraphicsContext.pose().popPose();
    }
}
