package tfar.thehandofgod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
  public static SimpleNetworkWrapper INSTANCE;

  public static void registerMessages(String channelName) {
    int i = 0;
    INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
    INSTANCE.registerMessage(C2SStopTimePacket.Handler.class, C2SStopTimePacket.class, i++, Side.SERVER);
    INSTANCE.registerMessage(S2CStopTimePacket.Handler.class, S2CStopTimePacket.class, i++, Side.CLIENT);

    INSTANCE.registerMessage(C2SOpenGuiFromKeybindPacket.Handler.class, C2SOpenGuiFromKeybindPacket.class, i++, Side.SERVER);
    INSTANCE.registerMessage(C2SPagePacket.Handler.class, C2SPagePacket.class, i++, Side.SERVER);
    INSTANCE.registerMessage(S2CPagePacket.Handler.class, S2CPagePacket.class, i++, Side.CLIENT);
    INSTANCE.registerMessage(C2STeleportPacket.Handler.class,C2STeleportPacket.class,i++,Side.SERVER);
    INSTANCE.registerMessage(C2SSendItemStackPacket.Handler.class,C2SSendItemStackPacket.class,i++,Side.SERVER);
  }
}
