package tfar.thehandofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.thehandofgod.client.Client;
import tfar.thehandofgod.client.ModKeybinds;
import tfar.thehandofgod.init.ModEntityTypes;
import tfar.thehandofgod.init.ModItems;
import tfar.thehandofgod.init.ModSounds;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.network.S2CStopTimePacket;
import tfar.thehandofgod.util.Util;

import java.util.List;
import java.util.UUID;

@Mod(modid = TheHandOfGod.MODID, name = TheHandOfGod.NAME, version = TheHandOfGod.VERSION)
@Mod.EventBusSubscriber
public class TheHandOfGod {
    public static final String MODID = "thehandofgod";
    public static final String NAME = "The Hand of God";
    public static final String VERSION = "1.0";

    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870762");

    public static Logger logger = LogManager.getLogger();

    public static TheHandOfGod INSTANCE;

    public TheHandOfGod() {
        INSTANCE = this;
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        if (FMLCommonHandler.instance().getSide().isClient()) {
            Client.preInit(event);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.registerMessages(MODID);
        HandOfGodConfig.parseConfigs(false);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            ModKeybinds.register();
            Client.init(event);
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
    public static void entity(RegistryEvent.Register<EntityEntry> e) {
        ModEntityTypes.register(e.getRegistry());
    }

    @SubscribeEvent
    public static void configs(ConfigChangedEvent.OnConfigChangedEvent e) {
        HandOfGodConfig.parseConfigs(e.isWorldRunning());
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
            boolean stopped = HandoOfGodData.getDefaultInstance((WorldServer) e.getWorld()).stopped;
            if (stopped) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        EntityPlayer player = e.player;
        World world = player.world;
        HandoOfGodData handoOfGodData = HandoOfGodData.getDefaultInstance((WorldServer) world);
        if (handoOfGodData.stopped) {
            PacketHandler.INSTANCE.sendTo(new S2CStopTimePacket(true, player.getGameProfile().getId().equals(handoOfGodData.user)), (EntityPlayerMP) player);
        }

        if (handoOfGodData.newToWorld(player) && HandOfGodConfig.omnipresence) {
            player.addItemStackToInventory(new ItemStack(ModItems.HAND_OF_GOD));
            handoOfGodData.addNewPlayer(player);
        }
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinWorldEvent e) {
        if (!e.getWorld().isRemote) {
            boolean stopped = HandoOfGodData.getDefaultInstance((WorldServer) e.getWorld()).stopped;
            if (stopped) {
                e.getEntity().updateBlocked = true;
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent e) {
        EntityPlayer player = e.player;
        if (e.phase == TickEvent.Phase.START) {
            if (!e.player.world.isRemote) {
                for (ItemStack stack : e.player.inventory.mainInventory) {
                    if (stack.getItem() instanceof HandOfGodItem) {
                        if (!stack.hasTagCompound() || !stack.getTagCompound().hasUniqueId("owner")) {
                            if (!stack.hasTagCompound()) {
                                stack.setTagCompound(new NBTTagCompound());
                            }
                            stack.getTagCompound().setUniqueId("owner", e.player.getGameProfile().getId());
                            stack.getTagCompound().setString("owner_name", e.player.getDisplayNameString());
                        }
                    }
                }
            }

            if (Util.hasHand(e.player)) {
                e.player.capabilities.allowFlying = true;
                e.player.setInvisible(HandOfGodConfig.true_invisibility);
                if (e.player.world.isRemote) {
                    //caution, this is clientside only
                    e.player.capabilities.setFlySpeed((float) (HandOfGodConfig.flight_speed * .05));
                } else {


                    if (HandOfGodConfig.kill_aura) {
                        double r = HandOfGodConfig.kill_aura_range;
                        List<Entity> nearby = player.world.getEntitiesWithinAABBExcludingEntity(player,new AxisAlignedBB(player.posX - r, player.posY - r, player.posZ - r, player.posX + r, player.posY + r, player.posZ + r));
                        for (Entity entityLivingBase : nearby) {
                                entityLivingBase.attackEntityFrom(DamageSource.causePlayerDamage(player), 1000000);
                        }
                    }

                    IAttribute speedAttr = SharedMonsterAttributes.MOVEMENT_SPEED;
                    if (HandOfGodConfig.walking_speed > 1) {
                        if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID) == null) {
                            player.getEntityAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), HandOfGodConfig.walking_speed, 1));
                        } else if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID).getAmount() != HandOfGodConfig.walking_speed) {
                            player.getEntityAttribute(speedAttr).removeModifier(player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
                            player.getEntityAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), HandOfGodConfig.walking_speed, 1));
                        }

