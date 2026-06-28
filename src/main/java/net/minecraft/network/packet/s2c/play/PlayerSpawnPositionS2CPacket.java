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

public class PlayerSpawnPositionS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, PlayerSpawnPositionS2CPacket> CODEC = Packet.createCodec(PlayerSpawnPositionS2CPacket::write, PlayerSpawnPositionS2CPacket::new);
    final private BlockPos pos;
    final private float angle;

    public PlayerSpawnPositionS2CPacket(BlockPos pos, float angle) {
        this.pos = pos;
        this.angle = angle;
    }

    private PlayerSpawnPositionS2CPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.angle = buf.readFloat();
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.angle);
    }

    @Override
    public PacketType<PlayerSpawnPositionS2CPacket> getPacketType() {
        return PlayPackets.SET_DEFAULT_SPAWN_POSITION;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerSpawnPosition(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public float getAngle() {
        return this.angle;
    }
}

