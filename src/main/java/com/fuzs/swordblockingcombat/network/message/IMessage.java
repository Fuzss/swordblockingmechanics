package com.fuzs.swordblockingcombat.network.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

public interface IMessage {

    /**
     * writes message data to buffer
     * @param buf network data byte buffer
     */
    void writePacketData(final PacketBuffer buf);

    /**
     * reads message data from buffer
     * @param buf network data byte buffer
     */
    <T extends IMessage> T readPacketData(final PacketBuffer buf);

    /**
     * handles message
     * @param player network context of incoming message
     */
    void processPacket(@Nullable final PlayerEntity player);

    /**
     * side message is executed at
     * @return {@link LogicalSide#CLIENT} or {@link LogicalSide#SERVER}
     */
    LogicalSide getExecutionSide();

}
