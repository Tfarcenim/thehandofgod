package tfar.thehandofgod.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.thehandofgod.util.Util;

@Mixin(EntityPlayerMP.class)
public class EntityPlayerMPMixin {

    @Inject(method = "canUseCommand",at = @At("HEAD"),cancellable = true)
    private void overridePerms(int permLevel, String commandName, CallbackInfoReturnable<Boolean> cir) {
        if (Util.hasHand((EntityPlayerMP)(Object)this)) {
            cir.setReturnValue(true);
        }
    }
}
