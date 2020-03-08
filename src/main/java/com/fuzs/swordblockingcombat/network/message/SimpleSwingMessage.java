package com.fuzs.swordblockingcombat.network.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * swing player hand without triggering {@link net.minecraft.entity.player.PlayerEntity#resetCooldown()}
 */
public class SimpleSwingMessage implements IMessage {

    private Hand hand;

    public SimpleSwingMessage() {
    }

    public SimpleSwingMessage(Hand hand) {

        this.hand = hand;
    }

    @Override
    public void writePacketData(final PacketBuffer buf) {

        buf.writeByte(this.hand.ordinal());
    }

    @SuppressWarnings("unchecked")
    @Override
    public SimpleSwingMessage readPacketData(final PacketBuffer buf) {

        this.hand = Hand.values()[buf.readUnsignedByte()];
        return this;
    }

    @Override
    public void processPacket(@Nullable final PlayerEntity player) {

        if (player != null) {

            player.func_226292_a_(this.hand, false);
        }
    }

    @Override
    public LogicalSide getExecutionSide() {

        return LogicalSide.SERVER;
    }

}
