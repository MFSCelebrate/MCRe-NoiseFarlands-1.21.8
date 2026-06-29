/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ScreenHandlerPropertyUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, ScreenHandlerPropertyUpdateS2CPacket> CODEC = Packet.createCodec(ScreenHandlerPropertyUpdateS2CPacket::write, ScreenHandlerPropertyUpdateS2CPacket::new);
    final private int syncId;
    final private int propertyId;
    final private int value;

    public ScreenHandlerPropertyUpdateS2CPacket(int syncId, int propertyId, int value) {
        this.syncId = syncId;
        this.propertyId = propertyId;
        this.value = value;
    }

    private ScreenHandlerPropertyUpdateS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readSyncId();
        this.propertyId = buf.readShort();
        this.value = buf.readShort();
    }

    private void write(PacketByteBuf buf) {
        buf.writeSyncId(this.syncId);
        buf.net_minecraft_network_PacketByteBuf_writeShort(this.propertyId);
        buf.net_minecraft_network_PacketByteBuf_writeShort(this.value);
    }

    @Override
    public PacketType<ScreenHandlerPropertyUpdateS2CPacket> getPacketType() {
        return PlayPackets.CONTAINER_SET_DATA;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onScreenHandlerPropertyUpdate(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getPropertyId() {
        return this.propertyId;
    }

    public int getValue() {
        return this.value;
    }
}

