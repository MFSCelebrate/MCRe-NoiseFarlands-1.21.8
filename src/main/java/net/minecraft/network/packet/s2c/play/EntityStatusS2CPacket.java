/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EntityStatusS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, EntityStatusS2CPacket> CODEC = Packet.createCodec(EntityStatusS2CPacket::write, EntityStatusS2CPacket::new);
    final private int entityId;
    final private byte status;

    public EntityStatusS2CPacket(Entity entity, byte status) {
        this.entityId = entity.getId();
        this.status = status;
    }

    private EntityStatusS2CPacket(PacketByteBuf buf) {
        this.entityId = buf.readInt();
        this.status = buf.readByte();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.entityId);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.status);
    }

    @Override
    public PacketType<EntityStatusS2CPacket> getPacketType() {
        return PlayPackets.ENTITY_EVENT;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityStatus(this);
    }

    @Nullable
    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }

    public byte getStatus() {
        return this.status;
    }
}

