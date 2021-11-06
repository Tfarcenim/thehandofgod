package tfar.thehandofgod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import sun.awt.ModalExclude;
import tfar.thehandofgod.HandOfGodItem;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.network.C2SOpenGuiFromKeybindPacket;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.util.Constants;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ModKeybinds {

    public static final KeyBinding BACKPACK = new KeyBinding("Backpack", Keyboard.KEY_UNLABELED, TheHandOfGod.MODID);
    public static final KeyBinding ENCHANTMENTS = new KeyBinding("Enchantments", Keyboard.KEY_UNLABELED, TheHandOfGod.MODID);
    public static final KeyBinding TIME_STOP = new KeyBinding("time_stop", Keyboard.KEY_Y, TheHandOfGod.MODID);
    public static final KeyBinding POTIONS = new KeyBinding("potion",Keyboard.KEY_Z,TheHandOfGod.MODID);
    public static final KeyBinding TELEPORT = new KeyBinding("teleport",Keyboard.KEY_P,TheHandOfGod.MODID);
    public static final KeyBinding HEAVENLY_POCKET = new KeyBinding("heavenly_pocket",Keyboard.KEY_H,TheHandOfGod.MODID);


    public static void register() {
        for (Field field : ModKeybinds.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof KeyBinding) {
                    ClientRegistry.registerKeyBinding((KeyBinding)o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent e) {
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof HandOfGodItem) {

            while (BACKPACK.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new C2SOpenGuiFromKeybindPacket(Constants.ScreenType.BACKPACK));
            }
            while (ENCHANTMENTS.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new C2SOpenGuiFromKeybindPacket(Constants.ScreenType.ENCHANTMENTS));
            }
            while (POTIONS.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new C2SOpenGuiFromKeybindPacket(Constants.ScreenType.POTIONS));
            }
            while (TELEPORT.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new C2SOpenGuiFromKeybindPacket(Constants.ScreenType.TELEPORT));
            }
            while (HEAVENLY_POCKET.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new C2SOpenGuiFromKeybindPacket(Constants.ScreenType.HEAVENLY_POCKET));
            }
        }
    }
}
