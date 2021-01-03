package tfar.thehandofgod;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import tfar.thehandofgod.network.PacketHandler;

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
    public static void neighbor(BlockEvent.NeighborNotifyEvent e) {
        if (!e.getWorld().isRemote) {
            boolean stopped = HandoOfGodData.getDefaultInstance((WorldServer)e.getWorld()).stopped;
            if (stopped) {
                e.setCanceled(true);
            }
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
