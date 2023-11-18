package turou.fantasy_metropolis.fabric.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.NetworkHandler;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.isShiftKeyDown() && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemSwordWhiter) {
            FriendlyByteBuf byteBuf = PacketByteBufs.create();
            byteBuf.writeInt((int) yOffset);
            ClientPlayNetworking.send(NetworkHandler.SCROLL_SWORD_PACKET, byteBuf);
            ci.cancel();
        }
    }
}
