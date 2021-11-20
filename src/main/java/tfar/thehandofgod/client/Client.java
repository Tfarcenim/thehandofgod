package tfar.thehandofgod.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import tfar.thehandofgod.ColoredLightningEntity;
import tfar.thehandofgod.client.search.SearchHelper;
import tfar.thehandofgod.client.search.color.ColorGetter;
import tfar.thehandofgod.client.search.color.ColorNamer;
import tfar.thehandofgod.init.ModItems;
import tfar.thehandofgod.init.ModSounds;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.network.C2SStopTimePacket;
import tfar.thehandofgod.network.PacketHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(Side.CLIENT)
public class Client {

    public static final SearchTreeManager.Key<ItemStack> ALL_ITEMS = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> MOD_NAMES = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> TOOLTIPS = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> OREDICT = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> CREATIVE_TAB = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> COLOR = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> RESOURCE_ID = new SearchTreeManager.Key<>();

    public static boolean stopped;

    @SubscribeEvent
    public static void client(ModelRegistryEvent e) {
        setModel(ModItems.HAND_OF_GOD);
    }

    public static void setModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent e) {
        while (ModKeybinds.TIME_STOP.isPressed()) {
            SoundEvent sound = stopped ? ModSounds.TIME_START : ModSounds.TIME_STOP;
            Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player, Minecraft.getMinecraft().player.getPosition(),
                    sound, SoundCategory.BLOCKS, 1.2F, 1);
            PacketHandler.INSTANCE.sendToServer(new C2SStopTimePacket());
        }
    }

    public static void preInit(FMLPreInitializationEvent e) {
        RenderingRegistry.registerEntityRenderingHandler(ColoredLightningEntity.class, ColoredLightningRenderer::new);
    }

    public static void init(FMLInitializationEvent e) {
    }

    public static void buildColorNamer() {
        final String[] searchColorDefaults = ColorGetter.getColorDefaults();
        final String[] searchColors = searchColorDefaults;

        final ImmutableMap.Builder<Color, String> searchColorsMapBuilder = ImmutableMap.builder();
        for (String entry : searchColors) {
            final String[] values = entry.split(":");
            if (values.length != 2) {
                TheHandOfGod.logger.error("Invalid format for searchColor entry: {}", entry);
            } else {
                try {
                    final String name = values[0];
                    final int colorValue = Integer.decode("0x" + values[1]);
                    final Color color = new Color(colorValue);
                    searchColorsMapBuilder.put(color, name);
                } catch (NumberFormatException e) {
                    TheHandOfGod.logger.error("Invalid number format for searchColor entry: {}", entry, e);
                }
            }
        }
        final ColorNamer colorNamer = new ColorNamer(searchColorsMapBuilder.build());
        SearchHelper.colorNamer = colorNamer;
    }

    public static void onTimeToggle(boolean stop, boolean user) {
        Client.stopped = stop;
        if (stop) {
            Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation(TheHandOfGod.MODID, "shaders/post/monochrome.json"));
        } else {
            Minecraft.getMinecraft().entityRenderer.loadEntityShader(Minecraft.getMinecraft().player);
        }

        for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
            if (entity != Minecraft.getMinecraft().player || !user)
                entity.updateBlocked = Client.stopped;
        }
    }


    public static void registerSearchTrees(SearchTreeManager searchTreeManager) {

        buildColorNamer();

        searchTreeManager.register(ALL_ITEMS, buildAllItemsSearch());
        searchTreeManager.register(MOD_NAMES, buildModNameSearch());
        searchTreeManager.register(TOOLTIPS, buildTooltipSearch());
        searchTreeManager.register(OREDICT, buildOredictSearch());
        searchTreeManager.register(CREATIVE_TAB, buildCreativeTabSearch());
        searchTreeManager.register(COLOR, buildColorSearch());
        searchTreeManager.register(RESOURCE_ID, buildResourceIDSearch());

    }

    private static SearchTree<ItemStack> buildAllItemsSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(stack ->
                stack.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).stream()
                        .map(TextFormatting::getTextWithoutFormattingCodes)
                        .map(s -> s != null ? s.trim() : null)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()), stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    private static SearchTree<ItemStack> buildTooltipSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(stack ->
                stack.getTooltip(null, ITooltipFlag.TooltipFlags.ADVANCED).stream()
                        .map(TextFormatting::getTextWithoutFormattingCodes)
                        .map(s -> s != null ? s.trim() : null)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()), stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    private static SearchTree<ItemStack> buildOredictSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(Client::getOreDictNames, stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    private static SearchTree<ItemStack> buildCreativeTabSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(Client::getCreativeTabNames, stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    public static Collection<String> getCreativeTabNames(ItemStack ingredient) {
        Collection<String> creativeTabsStrings = new ArrayList<>();
        Item item = ingredient.getItem();
        for (CreativeTabs creativeTab : item.getCreativeTabs()) {
            if (creativeTab != null) {
                String creativeTabName = I18n.format(creativeTab.getTranslationKey());
                creativeTabsStrings.add(creativeTabName);
            }
        }
        return creativeTabsStrings;
    }

    public static Collection<String> getOreDictNames(ItemStack ingredient) {
        Collection<String> names = new ArrayList<>();
        for (int oreId : OreDictionary.getOreIDs(ingredient)) {
            String oreNameLowercase = OreDictionary.getOreName(oreId).toLowerCase(Locale.ENGLISH);
            names.add(oreNameLowercase);
        }
        return names;
    }

    private static SearchTree<ItemStack> buildModNameSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(Client::modidAndName, stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    private static List<String> modidAndName(ItemStack stack) {
        List<String> list = new ArrayList<>();
        list.add(stack.getItem().getRegistryName().getNamespace());
        String modName = getModName(stack);
        if (modName != null)
            list.add(modName);
        return list;
    }

    private static SearchTree<ItemStack> buildColorSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(Client::color, stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    private static Collection<String> color(ItemStack stack) {
        return SearchHelper.colorNamer.getColorNames(getColors(stack), false);
    }

    public static Iterable<Color> getColors(ItemStack ingredient) {
        return ColorGetter.getColors(ingredient, 2);
    }

    private static SearchTree<ItemStack> buildResourceIDSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(stack -> Lists.newArrayList(stack.getItem().getRegistryName().getPath()), stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        finish(searchtree);
        return searchtree;
    }

    public static void finish(SearchTree<ItemStack> searchTree) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (Item item : Item.REGISTRY) {
            NonNullList<ItemStack> temp = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, temp);
            //forcibly add the item even if it isn't in any tabs, watch out for air
            if (temp.isEmpty() && !(item instanceof ItemAir)) {
                temp.add(item.getDefaultInstance());
            }
            nonnulllist.addAll(temp);
        }

        nonnulllist.forEach(searchTree::add);
    }

    @Nullable
    private static String getModName(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            String modId = item.getCreatorModId(itemStack);
            if (modId != null) {
                Map<String, ModContainer> indexedModList = Loader.instance().getIndexedModList();
                ModContainer modContainer = indexedModList.get(modId);
                if (modContainer != null) {
                    return modContainer.getName();
                }
            }
        }
        return null;
    }

}
