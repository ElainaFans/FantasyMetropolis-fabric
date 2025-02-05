package turou.fantasy_metropolis.fabric.client;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import turou.fantasy_metropolis.fabric.FantasyMetropolis;

import java.util.Arrays;
import java.util.List;

public class AnimationWorker {
    public static float renderTimer = 0;

    private static final List<ChatFormatting> colorCodesTitle = Arrays.asList(
            ChatFormatting.DARK_GRAY,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.AQUA,
            ChatFormatting.BLUE,
            ChatFormatting.DARK_AQUA,
            ChatFormatting.DARK_BLUE
    );

    private static final List<ChatFormatting> colorCodesDamage = Arrays.asList(
            ChatFormatting.RED,
            ChatFormatting.GOLD,
            ChatFormatting.YELLOW,
            ChatFormatting.GREEN,
            ChatFormatting.BLUE,
            ChatFormatting.AQUA,
            ChatFormatting.LIGHT_PURPLE
    );

    public static float increaseTimer(float value) {
        return renderTimer += value;
    }

    public static void resetTimer() {
        renderTimer = 0;
    }

    public static String marqueeTitle(String targetString) {
        return marquee(targetString, true, colorCodesTitle);
    }

    public static String marqueeDamage(String targetString) {
        return marquee(targetString, false, colorCodesDamage);
    }

    private static String marquee(String targetString, boolean bold, List<ChatFormatting> colorCodes) {
        var renderTick = (int) renderTimer;
        int colorCodeIndex = renderTick % colorCodes.size();

        char[] charArray = targetString.toCharArray();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            char currentChar = targetString.charAt(i);

            String colorCode = "" + colorCodes.get((i + colorCodeIndex) % colorCodes.size()) + (bold ? ChatFormatting.BOLD : "");
            string.append(colorCode).append(currentChar);
        }

        return string.toString();
    }

    public static ResourceLocation marqueeGif(int size) {
        int numIndex = ((int) renderTimer) % size;
        return ResourceLocation.fromNamespaceAndPath(FantasyMetropolis.MODID, "textures/tooltip/title/" + (numIndex + 1) + ".png");
    }
}
