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

public class BoatPaddleStateC2SPacket
implements Packet<ServerPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, BoatPaddleStateC2SPacket> CODEC = Packet.createCodec(BoatPaddleStateC2SPacket::write, BoatPaddleStateC2SPacket::new);
    final private boolean leftPaddling;
    final private boolean rightPaddling;

    public BoatPaddleStateC2SPacket(boolean leftPaddling, boolean rightPaddling) {
        this.leftPaddling = leftPaddling;
        this.rightPaddling = rightPaddling;
    }

    private BoatPaddleStateC2SPacket(PacketByteBuf buf) {
        this.leftPaddling = buf.readBoolean();
        this.rightPaddling = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.leftPaddling);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.rightPaddling);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onBoatPaddleState(this);
    }

    @Override
    public PacketType<BoatPaddleStateC2SPacket> getPacketType() {
        return PlayPackets.PADDLE_BOAT;
    }

    public boolean isLeftPaddling() {
        return this.leftPaddling;
    }

    public boolean isRightPaddling() {
        return this.rightPaddling;
    }
}

