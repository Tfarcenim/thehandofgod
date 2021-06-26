package tfar.thehandofgod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
  public static SimpleNetworkWrapper INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
    INSTANCE.registerMessage(C2SStopTimePacket.Handler.class, C2SStopTimePacket.class, 0, Side.SERVER);
    INSTANCE.registerMessage(S2CStopTimePacket.Handler.class, S2CStopTimePacket.class, 1, Side.CLIENT);

    INSTANCE.registerMessage(C2SKeybindPacket.Handler.class, C2SKeybindPacket.class, 2, Side.SERVER);

  }
}
