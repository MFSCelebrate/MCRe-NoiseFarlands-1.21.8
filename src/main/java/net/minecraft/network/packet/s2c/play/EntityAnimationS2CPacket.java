/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EntityAnimationS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, EntityAnimationS2CPacket> CODEC = Packet.createCodec(EntityAnimationS2CPacket::write, EntityAnimationS2CPacket::new);
    final static public int SWING_MAIN_HAND = 0;
    final static public int WAKE_UP = 2;
    final static public int SWING_OFF_HAND = 3;
    final static public int CRIT = 4;
    final static public int ENCHANTED_HIT = 5;
    final private int entityId;
    final private int animationId;

    public EntityAnimationS2CPacket(Entity entity, int animationId) {
        this.entityId = entity.getId();
        this.animationId = animationId;
    }

    private EntityAnimationS2CPacket(PacketByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.animationId = buf.readUnsignedByte();
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.animationId);
    }

    @Override
    public PacketType<EntityAnimationS2CPacket> getPacketType() {
        return PlayPackets.ANIMATE;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAnimation(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getAnimationId() {
        return this.animationId;
    }
}

