package tfar.thehandofgod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public World world;

    @Inject(method = "isInvisible",at = @At("RETURN"),cancellable = true)
    private void sye(CallbackInfoReturnable<Boolean> cir) {
     //   if (this.world.isRemote && HandOfGodConfig.true_vision && Util.hasHand(Minecraft.getMinecraft().player)) {
   //         cir.setReturnValue(false);
    //    }
    }
}
