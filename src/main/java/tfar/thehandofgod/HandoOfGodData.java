package tfar.thehandofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.network.S2CLoadShaderPacket;

public class HandoOfGodData extends WorldSavedData {

    public boolean stopped;
    public int oldTickSpeed;

    //this is called via reflection, do not remove
    public HandoOfGodData(String name) {
        super(name);
    }

    public static HandoOfGodData getDefaultInstance(WorldServer world) {
        return get(world.getMinecraftServer().getWorld(0));
    }

    public static HandoOfGodData get(WorldServer world) {
        MapStorage storage = world.getPerWorldStorage();
        String name = TheHandOfGod.MODID+":"+world.provider.getDimension();
        HandoOfGodData instance = (HandoOfGodData) storage.getOrLoadData(HandoOfGodData.class, name);

        if (instance == null) {
            HandoOfGodData wsd = new HandoOfGodData(name);
            storage.setData(name, wsd);
            instance = (HandoOfGodData) storage.getOrLoadData(HandoOfGodData.class, name);
        }
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        stopped = nbt.getBoolean("stopped");
        oldTickSpeed = nbt.getInteger("oldTickSpeed");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("stopped",stopped);
        compound.setInteger("oldTickSpeed",oldTickSpeed);
        return compound;
    }

    public void toggle(WorldServer serverWorld) {
        this.stopped = !stopped;
        if (stopped) {
            onStopped(serverWorld);
        } else {
            onResume(serverWorld);
        }
        markDirty();
    }

    public void onStopped(WorldServer serverWorld) {
        oldTickSpeed = serverWorld.getGameRules().getInt("randomTickSpeed");
        serverWorld.getGameRules().setOrCreateGameRule("randomTickSpeed","0");
        serverWorld.getGameRules().setOrCreateGameRule("doDaylightCycle","false");

        for (Entity entity : serverWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                PacketHandler.INSTANCE.sendTo(new S2CLoadShaderPacket(false),(EntityPlayerMP)entity);
            }
            entity.updateBlocked = true;
        }
     }

    public void onResume(WorldServer serverWorld) {
        serverWorld.getGameRules().setOrCreateGameRule("randomTickSpeed",String.valueOf(oldTickSpeed));
        serverWorld.getGameRules().setOrCreateGameRule("doDaylightCycle","true");

        for (Entity entity : serverWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                PacketHandler.INSTANCE.sendTo(new S2CLoadShaderPacket(true),(EntityPlayerMP)entity);
            }
            entity.updateBlocked = false;
        }
    }
}
