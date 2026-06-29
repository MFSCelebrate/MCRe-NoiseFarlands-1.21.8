/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LookAtS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, LookAtS2CPacket> CODEC = Packet.createCodec(LookAtS2CPacket::write, LookAtS2CPacket::new);
    final private double targetX;
    final private double targetY;
    final private double targetZ;
    final private int entityId;
    final private EntityAnchorArgumentType.EntityAnchor selfAnchor;
    final private EntityAnchorArgumentType.EntityAnchor targetAnchor;
    final private boolean lookAtEntity;

    public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor selfAnchor, double targetX, double targetY, double targetZ) {
        this.selfAnchor = selfAnchor;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.entityId = 0;
        this.lookAtEntity = false;
        this.targetAnchor = null;
    }

    public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor selfAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
        this.selfAnchor = selfAnchor;
        this.entityId = entity.getId();
        this.targetAnchor = targetAnchor;
        Vec3d vec3d = targetAnchor.positionAt(entity);
        this.targetX = vec3d.x;
        this.targetY = vec3d.y;
        this.targetZ = vec3d.z;
        this.lookAtEntity = true;
    }

    private LookAtS2CPacket(PacketByteBuf buf) {
        this.selfAnchor = buf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        this.targetX = buf.readDouble();
        this.targetY = buf.readDouble();
        this.targetZ = buf.readDouble();
        this.lookAtEntity = buf.readBoolean();
        if (this.lookAtEntity) {
            this.entityId = buf.readVarInt();
            this.targetAnchor = buf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        } else {
            this.entityId = 0;
            this.targetAnchor = null;
        }
    }

    private void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.selfAnchor);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.targetX);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.targetY);
        buf.net_minecraft_network_PacketByteBuf_writeDouble(this.targetZ);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.lookAtEntity);
        if (this.lookAtEntity) {
            buf.writeVarInt(this.entityId);
            buf.writeEnumConstant(this.targetAnchor);
        }
    }

    @Override
    public PacketType<LookAtS2CPacket> getPacketType() {
        return PlayPackets.PLAYER_LOOK_AT;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onLookAt(this);
    }

    public EntityAnchorArgumentType.EntityAnchor getSelfAnchor() {
        return this.selfAnchor;
    }

    @Nullable
    public Vec3d getTargetPosition(World world) {
        if (this.lookAtEntity) {
            Entity entity = world.getEntityById(this.entityId);
            if (entity == null) {
                return new Vec3d(this.targetX, this.targetY, this.targetZ);
            }
            return this.targetAnchor.positionAt(entity);
        }
        return new Vec3d(this.targetX, this.targetY, this.targetZ);
    }
}

