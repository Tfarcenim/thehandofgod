package tfar.thehandofgod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.SearchTreeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.thehandofgod.client.Client;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow private SearchTreeManager searchTreeManager;

    @Inject(method = "populateSearchTreeManager",at = @At("RETURN"))
    private void registerSearch(CallbackInfo ci) {
        Client.registerTrees(this.searchTreeManager);
    }
}
