package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turou.fantasy_metropolis.fabric.client.SwordExecutionPostEffectRenderer;

@Mixin(GameRenderer.class)
public class SwordExecutionGameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = At.Shift.AFTER))
    private void fantasyMetropolis$renderSwordExecutionPostEffect(DeltaTracker deltaTracker, boolean tick, CallbackInfo ci) {
        SwordExecutionPostEffectRenderer.render(deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}
