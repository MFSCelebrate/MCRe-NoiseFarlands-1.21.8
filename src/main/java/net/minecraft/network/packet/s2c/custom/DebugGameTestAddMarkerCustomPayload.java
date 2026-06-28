/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugGameTestAddMarkerCustomPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugGameTestAddMarkerCustomPayload> CODEC = CustomPayload.codecOf(DebugGameTestAddMarkerCustomPayload::write, DebugGameTestAddMarkerCustomPayload::new);
    final static public CustomPayload.Id<DebugGameTestAddMarkerCustomPayload> ID = CustomPayload.id("debug/game_test_add_marker");

    private DebugGameTestAddMarkerCustomPayload(PacketByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt(), buf.readString(), buf.readInt());
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.color);
        buf.writeString(this.text);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.durationMs);
    }

    public CustomPayload.Id<DebugGameTestAddMarkerCustomPayload> getId() {
        return ID;
    }
}

