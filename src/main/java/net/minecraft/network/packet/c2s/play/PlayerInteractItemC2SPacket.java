/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Hand;

public class PlayerInteractItemC2SPacket
implements Packet<ServerPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, PlayerInteractItemC2SPacket> CODEC = Packet.createCodec(PlayerInteractItemC2SPacket::write, PlayerInteractItemC2SPacket::new);
    final private Hand hand;
    final private int sequence;
    final private float yaw;
    final private float pitch;

    public PlayerInteractItemC2SPacket(Hand hand, int sequence, float yaw, float pitch) {
        this.hand = hand;
        this.sequence = sequence;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private PlayerInteractItemC2SPacket(PacketByteBuf buf) {
        this.hand = buf.readEnumConstant(Hand.class);
        this.sequence = buf.readVarInt();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    private void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.hand);
        buf.writeVarInt(this.sequence);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.yaw);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.pitch);
    }

    @Override
    public PacketType<PlayerInteractItemC2SPacket> getPacketType() {
        return PlayPackets.USE_ITEM;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerInteractItem(this);
    }

    public Hand getHand() {
        return this.hand;
    }

    public int getSequence() {
        return this.sequence;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}

