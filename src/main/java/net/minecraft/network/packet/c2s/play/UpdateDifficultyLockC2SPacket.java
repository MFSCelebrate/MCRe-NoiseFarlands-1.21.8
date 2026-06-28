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

public class UpdateDifficultyLockC2SPacket
implements Packet<ServerPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, UpdateDifficultyLockC2SPacket> CODEC = Packet.createCodec(UpdateDifficultyLockC2SPacket::write, UpdateDifficultyLockC2SPacket::new);
    final private boolean difficultyLocked;

    public UpdateDifficultyLockC2SPacket(boolean difficultyLocked) {
        this.difficultyLocked = difficultyLocked;
    }

    private UpdateDifficultyLockC2SPacket(PacketByteBuf buf) {
        this.difficultyLocked = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.difficultyLocked);
    }

    @Override
    public PacketType<UpdateDifficultyLockC2SPacket> getPacketType() {
        return PlayPackets.LOCK_DIFFICULTY;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onUpdateDifficultyLock(this);
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
}

