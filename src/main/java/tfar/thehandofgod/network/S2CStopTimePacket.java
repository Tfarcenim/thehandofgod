package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.client.Client;

public class S2CStopTimePacket implements IMessage {

    boolean stopped;
    boolean user;
    public S2CStopTimePacket(){}

    public S2CStopTimePacket(boolean stopped,boolean user) {
        this.stopped = stopped;
        this.user = user;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        stopped = buf.readBoolean();
        user = buf.readBoolean();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(stopped);
        buf.writeBoolean(user);
    }

    public static class Handler implements IMessageHandler<S2CStopTimePacket, IMessage> {
        @Override
        public IMessage onMessage(S2CStopTimePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(S2CStopTimePacket message, MessageContext ctx) {
            Client.onTimeToggle(message.stopped,message.user);
        }
    }
}
