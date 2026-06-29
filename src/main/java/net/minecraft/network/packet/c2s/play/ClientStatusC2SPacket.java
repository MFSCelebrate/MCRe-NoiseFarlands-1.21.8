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

public class ClientStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, ClientStatusC2SPacket> CODEC = Packet.createCodec(ClientStatusC2SPacket::write, ClientStatusC2SPacket::new);
    final private Mode mode;

    public ClientStatusC2SPacket(Mode mode) {
        this.mode = mode;
    }

    private ClientStatusC2SPacket(PacketByteBuf buf) {
        this.mode = buf.readEnumConstant(Mode.class);
    }

    private void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.mode);
    }

    @Override
    public PacketType<ClientStatusC2SPacket> getPacketType() {
        return PlayPackets.CLIENT_COMMAND;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onClientStatus(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public static final class Mode
    extends Enum<Mode> {
        final static public Mode PERFORM_RESPAWN = new Mode();
        final static public Mode REQUEST_STATS = new Mode();
        final static private Mode[] field_12776;

        public static Mode[] values() {
            return (Mode[])field_12776.clone();
        }

        public static Mode valueOf(String string) {
            return Enum.valueOf(Mode.class, string);
        }

        private static Mode[] method_36955() {
            return new Mode[]{PERFORM_RESPAWN, REQUEST_STATS};
        }

        static {
            field_12776 = Mode.method_36955();
        }
    }
}

