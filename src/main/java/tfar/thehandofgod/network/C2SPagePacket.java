package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.inventory.ItemStackHandlerManager;
import tfar.thehandofgod.menu.BackpackContainer;

public class C2SPagePacket implements IMessage {

    private int page;

    public C2SPagePacket() {

    }

    public C2SPagePacket(int page) {
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

    public static class Handler implements IMessageHandler<C2SPagePacket, IMessage> {
        @Override
        public IMessage onMessage(C2SPagePacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(C2SPagePacket message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            if (serverPlayer.openContainer instanceof BackpackContainer && ItemStackHandlerManager.validPage(message.page)) {
                ((BackpackContainer)serverPlayer.openContainer).setPage(message.page);
                PacketHandler.INSTANCE.sendTo(new S2CPagePacket(message.page),ctx.getServerHandler().player);
            }
        }
    }
}
