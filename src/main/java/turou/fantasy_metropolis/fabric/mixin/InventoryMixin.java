package turou.fantasy_metropolis.fabric.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Mutable
    @Final
    @Shadow
    public final Player player;
    @Mutable
    @Final
    @Shadow
    public final NonNullList<ItemStack> items;
    @Mutable
    @Final
    @Shadow
    public final NonNullList<ItemStack> armor;
    @Mutable
    @Final
    @Shadow
    public final NonNullList<ItemStack> offhand;
    @Mutable
    @Final
    @Shadow
    private List<NonNullList<ItemStack>> compartments;
    @Unique
    public final NonNullList<ItemStack> accessories = NonNullList.withSize(1, ItemStack.EMPTY);

    private InventoryMixin() {
        this.player = null;
        items = null;
        armor = null;
        offhand = null;
        compartments = null;
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/entity/player/Player;)V")
    private void init(Player player, CallbackInfo ci) {
        assert items != null;
        assert armor != null;
        assert offhand != null;
        compartments = ImmutableList.of(items, armor, offhand, accessories);
    }

    /**
     * @author TuRou
     * @reason It's hard to modify iterator using inject
     */
    @Overwrite
    public void dropAll() {
        assert items != null;
        assert armor != null;
        assert offhand != null;
        var original_compartments = ImmutableList.of(items, armor, offhand);
        for (var list : original_compartments) {
            for(int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);
                if (!itemStack.isEmpty()) {
                    assert this.player != null;
                    this.player.drop(itemStack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void save(ListTag listTag, CallbackInfoReturnable<ListTag> cir) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByte("Slot", (byte)(255)); // MOJANG FUCK YOU
        this.accessories.get(0).save(compoundTag);
        listTag.add(compoundTag);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void load(ListTag listTag, CallbackInfo ci) {
        this.accessories.clear();
        for(int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.of(compoundTag);
            if (j == 255) {
                this.accessories.set(0, itemStack);
            }
        }
    }
}
