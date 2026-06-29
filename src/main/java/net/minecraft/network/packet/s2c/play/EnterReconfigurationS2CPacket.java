/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EnterReconfigurationS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public EnterReconfigurationS2CPacket INSTANCE = new EnterReconfigurationS2CPacket();
    final static public PacketCodec<ByteBuf, EnterReconfigurationS2CPacket> CODEC = PacketCodec.unit(INSTANCE);

    private EnterReconfigurationS2CPacket() {
    }

    @Override
    public PacketType<EnterReconfigurationS2CPacket> getPacketType() {
        return PlayPackets.START_CONFIGURATION;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEnterReconfiguration(this);
    }

    @Override
    public boolean transitionsNetworkState() {
        return true;
    }
}

