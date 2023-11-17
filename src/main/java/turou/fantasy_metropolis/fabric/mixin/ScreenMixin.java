package turou.fantasy_metropolis.fabric.mixin;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.client.AnimationWorker;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "getTooltipFromItem", at=@At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void renderTooltipBackground(Minecraft minecraft, ItemStack item, CallbackInfoReturnable<List<Component>> cir) {
        if (item.getItem() instanceof ItemSwordWhiter) {
            int range = item.getOrCreateTag().getInt("range");
            List<Component> list = Lists.newArrayList();

            list.add(Component.literal(AnimationWorker.marqueeTitle(I18n.get("tooltip.whiter_sword.title"))));
            list.add(1, Component.literal(ChatFormatting.LIGHT_PURPLE + "+ "  + I18n.get("tooltip.skill.hint")));
            list.add(2, Component.literal(ChatFormatting.BLUE + "+ "  + I18n.get("tooltip.skill.range") + range));
            list.add(3, Component.literal(""));
            list.add(4, Component.literal(ChatFormatting.BLUE + "+ " + AnimationWorker.marqueeDamage(I18n.get("tooltip.attack.damage")) + " " + I18n.get("tooltip.attack.hint")));

            cir.setReturnValue(list);
        }
    }
}
