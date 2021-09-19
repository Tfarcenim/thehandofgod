package tfar.thehandofgod.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.thehandofgod.HandOfGodConfig;

@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerMixin {

    @Shadow public EntityPlayerMP player;

    @Redirect(method = "processClientStatus",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/MinecraftServer;isHardcore()Z"))
    private boolean redirectHardcore(MinecraftServer server) {
        EntityPlayer player = this.player;
        String uuidString = player.getGameProfile().getId().toString();
        for (String s : HandOfGodConfig.beyond_redemption_player_list) {
            if (s.equals(uuidString)) {
                return true;
            }
        }
        return server.isHardcore();
    }
}
