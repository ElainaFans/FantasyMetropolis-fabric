package turou.fantasy_metropolis.fabric.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.util.DamageUtil;
import turou.fantasy_metropolis.fabric.util.PlayerUtil;

public class ItemSwordWhiter extends SwordItem {
    private static final Properties properties = new Properties().fireResistant();
    private static final int RANGE_ATTACK = 5;


    public ItemSwordWhiter() {
        super(new TierWhiter(), 0, 9999, properties);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("range", 10);
        return stack;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        pStack.getOrCreateTag().putInt("range", 10);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean returnValue = super.hurtEnemy(stack, target, attacker);
        target.setHealth(0.0f);
        DamageUtil.killLivingEntity(target);
        if (attacker instanceof Player player) {
            DamageUtil.hurtRange(RANGE_ATTACK, player, attacker.level(), false);
        }
        return returnValue;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand.equals(InteractionHand.MAIN_HAND) && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.translatable("whiter_sword.kill_range"));
                int range = player.getItemInHand(InteractionHand.MAIN_HAND).getOrCreateTag().getInt("range");
                DamageUtil.hurtRange(range, player, level, true);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pItemSlot, boolean pIsSelected) {
        if (!(pEntity instanceof Player player)) return;
        // The first one who has it will be the owner
        CompoundTag tag = pStack.getOrCreateTag();
        if (!tag.contains("owner")) {
            tag.putUUID("owner", pEntity.getUUID());
        } else if (!player.getUUID().equals(tag.getUUID("owner"))) {
            player.getInventory().removeItem(pStack);
            player.drop(pStack, false, false);
        }
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        // set attack speed
        if (pEquipmentSlot.equals(EquipmentSlot.MAINHAND)) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", 9996, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return ImmutableMultimap.of();
    }

    private static class TierWhiter implements Tier {
        @Override
        public int getUses() {
            return Integer.MAX_VALUE;
        }

        @Override
        public float getSpeed() {
            return (float) Double.POSITIVE_INFINITY;
        }

        @Override
        public float getAttackDamageBonus() {
            return (float) Double.POSITIVE_INFINITY;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return Integer.MAX_VALUE;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of();
        }
    }
}
