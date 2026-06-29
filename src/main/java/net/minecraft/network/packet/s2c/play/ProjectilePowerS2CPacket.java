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

public class ProjectilePowerS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, ProjectilePowerS2CPacket> CODEC = Packet.createCodec(ProjectilePowerS2CPacket::write, ProjectilePowerS2CPacket::new);
    final private int entityId;
    final private double accelerationPower;

    public ProjectilePowerS2CPacket(int entityId, double accelerationPower) {
        this.entityId = entityId;
        this.accelerationPower = accelerationPower;
    }

    private ProjectilePowerS2CPacket(PacketByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.accelerationPower = buf.readDouble();
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.accelerationPower);
    }

    @Override
    public PacketType<ProjectilePowerS2CPacket> getPacketType() {
        return PlayPackets.PROJECTILE_POWER;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onProjectilePower(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public double getAccelerationPower() {
        return this.accelerationPower;
    }
}

