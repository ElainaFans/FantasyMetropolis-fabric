package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turou.fantasy_metropolis.fabric.state.ContainerState;
import turou.fantasy_metropolis.fabric.state.container.SimpleContainer;
import turou.fantasy_metropolis.fabric.state.container.WhiterSlot;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu {
    protected InventoryMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }
    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V")
    private void init(Inventory playerInventory, boolean active, Player owner, CallbackInfo ci) {
        SimpleContainer container = ContainerState.getContainer(owner);
        this.addSlot(new WhiterSlot(container, 0, 77, 8));
    }
}