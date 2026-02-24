package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import turou.fantasy_metropolis.fabric.EventHandler;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract void setHealth(float p_21154_);

    @Inject(method = "actuallyHurt", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onLivingEntityHurt(ServerLevel serverLevel, DamageSource damageSource, float damageAmount,
            CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        EventHandler.onLivingEntityHurt(livingEntity, damageSource, damageAmount, ci);
    }

    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Inject(method = "isAlive", at = @At("HEAD"), cancellable = true)
    public void isAlive(CallbackInfoReturnable<Boolean> cir) {
        if (Float.isNaN(getHealth()))
            cir.setReturnValue(false);
    }

    @Inject(method = "isDeadOrDying", at = @At("HEAD"), cancellable = true)
    public void isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        if (Float.isNaN(getHealth()))
            cir.setReturnValue(true);
    }

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    public void GetHealth(CallbackInfoReturnable<Float> cir) {
        if (Float.isNaN(entityData.get(DATA_HEALTH_ID)))
            cir.setReturnValue(0.0F);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (Float.isNaN(getHealth()))
            setHealth(0.0F);
    }
}
