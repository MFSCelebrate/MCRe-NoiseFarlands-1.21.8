/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record EntityPositionSyncS2CPacket(int id, PlayerPosition values, boolean onGround) implements Packet<ClientPlayPacketListener>
{
    final static public PacketCodec<PacketByteBuf, EntityPositionSyncS2CPacket> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EntityPositionSyncS2CPacket::id, PlayerPosition.PACKET_CODEC, EntityPositionSyncS2CPacket::values, PacketCodecs.BOOLEAN, EntityPositionSyncS2CPacket::onGround, EntityPositionSyncS2CPacket::new);

    public static EntityPositionSyncS2CPacket create(Entity entity) {
        return new EntityPositionSyncS2CPacket(entity.getId(), new PlayerPosition(entity.getSyncedPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch()), entity.isOnGround());
    }

    @Override
    public PacketType<EntityPositionSyncS2CPacket> getPacketType() {
        return PlayPackets.ENTITY_POSITION_SYNC;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityPositionSync(this);
    }
}

