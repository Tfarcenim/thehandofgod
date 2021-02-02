package tfar.thehandofgod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static final Item HAND_OF_GOD = new HandOfGodItem().setCreativeTab(CreativeTabs.COMBAT);

    public static void register(IForgeRegistry<Item> registry) {
        registry.register(HAND_OF_GOD.setRegistryName("hand_of_god").setTranslationKey("thehandofgod.hand_of_god"));
    }
}
