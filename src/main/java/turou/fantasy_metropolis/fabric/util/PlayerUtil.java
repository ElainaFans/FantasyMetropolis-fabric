package turou.fantasy_metropolis.fabric.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

public class PlayerUtil {
    public static boolean hasSword(Player player) {
        for(ItemStack itemStack : player.getInventory().items) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemSwordWhiter) {
                return true;
            }
        }
        return false;
    }
}
