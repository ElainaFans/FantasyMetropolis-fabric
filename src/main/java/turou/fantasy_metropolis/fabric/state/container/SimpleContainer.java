package turou.fantasy_metropolis.fabric.state.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SimpleContainer implements Container {
    int size;
    boolean dirty;
    NonNullList<ItemStack> items;

    public static final Codec<SimpleContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("Size").forGetter(c -> c.size),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("Items").forGetter(c -> List.copyOf(c.items)))
            .apply(instance, (size, itemList) -> {
                SimpleContainer c = new SimpleContainer(size);
                c.dirty = false;
                for (int i = 0; i < Math.min(itemList.size(), size); i++) {
                    c.items.set(i, itemList.get(i));
                }
                return c;
            }));

    public SimpleContainer(int size) {
        dirty = true;
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public @NotNull ItemStack getItem(int index) {
        return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
    }

    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack removeResult = ContainerHelper.removeItem(this.items, index, count);
        if (!removeResult.isEmpty()) {
            this.setChanged();
        }

        return removeResult;
    }

    public @NotNull ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.items.get(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.items.set(index, ItemStack.EMPTY);
            return stack;
        }
    }

    public void setItem(int index, @NotNull ItemStack stack) {
        this.items.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    public int getContainerSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setChanged() {
        setDirty(true);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    public String toString() {
        return this.items.stream().filter((stack) -> !stack.isEmpty()).toList().toString();
    }

    public void setSize(int size) {
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        setChanged();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        var registryOps = RegistryOps.create(NbtOps.INSTANCE, provider);
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                ItemStack.OPTIONAL_CODEC.encodeStart(registryOps, items.get(i))
                        .result().ifPresent(tag -> itemTag.put("Stack", tag));
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", items.size());
        nbt.putBoolean("Dirty", dirty);
        return nbt;
    }

    public void deserializeNBT(final CompoundTag nbt, HolderLookup.Provider provider) {
        this.dirty = nbt.getBooleanOr("Dirty", false);
        setSize(nbt.getIntOr("Size", items.size()));
        ListTag tagList = nbt.getListOrEmpty("Items");
        var registryOps = RegistryOps.create(NbtOps.INSTANCE, provider);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompoundOrEmpty(i);
            int slot = itemTags.getIntOr("Slot", -1);
            Tag tag = itemTags.get("Stack");
            if (slot >= 0 && slot < items.size() && tag != null) {
                ItemStack.OPTIONAL_CODEC.parse(registryOps, tag).result().ifPresent(stack -> items.set(slot, stack));
            }
        }
        setChanged();
    }
}
