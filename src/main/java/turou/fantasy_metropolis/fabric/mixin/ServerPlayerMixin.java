package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "restoreFrom", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void restoreFrom(ServerPlayer pThat, boolean keepEverything, CallbackInfo ci) {
        ServerPlayer pThis = (ServerPlayer) (Object) this;
        ItemStack thatStack = pThat.getInventory().getItem(41);
        pThis.getInventory().setItem(41, thatStack);
    }
}
