package tfar.thehandofgod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import tfar.thehandofgod.network.C2SStopTimePacket;
import tfar.thehandofgod.network.PacketHandler;

@Mod.EventBusSubscriber(Side.CLIENT)
public class Client {

    public static final KeyBinding time_stop = new KeyBinding("time_stop", Keyboard.KEY_Y, TheHandOfGod.MODID);
    public static boolean stopped;

    @SubscribeEvent
    public static void client(ModelRegistryEvent e) {
        ClientRegistry.registerKeyBinding(time_stop);
        setModel(ModItems.HAND_OF_GOD);
    }

    public static void setModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent e) {
        while (time_stop.isPressed()) {
            SoundEvent sound = stopped ? ModSounds.TIME_START : ModSounds.TIME_STOP;
            Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player,Minecraft.getMinecraft().player.getPosition(),
                    sound, SoundCategory.BLOCKS, 1.2F, 1);
            PacketHandler.INSTANCE.sendToServer(new C2SStopTimePacket());
        }
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
}
