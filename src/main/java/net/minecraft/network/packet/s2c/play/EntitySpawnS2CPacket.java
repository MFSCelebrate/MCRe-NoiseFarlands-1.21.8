/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntitySpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<RegistryByteBuf, EntitySpawnS2CPacket> CODEC = Packet.createCodec(EntitySpawnS2CPacket::write, EntitySpawnS2CPacket::new);
    final static private double VELOCITY_SCALE = 8000.0;
    final static private double MAX_ABSOLUTE_VELOCITY = 3.9;
    final private int entityId;
    final private UUID uuid;
    final private EntityType<?> entityType;
    final private double x;
    final private double y;
    final private double z;
    final private int velocityX;
    final private int velocityY;
    final private int velocityZ;
    final private byte pitch;
    final private byte yaw;
    final private byte headYaw;
    final private int entityData;

    public EntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry) {
        this(entity, entityTrackerEntry, 0);
    }

    public EntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry, int entityData) {
        this(entity.getId(), entity.getUuid(), entityTrackerEntry.getPos().getX(), entityTrackerEntry.getPos().getY(), entityTrackerEntry.getPos().getZ(), entityTrackerEntry.getPitch(), entityTrackerEntry.getYaw(), entity.getType(), entityData, entityTrackerEntry.getVelocity(), entityTrackerEntry.getHeadYaw());
    }

    public EntitySpawnS2CPacket(Entity entity, int entityData, BlockPos pos) {
        this(entity.getId(), entity.getUuid(), pos.getX(), pos.getY(), pos.getZ(), entity.getPitch(), entity.getYaw(), entity.getType(), entityData, entity.getVelocity(), entity.getHeadYaw());
    }

    public EntitySpawnS2CPacket(int entityId, UUID uuid, double x, double y, double z, float pitch, float yaw, EntityType<?> entityType, int entityData, Vec3d velocity, double headYaw) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = MathHelper.packDegrees(pitch);
        this.yaw = MathHelper.packDegrees(yaw);
        this.headYaw = MathHelper.packDegrees((float)headYaw);
        this.entityType = entityType;
        this.entityData = entityData;
        this.velocityX = (int)(MathHelper.clamp(velocity.x, -3.9, 3.9) * 8000.0);
        this.velocityY = (int)(MathHelper.clamp(velocity.y, -3.9, 3.9) * 8000.0);
        this.velocityZ = (int)(MathHelper.clamp(velocity.z, -3.9, 3.9) * 8000.0);
    }

    private EntitySpawnS2CPacket(RegistryByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.entityType = (EntityType)PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE).decode(buf);
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.headYaw = buf.readByte();
        this.entityData = buf.readVarInt();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
    }

    private void write(RegistryByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeUuid(this.uuid);
        PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE).encode(buf, this.entityType);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.x);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.y);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.z);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.pitch);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.yaw);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.headYaw);
        buf.writeVarInt(this.entityData);
        buf.net_minecraft_network_PacketByteBuf_writeShort(this.velocityX);
        buf.net_minecraft_network_PacketByteBuf_writeShort(this.velocityY);
        buf.net_minecraft_network_PacketByteBuf_writeShort(this.velocityZ);
    }

    @Override
    public PacketType<EntitySpawnS2CPacket> getPacketType() {
        return PlayPackets.ADD_ENTITY;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntitySpawn(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
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

    public double getVelocityX() {
        return (double)this.velocityX / 8000.0;
    }

    public double getVelocityY() {
        return (double)this.velocityY / 8000.0;
    }

    public double getVelocityZ() {
        return (double)this.velocityZ / 8000.0;
    }

    public float getPitch() {
        return MathHelper.unpackDegrees(this.pitch);
    }

    public float getYaw() {
        return MathHelper.unpackDegrees(this.yaw);
    }

    public float getHeadYaw() {
        return MathHelper.unpackDegrees(this.headYaw);
    }

    public int getEntityData() {
        return this.entityData;
    }
}

