package tfar.thehandofgod.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.TheHandOfGod;

import java.util.ArrayList;
import java.util.List;

public class ItemStackHandlerManager {

    public static final int MAX_PAGES = 10000;
    public final List<NonNullList<ItemStack>> stacksStacks = new ArrayList<>(MAX_PAGES);
    private final World world;

    public ItemStackHandlerManager(World world) {
        this.world = world;
    }

    public ItemStackHandler getOrCreateHandlerForPage(int page) {
        if (!validPage(page)) {
            TheHandOfGod.logger.warn("Invalid page selected: {}", page);
            return null;
        }

        if (page >= stacksStacks.size()) {
            while ((stacksStacks.size() <= page)) {
                stacksStacks.add(NonNullList.withSize(54, ItemStack.EMPTY));
            }
        }
        return new AutoSaveItemStackHandler(54, world, page, this);
    }

    public NBTTagList save() {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < stacksStacks.size(); i++) {
            NonNullList<ItemStack> handler = stacksStacks.get(i);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("page", i);
            compound.setTag("contents", serializeItems(handler));
            list.appendTag(compound);
        }
        return list;
    }

    public static NBTTagCompound serializeItems(NonNullList<ItemStack> stacks) {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                stacks.get(i).writeToNBT(itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", stacks.size());
        return nbt;
    }

    public void load(NBTTagList tag) {
        stacksStacks.clear();
        for (NBTBase nbtBase : tag) {
            NBTTagCompound compound = (NBTTagCompound) nbtBase;
            NonNullList<ItemStack> handler = deserializeItems(compound.getCompoundTag("contents"));
            int page = compound.getInteger("page");
            stacksStacks.add(page, handler);
        }
    }

    public static NonNullList<ItemStack> deserializeItems(NBTTagCompound nbt) {
        NonNullList<ItemStack> handler = NonNullList.withSize(54, ItemStack.EMPTY);
        int size = nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : 54;
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            if (slot >= 0 && slot < size) {
                ItemStack stack = new ItemStack(itemTags);
                handler.set(slot, stack);
            }
        }
        return handler;
    }

    public void addPage() {
        stacksStacks.add(NonNullList.withSize(54, ItemStack.EMPTY));
    }

    public static boolean validPage(int page) {
        return page >= 0 && page < MAX_PAGES;
    }
}
