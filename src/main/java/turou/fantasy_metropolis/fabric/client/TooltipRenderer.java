package turou.fantasy_metropolis.fabric.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;
import turou.fantasy_metropolis.fabric.RegisterHandler;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

import java.util.List;
import java.util.stream.Collectors;

public class TooltipRenderer {
    private static GuiGraphics guiGraphicsContext;
    private static ItemStack itemStackContext;

    public static void setGuiGraphicsContext(GuiGraphics guiGraphics) {
        guiGraphicsContext = guiGraphics;
    }

    public static void setItemStackContext(ItemStack itemStackContext) {
        TooltipRenderer.itemStackContext = itemStackContext;
    }

    public static boolean shouldRender() {
        return itemStackContext != null && itemStackContext.getItem() instanceof ItemSwordWhiter;
    }

    private static ResourceLocation getPath(String part) {
        return ResourceLocation.fromNamespaceAndPath(FantasyMetropolis.MODID, "textures/tooltip/" + part + ".png");
    }

    private static ResourceLocation getBorderPath(String borderPart) {
        return getPath("border_" + borderPart);
    }

    private static void innerBlit(ResourceLocation pAtlasLocation, int pX, int pY, int pWidth, int pHeight) {
        // duration maybe negative, we don't render negative width
        if (pWidth > 0) guiGraphicsContext.blit(pAtlasLocation, pX, pY, 0, 0, pWidth, pHeight, pWidth, pHeight);
    }

    public static List<ClientTooltipComponent> getComponents() {
        int range = itemStackContext.getOrDefault(RegisterHandler.SWORD_RANGE, 0);
        List<Component> list = Lists.newArrayList();

        // list.add(Component.literal(AnimationWorker.marqueeTitle(I18n.get("tooltip.whiter_sword.title"))));
        list.add(Component.literal("")); // keep enough space for gif title
        list.add(1, Component.literal(ChatFormatting.LIGHT_PURPLE + "+ "  + I18n.get("tooltip.skill.hint")));
        list.add(2, Component.literal(ChatFormatting.BLUE + "+ "  + I18n.get("tooltip.skill.range") + range));
        list.add(3, Component.literal(""));
        list.add(4, Component.literal(ChatFormatting.BLUE + "+ " + AnimationWorker.marqueeDamage(I18n.get("tooltip.attack.damage")) + " " + I18n.get("tooltip.attack.hint")));

        var tooltipLines = Lists.transform(list, Component::getVisualOrderText);
        return tooltipLines.stream().map(ClientTooltipComponent::create).collect(Collectors.toList());
    }

    public static void renderCharacter(int x, int y) {
        guiGraphicsContext.pose().pushPose();
        guiGraphicsContext.pose().translate(0, 0, 400);
        RenderSystem.enableBlend();
        innerBlit(getPath("character_left"), x - 68, y - 16, 58, 80);
        RenderSystem.disableBlend();
        guiGraphicsContext.pose().popPose();
    }

    public static void renderTitle(int x, int y, int width) {
        guiGraphicsContext.pose().pushPose();
        guiGraphicsContext.pose().translate(0, 0, 450);
        RenderSystem.enableBlend();
        var currentImage = AnimationWorker.marqueeGif(29);
        innerBlit(currentImage, x - 24 + width / 2, y - 27, 46, 60);
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
