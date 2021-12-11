package tfar.thehandofgod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.thehandofgod.HandOfGodConfig;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.entity.EntityArchangel;
import tfar.thehandofgod.util.Constants;

public class C2SOpenGuiFromKeybindPacket implements IMessage {

    private Constants.ScreenType type;

    public C2SOpenGuiFromKeybindPacket(){}

    public C2SOpenGuiFromKeybindPacket(Constants.ScreenType type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Constants.ScreenType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    public static class Handler implements IMessageHandler<C2SOpenGuiFromKeybindPacket, IMessage> {
        @Override
        public IMessage onMessage(C2SOpenGuiFromKeybindPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(C2SOpenGuiFromKeybindPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (message.type.screen) {
                player.openGui(TheHandOfGod.INSTANCE, message.type.ordinal(), player.world, 0, 0, 0);
            } else {
                switch (message.type) {
                    case ARCHANGEL: {

                        boolean exists = false;
                        for (Entity entity : player.world.loadedEntityList) {
                            if (entity instanceof EntityArchangel && ((EntityArchangel)entity).isOwner(player)) {
                                entity.setDead();
                                exists = true;
                            }
                        }

                        if (!exists) {
                            EntityArchangel archangel = new EntityArchangel(player.world);
                            archangel.setOwner(player);
                            archangel.setPositionAndUpdate(player.posX,player.posY,player.posZ);
                            player.world.spawnEntity(archangel);
                        }

                    }break;

                    case GAMEMODE:{
                        GameType gameType = getGameType(player.interactionManager.getGameType());
                        player.setGameType(gameType);
                    }break;

                    case CLEANSE:{
                        HandOfGodConfig.perfect_cleanse = !HandOfGodConfig.perfect_cleanse;
                        if (HandOfGodConfig.perfect_cleanse) {
                            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                            server.getWorld(0).getGameRules().setOrCreateGameRule("doMobSpawning","false");
                            for (WorldServer serverWorld : server.worlds) {
                                for (Entity entity : serverWorld.loadedEntityList) {
                                    if (!(entity instanceof EntityPlayerMP)) {
                                        entity.setDead();
                                    }
                                }
                            }
                        }
                    }break;

                    default:
                }
            }
        }
    }

    private static GameType getGameType(GameType gameType) {
        switch (gameType) {
            case SURVIVAL:return GameType.CREATIVE;
            case CREATIVE:return GameType.SPECTATOR;
            case SPECTATOR:return GameType.SURVIVAL;
        }
        return GameType.SURVIVAL;
    }
}
