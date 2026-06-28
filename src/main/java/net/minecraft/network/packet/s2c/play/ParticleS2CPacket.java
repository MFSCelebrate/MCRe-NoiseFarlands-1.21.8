/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class ParticleS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<RegistryByteBuf, ParticleS2CPacket> CODEC = Packet.createCodec(ParticleS2CPacket::write, ParticleS2CPacket::new);
    final private double x;
    final private double y;
    final private double z;
    final private float offsetX;
    final private float offsetY;
    final private float offsetZ;
    final private float speed;
    final private int count;
    final private boolean forceSpawn;
    final private boolean important;
    final private ParticleEffect parameters;

    public <T extends ParticleEffect> ParticleS2CPacket(T parameters, boolean forceSpawn, boolean important, double x, double y, double z, float offsetX, float offsetY, float offsetZ, float speed, int count) {
        this.parameters = parameters;
        this.forceSpawn = forceSpawn;
        this.important = important;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.count = count;
    }

    private ParticleS2CPacket(RegistryByteBuf buf) {
        this.forceSpawn = buf.readBoolean();
        this.important = buf.readBoolean();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.offsetX = buf.readFloat();
        this.offsetY = buf.readFloat();
        this.offsetZ = buf.readFloat();
        this.speed = buf.readFloat();
        this.count = buf.readInt();
        this.parameters = (ParticleEffect)ParticleTypes.PACKET_CODEC.decode(buf);
    }

    private void write(RegistryByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.forceSpawn);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.important);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.x);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.y);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.z);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.offsetX);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.offsetY);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.offsetZ);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.speed);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.count);
        ParticleTypes.PACKET_CODEC.encode(buf, this.parameters);
    }

    @Override
    public PacketType<ParticleS2CPacket> getPacketType() {
        return PlayPackets.LEVEL_PARTICLES;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onParticle(this);
    }

    public boolean shouldForceSpawn() {
        return this.forceSpawn;
    }

    public boolean isImportant() {
        return this.important;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getOffsetX() {
        return this.offsetX;
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public float getOffsetZ() {
        return this.offsetZ;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getCount() {
        return this.count;
    }

    public ParticleEffect getParameters() {
        return this.parameters;
    }
}

