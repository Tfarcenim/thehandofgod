package tfar.thehandofgod.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.thehandofgod.HandOfGodItem;

@Mixin(Slot.class)
public abstract class PlayerInventoryMixin {

    @Shadow public abstract ItemStack getStack();

    @Inject(method = "canTakeStack",at = @At("RETURN"),cancellable = true)
    private void handCheck(EntityPlayer playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || playerIn.capabilities.isCreativeMode) return;
        cir.setReturnValue(!(getStack().getItem() instanceof HandOfGodItem));
    }
}
