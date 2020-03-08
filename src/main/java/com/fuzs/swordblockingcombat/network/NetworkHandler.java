package com.fuzs.swordblockingcombat.network;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.fuzs.swordblockingcombat.network.message.IMessage;
import com.fuzs.swordblockingcombat.network.message.SimpleSwingMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@SuppressWarnings("unused")
public class NetworkHandler {

    private static final NetworkHandler INSTANCE = new NetworkHandler();
    private final String PROTOCOL_VERSION = Integer.toString(1);
    private final SimpleChannel MAIN_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(SwordBlockingCombat.MODID, "main_channel"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private int discriminator;

    public void init() {

        this.registerMessage(new SimpleSwingMessage());
    }

    private <T extends IMessage> void registerMessage(final T message) {

        MAIN_CHANNEL.registerMessage(this.discriminator++, message.getClass(), IMessage::writePacketData, message::readPacketData, (msg, side) -> {

            NetworkEvent.Context ctx = side.get();
            ctx.setPacketHandled(true);
            if (ctx.getDirection().getOriginationSide().equals(msg.getExecutionSide())) {

                SwordBlockingCombat.LOGGER.error("Receiving {} at wrong side!", msg.getClass().getSimpleName());
                return;
            }

            ctx.enqueueWork(() -> msg.processPacket(ctx.getSender()));
        });
    }

    public void sendToServer(IMessage message) {

        MAIN_CHANNEL.sendToServer(message);
    }

    public void sendTo(IMessage message, ServerPlayerEntity player) {

        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public void sendToAll(IMessage message) {

        MAIN_CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    public static NetworkHandler getInstance() {

        return INSTANCE;
    }

}