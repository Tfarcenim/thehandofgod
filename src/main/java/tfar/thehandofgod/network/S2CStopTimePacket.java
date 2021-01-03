package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.HandoOfGodData;


public class S2CStopTimePacket implements IMessage {


  public S2CStopTimePacket(){}

  /**
   * Convert from the supplied buffer into your specific message type
   *
   * @param buf
   */
  @Override
  public void fromBytes(ByteBuf buf) {

  }

  /**
   * Deconstruct your message into the supplied byte buffer
   *
   * @param buf
   */
  @Override
  public void toBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<S2CStopTimePacket, IMessage> {
    @Override
    public IMessage onMessage(S2CStopTimePacket message, MessageContext ctx) {
      FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
      return null;
    }

    private void handle(S2CStopTimePacket message, MessageContext ctx) {
      EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
      WorldServer serverWorld = (WorldServer) ctx.getServerHandler().player.world;
      HandoOfGodData.getDefaultInstance(serverWorld).toggle(serverWorld);
    }
  }
}

