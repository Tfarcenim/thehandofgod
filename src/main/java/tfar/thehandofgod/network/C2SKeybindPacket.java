package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.util.Constants;

public class C2SKeybindPacket implements IMessage {

    private Constants.KeybindType type;

    public C2SKeybindPacket(){}

    public C2SKeybindPacket(Constants.KeybindType type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Constants.KeybindType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    public static class Handler implements IMessageHandler<C2SKeybindPacket, IMessage> {
        @Override
        public IMessage onMessage(C2SKeybindPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(C2SKeybindPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            switch (message.type) {
                case BACKPACK:
                    player.openGui(TheHandOfGod.INSTANCE, 0, player.world,0, 0, 0);
                    break;
            }


        }
    }

}
