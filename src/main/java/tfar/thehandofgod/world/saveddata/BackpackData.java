package tfar.thehandofgod.world.saveddata;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.inventory.ItemStackHandlerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackData extends WorldSavedData {


    //each player has 10000 pages of inventory
    private final Map<UUID, ItemStackHandlerManager> storage = new HashMap<>();

    private World world;

    public BackpackData(String name) {
        super(name);
    }

    public static BackpackData getDefaultInstance(WorldServer world) {
        return get(world.getMinecraftServer().getWorld(0));
    }

    public ItemStackHandlerManager getOrCreateManagerForPlayer(EntityPlayerMP player) {
        return storage.computeIfAbsent(player.getGameProfile().getId(),uuid -> new ItemStackHandlerManager(player.world));
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

        instance.world = world;
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        storage.clear();
        NBTTagList list = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
        for (NBTBase nbtBase : list) {
            NBTTagCompound compound = (NBTTagCompound)nbtBase;
            UUID uuid = compound.getUniqueId("uuid");
            NBTTagList tagList = compound.getTagList("ItemStackHandlers", Constants.NBT.TAG_COMPOUND);
            ItemStackHandlerManager manager = new ItemStackHandlerManager(world);
            manager.load(tagList);
            storage.put(uuid,manager);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID,ItemStackHandlerManager> listEntry : storage.entrySet()) {
            NBTTagCompound c1 = new NBTTagCompound();
            c1.setUniqueId("uuid",listEntry.getKey());
            ItemStackHandlerManager manager = listEntry.getValue();

            c1.setTag("ItemStackHandlers",manager.save());
            list.appendTag(c1);
        }
        compound.setTag("data",list);
        return compound;
    }
}
