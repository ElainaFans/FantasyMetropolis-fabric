package turou.fantasy_metropolis.fabric.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.RegisterHandler;
import turou.fantasy_metropolis.fabric.util.DamageUtil;
import turou.fantasy_metropolis.fabric.util.PlayerUtil;

public class ItemSwordWhiter extends SwordItem {
    private static final Properties properties = new Properties().fireResistant().attributes(SwordItem.createAttributes(new TierWhiter(), 0, 9996));
    private static final int RANGE_ATTACK = 5;


    public ItemSwordWhiter() {
        super(new TierWhiter(), properties);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.set(RegisterHandler.SWORD_RANGE, 10);
        return stack;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        pStack.set(RegisterHandler.SWORD_RANGE, 10);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand usedHand) {
        if (!player.level().isClientSide && livingEntity instanceof Player targetPlayer) {
            // Creative who has the sword will not be set dead
            if (targetPlayer.isCreative() && !PlayerUtil.hasSword(targetPlayer)) {
                DamageUtil.punishPlayer(targetPlayer);
            }
            livingEntity.setHealth(0.0f);
            DamageUtil.killLivingEntity(livingEntity);
            DamageUtil.hurtRange(RANGE_ATTACK, player, player.level(), false);
        }
        return InteractionResult.SUCCESS; // We don't need the rest of the interaction
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
                int range = player.getItemInHand(InteractionHand.MAIN_HAND).getOrDefault(RegisterHandler.SWORD_RANGE, 0);
                DamageUtil.hurtRange(range, player, level, true);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // prohibit damaging the tools
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pItemSlot, boolean pIsSelected) {
        if (!(pEntity instanceof Player player)) return;
        // The first one who has it will be the owner
        if (!pStack.has(RegisterHandler.SWORD_OWNER)) pStack.set(RegisterHandler.SWORD_OWNER, player.getUUID());
        if (!player.getUUID().equals(pStack.get(RegisterHandler.SWORD_OWNER))) {
            player.getInventory().removeItem(pStack);
            player.drop(pStack, false, false);
        }
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
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.AIR;
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
