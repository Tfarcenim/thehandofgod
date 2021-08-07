package tfar.thehandofgod.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import tfar.thehandofgod.TheHandOfGod;

public class ModSounds {

    public static final SoundEvent TIME_STOP = new SoundEvent(new ResourceLocation(TheHandOfGod.MODID,"time_stop"));
    public static final SoundEvent TIME_START = new SoundEvent(new ResourceLocation(TheHandOfGod.MODID,"time_start"));

    public static void register(IForgeRegistry<SoundEvent> registry) {
        registry.registerAll(TIME_STOP.setRegistryName(TIME_STOP.getSoundName()),TIME_START.setRegistryName(TIME_START.getSoundName()));
    }
}
