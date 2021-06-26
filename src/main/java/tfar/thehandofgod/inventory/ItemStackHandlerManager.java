package tfar.thehandofgod.inventory;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.TheHandOfGod;

import java.util.ArrayList;
import java.util.List;

public class ItemStackHandlerManager {

    public static final int MAX_PAGES = 10000;
    private final List<ItemStackHandler> itemStackHandlers = new ArrayList<>();
    private final World world;

    public ItemStackHandlerManager(World world) {
        this.world = world;
    }

    public ItemStackHandler getOrCreateHandlerForPage(int page) {
        if (page < 0 || page >= MAX_PAGES) {
            TheHandOfGod.logger.warn("Invalid page selected: {}",page);
            return null;
        }

        if (page >= itemStackHandlers.size()) {
            while ((itemStackHandlers.size() <= page)) {
                itemStackHandlers.add(createEmptyHandler());
            }
        }
        return itemStackHandlers.get(page);
    }


    //save all itemstackhandlers and their contents in an array
    public NBTTagList save() {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < itemStackHandlers.size(); i++) {
            ItemStackHandler handler = itemStackHandlers.get(i);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("page",i);
            compound.setTag("contents",handler.serializeNBT());
            list.appendTag(compound);
        }
        return list;
    }

    public void load(NBTTagList tag) {
        itemStackHandlers.clear();
        for (NBTBase nbtBase : tag) {
            NBTTagCompound compound = (NBTTagCompound)nbtBase;
            ItemStackHandler handler = createEmptyHandler();
            handler.deserializeNBT(compound.getCompoundTag("contents"));
            int page = compound.getInteger("page");
            itemStackHandlers.add(page,handler);
        }
    }


    public ItemStackHandler createEmptyHandler() {
        return new AutoSaveItemStackHandler(54,world);
    }
}
