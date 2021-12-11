package tfar.thehandofgod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import tfar.thehandofgod.HandOfGodItem;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.network.C2SOpenGuiFromKeybindPacket;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.util.Constants;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ModKeybinds {

    public static final KeyBinding ENCHANTMENTS = new KeyBinding("Enchantments", Keyboard.KEY_NUMPAD0, TheHandOfGod.MODID);
    public static final KeyBinding POTIONS = new KeyBinding("potion",Keyboard.KEY_NUMPAD1,TheHandOfGod.MODID);
    public static final KeyBinding TELEPORT = new KeyBinding("teleport",Keyboard.KEY_NUMPAD2,TheHandOfGod.MODID);
    public static final KeyBinding BACKPACK = new KeyBinding("Backpack", Keyboard.KEY_NUMPAD3, TheHandOfGod.MODID);
    public static final KeyBinding HEAVENLY_POCKET = new KeyBinding("heavenly_pocket",Keyboard.KEY_NUMPAD4,TheHandOfGod.MODID);
    public static final KeyBinding ARCHANGEL = new KeyBinding("archangel",Keyboard.KEY_NUMPAD5,TheHandOfGod.MODID);
    public static final KeyBinding GAMEMODE = new KeyBinding("gamemode",Keyboard.KEY_NUMPAD6,TheHandOfGod.MODID);
    public static final KeyBinding CLEANSE = new KeyBinding("cleanse",Keyboard.KEY_NUMPAD7,TheHandOfGod.MODID);
    public static final KeyBinding TIME_STOP = new KeyBinding("time_stop", Keyboard.KEY_NUMPAD8, TheHandOfGod.MODID);

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

            Constants.ScreenType type = null;

            while (BACKPACK.isPressed()) {
                type = Constants.ScreenType.BACKPACK;
            }
            while (ENCHANTMENTS.isPressed()) {
                type = Constants.ScreenType.ENCHANTMENTS;
            }
            while (POTIONS.isPressed()) {
                type = Constants.ScreenType.POTIONS;
            }
            while (TELEPORT.isPressed()) {
                type = Constants.ScreenType.TELEPORT;
            }
            while (HEAVENLY_POCKET.isPressed()) {
                type = Constants.ScreenType.HEAVENLY_POCKET;
            }
            while (ARCHANGEL.isPressed()) {
                type = Constants.ScreenType.ARCHANGEL;
            }
            while (GAMEMODE.isPressed()) {
                type = Constants.ScreenType.GAMEMODE;
            }
            while (CLEANSE.isPressed()) {
                type = Constants.ScreenType.CLEANSE;
            }

            if (type != null) {
                PacketHandler.INSTANCE.sendToServer(new C2SOpenGuiFromKeybindPacket(type));
            }
        }
    }
}
