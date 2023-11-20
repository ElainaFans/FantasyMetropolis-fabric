package turou.fantasy_metropolis.fabric.client.container;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

public class WhiterSlot extends Slot {
    public WhiterSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ItemSwordWhiter;
    }
}
