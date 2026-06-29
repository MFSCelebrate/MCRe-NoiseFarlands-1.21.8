/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiAddedCustomPayload(BlockPos pos, String poiType, int freeTicketCount) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugPoiAddedCustomPayload> CODEC = CustomPayload.codecOf(DebugPoiAddedCustomPayload::write, DebugPoiAddedCustomPayload::new);
    final static public CustomPayload.Id<DebugPoiAddedCustomPayload> ID = CustomPayload.id("debug/poi_added");

    private DebugPoiAddedCustomPayload(PacketByteBuf buf) {
        this(buf.readBlockPos(), buf.readString(), buf.readInt());
    }

    public DebugPoiAddedCustomPayload(BlockPos blockPos, String string, int i) {
        this.pos = blockPos;
        this.poiType = string;
        this.freeTicketCount = 1;
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeString(this.poiType);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.freeTicketCount);
    }

    public CustomPayload.Id<DebugPoiAddedCustomPayload> getId() {
        return ID;
    }
}

