/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiRemovedCustomPayload(BlockPos pos) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugPoiRemovedCustomPayload> CODEC = CustomPayload.codecOf(DebugPoiRemovedCustomPayload::write, DebugPoiRemovedCustomPayload::new);
    final static public CustomPayload.Id<DebugPoiRemovedCustomPayload> ID = CustomPayload.id("debug/poi_removed");

    private DebugPoiRemovedCustomPayload(PacketByteBuf buf) {
        this(buf.readBlockPos());
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public CustomPayload.Id<DebugPoiRemovedCustomPayload> getId() {
        return ID;
    }
}

