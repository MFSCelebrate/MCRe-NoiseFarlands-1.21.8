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
import net.minecraft.util.math.BlockPos;

public class WorldEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, WorldEventS2CPacket> CODEC = Packet.createCodec(WorldEventS2CPacket::write, WorldEventS2CPacket::new);
    final private int eventId;
    final private BlockPos pos;
    final private int data;
    final private boolean global;

    public WorldEventS2CPacket(int eventId, BlockPos pos, int data, boolean global) {
        this.eventId = eventId;
        this.pos = pos.toImmutable();
        this.data = data;
        this.global = global;
    }

    private WorldEventS2CPacket(PacketByteBuf buf) {
        this.eventId = buf.readInt();
        this.pos = buf.readBlockPos();
        this.data = buf.readInt();
        this.global = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.eventId);
        buf.writeBlockPos(this.pos);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.data);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.global);
    }

    @Override
    public PacketType<WorldEventS2CPacket> getPacketType() {
        return PlayPackets.LEVEL_EVENT;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onWorldEvent(this);
    }

    public boolean isGlobal() {
        return this.global;
    }

    public int getEventId() {
        return this.eventId;
    }

    public int getData() {
        return this.data;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

