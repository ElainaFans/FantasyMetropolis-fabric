package turou.fantasy_metropolis.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import turou.fantasy_metropolis.fabric.client.AnimationWorker;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;
import turou.fantasy_metropolis.fabric.state.ContainerState;
import turou.fantasy_metropolis.fabric.util.PlayerUtil;

public class EventHandler {
    // So sad that fabric provided almost nothing events we need.
    public static void registerEvents() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            var itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            var block = player.level().getBlockState(pos).getBlock();
            // replace the real bedrock with our fake bedrock
            if (block.equals(RegisterHandler.BEDROCK) && !PlayerUtil.hasSword(player)) {
                player.level().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
            } else if (block.equals(Blocks.BEDROCK) && itemStack.getItem() instanceof DiggerItem && PlayerUtil.hasSword(player)) {
                player.level().setBlock(pos, RegisterHandler.BEDROCK.defaultBlockState(), 3);
            }
            return InteractionResult.PASS;
        });

        ServerTickEvents.START_WORLD_TICK.register((world) -> {
            ContainerState.get(world).refreshDirty(world);
        });
    }

    public static void registerClientEvents() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            float baseFrameTime = Minecraft.getInstance().getDeltaFrameTime();
            float speedFactor = 2f;
            var result = AnimationWorker.increaseTimer(baseFrameTime * speedFactor);
            if (result >= 20) AnimationWorker.resetTimer();
        });
    }

    // We use mixin to solve our problems.

    public static void onPlayerHurt(Player player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (player.level().isClientSide) return;
        Entity attacker = source.getEntity();
        if (PlayerUtil.hasSword(player)) {
            var damageSource = player.level().damageSources().magic();
            // reflect the damage to the attacker
            if (attacker != null && !PlayerUtil.hasSword(attacker)) attacker.hurt(damageSource, Float.MAX_VALUE);
            // player will get no damage
            cir.setReturnValue(false);
        }
    }

    public static void onLivingEntityHurt(LivingEntity receiver, DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        var attacker = damageSource.getEntity();
        if (attacker instanceof Player playerAttacker) {
            if (PlayerUtil.hasSword(playerAttacker)) {
                if (damageSource.isIndirect() || ((Player) attacker).getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemSwordWhiter) {
                    // Use hurt trigger to get real effect (use magic damage to prevent recursion).
                    receiver.hurt(receiver.level().damageSources().magic(), Float.MAX_VALUE);
                }
            }
        }
    }

    public static void onHarvestCheck(Player player, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerUtil.hasSword(player)) cir.setReturnValue(true);
        else if (state.getBlock().equals(RegisterHandler.BEDROCK) && !PlayerUtil.hasSword(player)) {
            cir.setReturnValue(false);
        }
    }
}
