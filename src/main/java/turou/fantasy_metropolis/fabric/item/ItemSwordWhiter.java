package turou.fantasy_metropolis.fabric.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import turou.fantasy_metropolis.fabric.RegisterHandler;
import turou.fantasy_metropolis.fabric.util.DamageUtil;
import turou.fantasy_metropolis.fabric.util.PlayerUtil;

public class ItemSwordWhiter extends Item {
    public static final ToolMaterial WHITER_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            Integer.MAX_VALUE,
            Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY,
            Integer.MAX_VALUE,
            ItemTags.NETHERITE_TOOL_MATERIALS);

    private static final int RANGE_ATTACK = 5;

    public ItemSwordWhiter() {
        super(new Properties()
                .sword(WHITER_MATERIAL, (int) Float.POSITIVE_INFINITY, 9996)
                .fireResistant());
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.set(RegisterHandler.SWORD_RANGE, 10);
        return stack;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Player pPlayer) {
        super.onCraftedBy(pStack, pPlayer);
        pStack.set(RegisterHandler.SWORD_RANGE, 10);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity,
            InteractionHand usedHand) {
        if (!player.level().isClientSide() && livingEntity instanceof Player targetPlayer) {
            // Creative who has the sword will not be set dead
            if (targetPlayer.isCreative() && !PlayerUtil.hasSword(targetPlayer)) {
                DamageUtil.punishPlayer(targetPlayer);
            }
            livingEntity.setHealth(0.0f);
            DamageUtil.killLivingEntity(livingEntity);
            DamageUtil.hurtRange(RANGE_ATTACK, player, player.level(), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setHealth(0.0f);
        DamageUtil.killLivingEntity(target);
        if (attacker instanceof Player player) {
            DamageUtil.hurtRange(RANGE_ATTACK, player, attacker.level(), false);
        }
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand.equals(InteractionHand.MAIN_HAND) && player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("whiter_sword.kill_range"), false);
                int range = player.getItemInHand(InteractionHand.MAIN_HAND).getOrDefault(RegisterHandler.SWORD_RANGE,
                        0);
                DamageUtil.hurtRange(range, player, level, true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
    public void inventoryTick(ItemStack pStack, ServerLevel pLevel, Entity pEntity, EquipmentSlot pSlot) {
        if (!(pEntity instanceof Player player))
            return;
        // The first one who has it will be the owner
        if (!pStack.has(RegisterHandler.SWORD_OWNER))
            pStack.set(RegisterHandler.SWORD_OWNER, player.getUUID());
        if (!player.getUUID().equals(pStack.get(RegisterHandler.SWORD_OWNER))) {
            player.getInventory().removeItem(pStack);
            player.drop(pStack, false, false);
        }
    }
}
