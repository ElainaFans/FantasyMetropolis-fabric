package turou.fantasy_metropolis.fabric.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

public class PlayerUtil {
    public static boolean hasSword(Entity entity) {
        if (!(entity instanceof Player player))
            return false;
        for (ItemStack itemStack : player.getInventory().getNonEquipmentItems()) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemSwordWhiter) {
                return true;
            }
        }
        return false;
    }
}
