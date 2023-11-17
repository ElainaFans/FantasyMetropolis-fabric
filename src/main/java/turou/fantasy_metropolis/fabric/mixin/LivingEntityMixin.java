package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.EventHandler;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "actuallyHurt", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onLivingEntityHurt(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        EventHandler.onLivingEntityHurt(livingEntity, damageSource, damageAmount, ci);
    }
}
