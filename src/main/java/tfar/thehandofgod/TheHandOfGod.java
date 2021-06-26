package tfar.thehandofgod;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.thehandofgod.client.ModKeybinds;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.network.S2CStopTimePacket;
import tfar.thehandofgod.util.Util;

@Mod(modid = TheHandOfGod.MODID, name = TheHandOfGod.NAME, version = TheHandOfGod.VERSION)
@Mod.EventBusSubscriber
public class TheHandOfGod {
    public static final String MODID = "thehandofgod";
    public static final String NAME = "The Hand of God";
    public static final String VERSION = "1.0";

    public static Logger logger = LogManager.getLogger();

    public static TheHandOfGod INSTANCE;

    public TheHandOfGod() {
        INSTANCE = this;
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this,new GuiHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.registerMessages(MODID);
        HandOfGodConfig.parseConfigs();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            ModKeybinds.clientSetup();
        }
    }

    @SubscribeEvent
    public static void attack(LivingAttackEvent e) {
        EntityLivingBase victim = e.getEntityLiving();
        if (Util.hasHand(victim)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void items(RegistryEvent.Register<Item> e) {
        ModItems.register(e.getRegistry());
    }

    @SubscribeEvent
    public static void sounds(RegistryEvent.Register<SoundEvent> e) {
        ModSounds.register(e.getRegistry());
    }

    @SubscribeEvent
    public static void configs(ConfigChangedEvent.OnConfigChangedEvent e) {
        HandOfGodConfig.parseConfigs();
    }

    @SubscribeEvent
    public static void potionFilter(PotionEvent.PotionApplicableEvent e) {
        if (Util.hasHand(e.getEntityLiving()) && !HandOfGodConfig.allowed.contains(e.getPotionEffect().getPotion())) {
            e.setResult(Event.Result.DENY);
        }
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

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START && !e.player.world.isRemote) {
            for (ItemStack stack : e.player.inventory.mainInventory) {
                if (stack.getItem() instanceof HandOfGodItem) {
                    if (!stack.hasTagCompound() || !stack.getTagCompound().hasUniqueId("owner")) {
                        if (!stack.hasTagCompound()) {
                            stack.setTagCompound(new NBTTagCompound());
                        }
                        stack.getTagCompound().setUniqueId("owner",e.player.getGameProfile().getId());
                        stack.getTagCompound().setString("owner_name",e.player.getDisplayNameString());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void pickup(EntityItemPickupEvent e) {
        ItemStack stack = e.getItem().getItem();
        if (stack.getItem() instanceof HandOfGodItem && stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("owner")) {
            boolean isOwner = e.getEntityPlayer().getGameProfile().getId().equals(stack.getTagCompound().getUniqueId("owner"));
            if (!isOwner) {
                e.setCanceled(true);
            }
        }
    }
}
