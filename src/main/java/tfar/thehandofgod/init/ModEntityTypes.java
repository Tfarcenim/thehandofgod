package tfar.thehandofgod.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import tfar.thehandofgod.ColoredLightningEntity;
import tfar.thehandofgod.TheHandOfGod;

public class ModEntityTypes {

    public static int ID = 0;

    public static final EntityEntry COLORED_LIGHTNING = EntityEntryBuilder.create().entity(ColoredLightningEntity.class)
            .id(new ResourceLocation(TheHandOfGod.MODID,"colored_lightning"),0).name("colored_lighting")
            .tracker(64,1,true).build();

    public static void register(IForgeRegistry<EntityEntry> registry) {
        registry.register(COLORED_LIGHTNING);
    }
}
