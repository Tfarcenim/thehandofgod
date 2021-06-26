package tfar.thehandofgod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.client.gui.BackpackScreen;
import tfar.thehandofgod.world.saveddata.BackpackData;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BackpackData data = BackpackData.getDefaultInstance((WorldServer) world);
        ItemStackHandler handler = data.getOrCreateManagerForPlayer((EntityPlayerMP) player).getOrCreateHandlerForPage(0);
        return createMenu(player.inventory,handler,player);
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new BackpackScreen(createMenu(player.inventory,new ItemStackHandler(54),player));
    }

    private static BackpackContainer createMenu(InventoryPlayer inv,ItemStackHandler handler,EntityPlayer player) {
        return new BackpackContainer(inv,handler,player);
    }
}
