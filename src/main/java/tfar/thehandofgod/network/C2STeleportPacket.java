package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.server.command.CommandSetDimension;
import tfar.thehandofgod.HandoOfGodData;
import tfar.thehandofgod.util.CrossDimTeleporter;

public class C2STeleportPacket implements IMessage {

    private BlockPos pos;
    private int dimension;

    public C2STeleportPacket() {

    }

    public C2STeleportPacket(BlockPos pos, int dim) {
        this.pos = pos;
        dimension = dim;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
        dimension = buf.readInt();
    }


    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(dimension);
    }

    public static class Handler implements IMessageHandler<C2STeleportPacket, IMessage> {
        @Override
        public IMessage onMessage(C2STeleportPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(C2STeleportPacket message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.changeDimension(message.dimension, new CrossDimTeleporter(message.pos));
        }
    }
}
