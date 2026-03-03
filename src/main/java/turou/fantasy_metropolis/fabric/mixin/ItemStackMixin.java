package turou.fantasy_metropolis.fabric.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import turou.fantasy_metropolis.fabric.item.ItemSwordWhiter;

@Mixin(value = ItemStack.class, priority = 1001)
public class ItemStackMixin {
    @Shadow


    @Inject(method = "isDamaged()Z", at = @At(value = "HEAD"), cancellable = true)
    public void isDamaged(CallbackInfoReturnable<Boolean> cir) {
        ItemStack current = (ItemStack) (Object) this;
        if (current.getItem() instanceof ItemSwordWhiter)
            cir.setReturnValue(false);
    }

    @Inject(method = "getDamageValue()I", at = @At(value = "HEAD"), cancellable = true)
    public void getDamageValue(CallbackInfoReturnable<Integer> cir) {
        ItemStack current = (ItemStack) (Object) this;
        if (current.getItem() instanceof ItemSwordWhiter)
            cir.setReturnValue(0);
    }

    @Inject(method = "setDamageValue(I)V", at = @At(value = "HEAD"))
    public void setDamageValue(int damage, CallbackInfo ci) {
        ItemStack current = (ItemStack) (Object) this;
        if (current.getItem() instanceof ItemSwordWhiter) {
            current.set(DataComponents.DAMAGE, 0);
        }
    }
}
