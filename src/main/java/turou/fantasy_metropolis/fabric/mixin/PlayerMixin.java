package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.EventHandler;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "hurt", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        EventHandler.onPlayerHurt(player, source, amount, cir);
    }

    @Inject(method = "hasCorrectToolForDrops", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void harvestCheck(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        EventHandler.onHarvestCheck(player, state, cir);}
}
