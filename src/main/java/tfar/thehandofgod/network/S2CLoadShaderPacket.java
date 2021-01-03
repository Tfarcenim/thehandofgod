package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.TheHandOfGod;

public class S2CLoadShaderPacket implements IMessage {

    boolean unload;
    public S2CLoadShaderPacket(){}

    public S2CLoadShaderPacket(boolean unload){
        this.unload = unload;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        unload = buf.readBoolean();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(unload);
    }

    public static class Handler implements IMessageHandler<S2CLoadShaderPacket, IMessage> {
        @Override
        public IMessage onMessage(S2CLoadShaderPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(S2CLoadShaderPacket message, MessageContext ctx) {
            if (message.unload) {
                Minecraft.getMinecraft().entityRenderer.loadEntityShader(Minecraft.getMinecraft().player);
            } else {
                Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation(TheHandOfGod.MODID,"shaders/post/monochrome.json"));
            }
        }
    }
}
