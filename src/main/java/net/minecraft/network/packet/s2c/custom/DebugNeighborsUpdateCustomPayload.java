/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugNeighborsUpdateCustomPayload(long time, BlockPos pos) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugNeighborsUpdateCustomPayload> CODEC = CustomPayload.codecOf(DebugNeighborsUpdateCustomPayload::write, DebugNeighborsUpdateCustomPayload::new);
    final static public CustomPayload.Id<DebugNeighborsUpdateCustomPayload> ID = CustomPayload.id("debug/neighbors_update");

    private DebugNeighborsUpdateCustomPayload(PacketByteBuf buf) {
        this(buf.readVarLong(), buf.readBlockPos());
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarLong(this.time);
        buf.writeBlockPos(this.pos);
    }

    public CustomPayload.Id<DebugNeighborsUpdateCustomPayload> getId() {
        return ID;
    }
}

