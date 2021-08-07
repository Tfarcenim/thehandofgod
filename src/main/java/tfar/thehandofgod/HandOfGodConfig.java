package tfar.thehandofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;

@Config(modid = TheHandOfGod.MODID)
public class HandOfGodConfig {

    @Config.Name("cone_angle")
    public static double cone_angle = 45;

    @Config.Name("cone_length")
    public static double cone_length = 64;

    @Config.Name("allowed_potions")
    public static String[] allowed_potions = getDefaultPotions();

    @Config.Name("break_radius")
    public static int break_radius = 1;

    @Config.Name("inertia_cancellation")
    public static boolean inertia_cancellation = true;

    @Config.Name("flight_speed")
    public static double flight_speed = 2;

    @Config.Name("true_invisibility")
    public static boolean true_invisibility = true;

    @Config.Name("blocks_drop_items")
    public static boolean blocks_drop_items = true;

    @Config.Name("entities_drop_items")
    public static boolean entities_drop_items = true;

    @Config.Name("perfect_cleanse")
    public static boolean perfect_cleanse = false;

    @Config.Name("omnipresence")
    @Config.Comment("When activated, all entities in the world currently are removed and no new entities can spawn.")
    public static boolean omnipresence = false;

    @Config.Name("no_clip")
    public static boolean no_clip = false;

    @Config.Name("judgement")
    @Config.Comment("Toggle black lightning spawning when entities are killed by the area attack.")
    public static boolean judgement = true;

    @Config.Name("thorns")
    @Config.Comment("Configure if entities that attack the player are automatically PK-ed")
    public static boolean thorns = true;

    @Config.Name("infinite_energy")
    public static boolean infinite_energy = true;

    @Config.Name("inventory_destruction")
    @Config.Comment("Toggle inventory destruction. Clears target's inventory upon kill, including ender chests")
    public static boolean inventory_destruction = true;

    @Config.Name("block_reach_distance")
    public static double block_reach_distance = 5;

    private static String[] getDefaultPotions() {
        return ForgeRegistries.POTIONS.getValuesCollection().stream()
                .filter(Potion::isBeneficial)
                .map(IForgeRegistryEntry.Impl::getRegistryName)
                .map(ResourceLocation::toString).toArray(String[]::new);
    }

    @Config.Ignore
    public static Set<Potion> allowed = new HashSet<>();

    public static void parseConfigs(boolean worldActive) {
        allowed.clear();
        for (String string : allowed_potions) {
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(string));
            allowed.add(potion);
        }

        if (worldActive && perfect_cleanse) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.getWorld(0).getGameRules().setOrCreateGameRule("doMobSpawning","false");
            for (WorldServer serverWorld : server.worlds) {
                for (Entity entity : serverWorld.loadedEntityList) {
                    if (!(entity instanceof EntityPlayerMP)) {
                        entity.setDead();
                    }
                }
            }
        }
    }
}
