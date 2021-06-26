package tfar.thehandofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.network.S2CStopTimePacket;

import java.util.UUID;

public class HandoOfGodData extends WorldSavedData {

    public boolean stopped;
    public int oldTickSpeed;
    //the user who activated time stop
    public UUID user;

    //this is called via reflection, do not remove
    public HandoOfGodData(String name) {
        super(name);
    }

    public static HandoOfGodData getDefaultInstance(WorldServer world) {
        return get(world.getMinecraftServer().getWorld(0));
    }

    private static HandoOfGodData get(WorldServer world) {
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
        user = nbt.getUniqueId("user");
        oldTickSpeed = nbt.getInteger("oldTickSpeed");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("stopped",stopped);
        compound.setUniqueId("user",user);
        compound.setInteger("oldTickSpeed",oldTickSpeed);
        return compound;
    }

    public void toggle(WorldServer serverWorld, EntityPlayerMP serverPlayer) {
        this.stopped = !stopped;
        if (stopped) {
            onStopped(serverWorld,serverPlayer);
        } else {
            onResume(serverWorld);
        }
        markDirty();
    }

    public void onStopped(WorldServer serverWorld, EntityPlayerMP serverPlayer) {
        oldTickSpeed = serverWorld.getGameRules().getInt("randomTickSpeed");
        user = serverPlayer.getGameProfile().getId();
        serverWorld.getGameRules().setOrCreateGameRule("randomTickSpeed","0");
        serverWorld.getGameRules().setOrCreateGameRule("doDaylightCycle","false");

        for (Entity entity : serverWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                PacketHandler.INSTANCE.sendTo(new S2CStopTimePacket(true,((EntityPlayer)entity).getGameProfile().getId().equals(user)),(EntityPlayerMP)entity);
            }
            entity.updateBlocked = true;
        }
     }

    public void onResume(WorldServer serverWorld) {
        serverWorld.getGameRules().setOrCreateGameRule("randomTickSpeed",String.valueOf(oldTickSpeed));
        serverWorld.getGameRules().setOrCreateGameRule("doDaylightCycle","true");

        for (Entity entity : serverWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                PacketHandler.INSTANCE.sendTo(new S2CStopTimePacket(false,false),(EntityPlayerMP)entity);
            }
            entity.updateBlocked = false;
        }
    }
}
