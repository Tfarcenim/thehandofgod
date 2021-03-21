package tfar.thehandofgod;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
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

    private static String[] getDefaultPotions() {
        return ForgeRegistries.POTIONS.getValuesCollection().stream()
                .filter(Potion::isBeneficial)
                .map(IForgeRegistryEntry.Impl::getRegistryName)
                .map(ResourceLocation::toString).toArray(String[]::new);
    }

    @Config.Ignore
    public static Set<Potion> allowed = new HashSet<>();

    public static void parseConfigs() {
        allowed.clear();
        for (String string : allowed_potions) {
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(string));
            allowed.add(potion);
        }
    }
}
