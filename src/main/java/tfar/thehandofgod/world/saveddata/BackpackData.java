package tfar.thehandofgod.world.saveddata;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import tfar.thehandofgod.TheHandOfGod;

import java.util.*;

public class BackpackData extends WorldSavedData {

    public static final int PAGES = 10000;

    private final Map<UUID, List<ItemStack>> storage = new HashMap<>();

    public BackpackData(String name) {
        super(name);
    }

    public static BackpackData getDefaultInstance(WorldServer world) {
        return get(world.getMinecraftServer().getWorld(0));
    }

    public static BackpackData get(WorldServer world) {
        MapStorage storage = world.getPerWorldStorage();
        String name = TheHandOfGod.MODID+":backpack_"+world.provider.getDimension();
        BackpackData instance = (BackpackData) storage.getOrLoadData(BackpackData.class, name);

        if (instance == null) {
            BackpackData wsd = new BackpackData(name);
            storage.setData(name, wsd);
            instance = (BackpackData) storage.getOrLoadData(BackpackData.class, name);
        }
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
        for (NBTBase nbtBase : list) {
            NBTTagCompound compound = (NBTTagCompound)nbtBase;
            UUID uuid = compound.getUniqueId("uuid");
            NBTTagList tagList = compound.getTagList("items",Constants.NBT.TAG_COMPOUND);
            List<ItemStack> stackList =  new ArrayList<>();
            for (NBTBase nbtBase1 : tagList) {
                NBTTagCompound compound1 = (NBTTagCompound)nbtBase1;
                int slot =
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID,List<ItemStack>> listEntry : storage.entrySet()) {
            NBTTagCompound c1 = new NBTTagCompound();
            c1.setUniqueId("uuid",listEntry.getKey());
            NBTTagList list1 = new NBTTagList();
            List<ItemStack> items = listEntry.getValue();
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    stack.writeToNBT(itemTag);
                    itemTag.setInteger("BigCount",stack.getCount());
                    list1.appendTag(itemTag);
                }
            }
            c1.setTag("items",list1);
            list.appendTag(c1);
        }
        compound.setTag("data",list);
        return compound;
    }
}
