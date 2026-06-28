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
import org.jetbrains.annotations.Nullable;

public class EntityAttachS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, EntityAttachS2CPacket> CODEC = Packet.createCodec(EntityAttachS2CPacket::write, EntityAttachS2CPacket::new);
    final private int attachedEntityId;
    final private int holdingEntityId;

    public EntityAttachS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
        this.attachedEntityId = attachedEntity.getId();
        this.holdingEntityId = holdingEntity != null ? holdingEntity.getId() : 0;
    }

    private EntityAttachS2CPacket(PacketByteBuf buf) {
        this.attachedEntityId = buf.readInt();
        this.holdingEntityId = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.attachedEntityId);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.holdingEntityId);
    }

    @Override
    public PacketType<EntityAttachS2CPacket> getPacketType() {
        return PlayPackets.SET_ENTITY_LINK;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAttach(this);
    }

    public int getAttachedEntityId() {
        return this.attachedEntityId;
    }

    public int getHoldingEntityId() {
        return this.holdingEntityId;
    }
}