                        if (!player.onGround && player.getRidingEntity() == null) {
                            player.jumpMovementFactor = (float) (0.02F + (0.02F * HandOfGodConfig.walking_speed));
                        }
                    }
                }
                if (HandOfGodConfig.inertia_cancellation && e.player.moveForward == 0 && e.player.moveStrafing == 0 && e.player.capabilities.isFlying) {
                    e.player.motionX *= 0.5;
                    e.player.motionZ *= 0.5;
                }
            } else {
                e.player.capabilities.allowFlying = false;
                e.player.setInvisible(false);

                if (!e.player.world.isRemote) {
                    if (player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(WALK_SPEED_UUID) != null) {
                        player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(WALK_SPEED_UUID));
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

    @SubscribeEvent
    public static void itemDrops(BlockEvent.HarvestDropsEvent e) {
        EntityPlayer harvester = e.getHarvester();
        if (harvester != null) {
            ItemStack stack = harvester.getHeldItemMainhand();
            if (stack.getItem() instanceof HandOfGodItem && !HandOfGodConfig.blocks_drop_items) {
                e.getDrops().clear();
            }
        }
    }

    @SubscribeEvent
    public static void entityDrops(LivingDropsEvent e) {
        Entity attacker = e.getSource().getTrueSource();
        if (attacker instanceof EntityLivingBase) {
            EntityLivingBase livingEntity = (EntityLivingBase) attacker;
            if (Util.hasHand(livingEntity)) {
                if (!HandOfGodConfig.entities_drop_items) {
                    e.getDrops().clear();
                }
            }
        }
    }

    @SubscribeEvent
    public static void noClip(GetCollisionBoxesEvent e) {
        if (HandOfGodConfig.no_clip) {
            e.getCollisionBoxesList().clear();
        }
    }

    @SubscribeEvent
    public static void attack(LivingAttackEvent e) {
        EntityLivingBase victim = e.getEntityLiving();
        if (Util.hasHand(victim)) {
            e.setCanceled(true);
            if (HandOfGodConfig.thorns) {
                DamageSource source = e.getSource();
                if (source.getTrueSource() != null) {
                    HandOfGodItem.pk(source.getTrueSource());
                }
            }
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent e) {
        EntityLivingBase victim = e.getEntityLiving();

        Entity attacker = e.getSource().getTrueSource();

        if (victim instanceof EntityPlayer && attacker instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) victim;
            EntityPlayer attack = (EntityPlayer) attacker;

            if (attack.getHeldItemMainhand().getItem() == ModItems.HAND_OF_GOD) {

                if (HandOfGodConfig.inventory_destruction) {
                    player.inventory.clear();
                    player.getInventoryEnderChest().clear();
                }
                if (HandOfGodConfig.kick_player) {
                    ((EntityPlayerMP) player).connection.disconnect(new TextComponentString(HandOfGodConfig.kick_message));
                }

                if (HandOfGodConfig.beyond_redemption) {

                    String[] temp = HandOfGodConfig.beyond_redemption_player_list;

                    HandOfGodConfig.beyond_redemption_player_list = new String[temp.length + 1];

                    for (int i = 0; i < temp.length;i++) {
                        HandOfGodConfig.beyond_redemption_player_list[i] = temp[i];
                    }
                    HandOfGodConfig.beyond_redemption_player_list[temp.length] = player.getGameProfile().getId().toString();
                }
            }
        }
    }
}
