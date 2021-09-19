package tfar.thehandofgod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.client.gui.BackpackScreen;
import tfar.thehandofgod.client.gui.ConfigureEnchantmentScreen;
import tfar.thehandofgod.client.gui.ConfigurePotionScreen;
import tfar.thehandofgod.inventory.BiggerItemStackHandler;
import tfar.thehandofgod.inventory.EnchantmentItemStackHandler;
import tfar.thehandofgod.inventory.PotionItemStackHandler;
import tfar.thehandofgod.menu.BackpackContainer;
import tfar.thehandofgod.menu.ConfigureEnchantmentContainer;
import tfar.thehandofgod.menu.ConfigurePotionContainer;
import tfar.thehandofgod.util.Constants;
import tfar.thehandofgod.util.Util;
import tfar.thehandofgod.world.saveddata.BackpackData;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Constants.ScreenType screenType = Constants.ScreenType.values()[ID];
        switch (screenType) {
            case BACKPACK: {
                BackpackData data = BackpackData.getDefaultInstance((WorldServer) world);
                BiggerItemStackHandler handler = data.getOrCreateManagerForPlayer((EntityPlayerMP) player).getOrCreateHandlerForPage(0);
                return createBackpackMenu(player.inventory,handler);
            }
            case ENCHANTMENTS: {
                ItemStackHandler handler = Util.getEnchantmentHandler(player.getHeldItemMainhand());
                return createEnchantmentMenu(player.inventory, handler);
            }
            case POTIONS:{
                ItemStackHandler handler = Util.getPotionHandler(player.getHeldItemMainhand());
                return createPotionMenu(player.inventory,handler);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Constants.ScreenType screenType = Constants.ScreenType.values()[ID];
        switch (screenType) {
            case BACKPACK:
                return new BackpackScreen(createBackpackMenu(player.inventory, new BiggerItemStackHandler(54)));
            case ENCHANTMENTS:
                return new ConfigureEnchantmentScreen(createEnchantmentMenu(player.inventory,new EnchantmentItemStackHandler(9)));
            case POTIONS:
                return new ConfigurePotionScreen(createPotionMenu(player.inventory,new PotionItemStackHandler(9)));
        }
        return null;
    }

    private static BackpackContainer createBackpackMenu(InventoryPlayer inv, BiggerItemStackHandler handler) {
        return new BackpackContainer(inv,handler);
    }

    private static ConfigureEnchantmentContainer createEnchantmentMenu(InventoryPlayer inv, ItemStackHandler handler) {
        ItemStack stack = inv.player.getHeldItemMainhand();
        return new ConfigureEnchantmentContainer(inv,handler,stack);
    }

    private static ConfigurePotionContainer createPotionMenu(InventoryPlayer inv, ItemStackHandler handler) {
        ItemStack stack = inv.player.getHeldItemMainhand();
        return new ConfigurePotionContainer(inv,handler,stack);
    }

}
