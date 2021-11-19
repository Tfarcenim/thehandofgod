package tfar.thehandofgod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import tfar.thehandofgod.ColoredLightningEntity;
import tfar.thehandofgod.init.ModItems;
import tfar.thehandofgod.init.ModSounds;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.network.C2SStopTimePacket;
import tfar.thehandofgod.network.PacketHandler;

import java.util.Collections;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(Side.CLIENT)
public class Client {

    public static final SearchTreeManager.Key<ItemStack> ALL_ITEMS = new SearchTreeManager.Key<>();

    public static boolean stopped;

    @SubscribeEvent
    public static void client(ModelRegistryEvent e) {
        setModel(ModItems.HAND_OF_GOD);
    }

    public static void setModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent e) {
        while (ModKeybinds.TIME_STOP.isPressed()) {
            SoundEvent sound = stopped ? ModSounds.TIME_START : ModSounds.TIME_STOP;
            Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player,Minecraft.getMinecraft().player.getPosition(),
                    sound, SoundCategory.BLOCKS, 1.2F, 1);
            PacketHandler.INSTANCE.sendToServer(new C2SStopTimePacket());
        }
    }

    public static void preInit(FMLPreInitializationEvent e) {
        RenderingRegistry.registerEntityRenderingHandler(ColoredLightningEntity.class, ColoredLightningRenderer::new);
    }

    public static void init(FMLInitializationEvent e) {
    }

    public static void onTimeToggle(boolean stop, boolean user) {
        Client.stopped = stop;
        if (stop) {
            Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation(TheHandOfGod.MODID,"shaders/post/monochrome.json"));
        } else {
            Minecraft.getMinecraft().entityRenderer.loadEntityShader(Minecraft.getMinecraft().player);
        }

        for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
            if (entity != Minecraft.getMinecraft().player || !user)
            entity.updateBlocked = Client.stopped;
        }
    }


    public static void registerTree(SearchTreeManager searchTreeManager) {

        searchTreeManager.register(ALL_ITEMS, buildAllItemsSearch());
    }

    private static SearchTree<ItemStack> buildAllItemsSearch() {
        SearchTree<ItemStack> searchtree = new SearchTree<>(stack ->
                stack.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).stream()
                        .map(TextFormatting::getTextWithoutFormattingCodes)
                        .map(s -> s != null ? s.trim() : null)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()), stack ->
                Collections.singleton(Item.REGISTRY.getNameForObject(stack.getItem())));
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (Item item : Item.REGISTRY)
        {
            NonNullList<ItemStack> temp = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, temp);
            if (temp.isEmpty()) {
                temp.add(item.getDefaultInstance());
            }
            nonnulllist.addAll(temp);
        }

        nonnulllist.forEach(searchtree::add);
        return searchtree;
    }
}
