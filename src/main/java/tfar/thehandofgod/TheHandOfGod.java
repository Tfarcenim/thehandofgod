package tfar.thehandofgod;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.network.S2CStopTimePacket;

@Mod(modid = TheHandOfGod.MODID, name = TheHandOfGod.NAME, version = TheHandOfGod.VERSION)
@Mod.EventBusSubscriber
public class TheHandOfGod {
    public static final String MODID = "thehandofgod";
    public static final String NAME = "The Hand of God";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.registerMessages(MODID);
    }

    @SubscribeEvent
    public static void sounds(RegistryEvent.Register<SoundEvent> e) {
        ModSounds.register(e.getRegistry());
    }

    @SubscribeEvent
    public static void neighbor(BlockEvent.NeighborNotifyEvent e) {
        if (!e.getWorld().isRemote) {
            boolean stopped = HandoOfGodData.getDefaultInstance((WorldServer)e.getWorld()).stopped;
            if (stopped) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        World world = e.player.world;
        HandoOfGodData handoOfGodData = HandoOfGodData.getDefaultInstance((WorldServer)world);
        if (handoOfGodData.stopped) {
            PacketHandler.INSTANCE.sendTo(new S2CStopTimePacket(true, e.player.getGameProfile().getId().equals(handoOfGodData.user)), (EntityPlayerMP) e.player);
        }
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinWorldEvent e) {
        if (!e.getWorld().isRemote) {
            boolean stopped = HandoOfGodData.getDefaultInstance((WorldServer)e.getWorld()).stopped;
            if (stopped) {
                e.getEntity().updateBlocked = true;
            }
        }
    }
}
