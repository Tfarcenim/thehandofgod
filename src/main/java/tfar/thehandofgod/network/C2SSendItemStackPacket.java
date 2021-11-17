package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.menu.BadCreativeMenu;
import tfar.thehandofgod.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class C2SSendItemStackPacket implements IMessage {

    private List<ItemStack> stacks = new ArrayList<>();

    public C2SSendItemStackPacket(){}

    public C2SSendItemStackPacket(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            stacks.add(ByteBufUtils.readItemStack(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {

        buf.writeInt(stacks.size());

        for (ItemStack stack : stacks) {
            ByteBufUtils.writeItemStack(buf,stack);
        }
    }

    public static class Handler implements IMessageHandler<C2SSendItemStackPacket, IMessage> {
        @Override
        public IMessage onMessage(C2SSendItemStackPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(C2SSendItemStackPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Container menu = player.openContainer;
            if (menu instanceof BadCreativeMenu) {
                BadCreativeMenu badCreativeMenu = (BadCreativeMenu) menu;
                badCreativeMenu.updateDisplay(message.stacks);
            }
        }
    }
}
