package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.client.gui.BackpackScreen;
import tfar.thehandofgod.menu.BackpackContainer;

//todo, remove in 1.13+
public class S2CPagePacket implements IMessage {

    private int page;

    public S2CPagePacket() {

    }

    public S2CPagePacket(int page) {
        this.page = page;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        page = buf.readInt();
    }


    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(page);
    }

    public static class Handler implements IMessageHandler<S2CPagePacket, IMessage> {
        @Override
        public IMessage onMessage(S2CPagePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(S2CPagePacket message, MessageContext ctx) {
            if (Minecraft.getMinecraft().player.openContainer instanceof BackpackContainer) {
                ((BackpackContainer)Minecraft.getMinecraft().player.openContainer).setPage(message.page);
            }
        }
    }
}
