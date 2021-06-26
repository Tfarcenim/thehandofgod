package tfar.thehandofgod.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.network.C2SKeybindPacket;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.util.Constants;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ModKeybinds {

    public static final KeyBinding BACKPACK = new KeyBinding("Backpack", Keyboard.KEY_I, TheHandOfGod.MODID);

    public static void clientSetup() {
        ClientRegistry.registerKeyBinding(BACKPACK);
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent e) {
        while ((BACKPACK.isPressed())) {
            PacketHandler.INSTANCE.sendToServer(new C2SKeybindPacket(Constants.KeybindType.BACKPACK));
        }
    }
}
