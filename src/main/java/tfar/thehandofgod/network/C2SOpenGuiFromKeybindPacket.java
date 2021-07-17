package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.util.Constants;

public class C2SOpenGuiFromKeybindPacket implements IMessage {

    private Constants.ScreenType type;

    public C2SOpenGuiFromKeybindPacket(){}

    public C2SOpenGuiFromKeybindPacket(Constants.ScreenType type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Constants.ScreenType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    public static class Handler implements IMessageHandler<C2SOpenGuiFromKeybindPacket, IMessage> {
        @Override
        public IMessage onMessage(C2SOpenGuiFromKeybindPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(C2SOpenGuiFromKeybindPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.openGui(TheHandOfGod.INSTANCE,message.type.ordinal(), player.world,0, 0, 0);
        }
    }
}
