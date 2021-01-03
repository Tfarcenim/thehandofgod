package tfar.thehandofgod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ModelRegistryEvent;
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

    @SubscribeEvent
    public static void client(ModelRegistryEvent e) {
        ClientRegistry.registerKeyBinding(time_stop);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent e) {
        while (time_stop.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new C2SStopTimePacket());
        }
    }
}
