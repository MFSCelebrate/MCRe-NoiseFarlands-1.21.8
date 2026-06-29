/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.packet.s2c.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.block.WireOrientation;

public record DebugRedstoneUpdateOrderCustomPayload(long time, List<Wire> wires) implements CustomPayload
{
    final static public CustomPayload.Id<DebugRedstoneUpdateOrderCustomPayload> ID = CustomPayload.id("debug/redstone_update_order");
    final static public PacketCodec<PacketByteBuf, DebugRedstoneUpdateOrderCustomPayload> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_LONG, DebugRedstoneUpdateOrderCustomPayload::time, Wire.PACKET_CODEC.collect(PacketCodecs.toList()), DebugRedstoneUpdateOrderCustomPayload::wires, DebugRedstoneUpdateOrderCustomPayload::new);

    public CustomPayload.Id<DebugRedstoneUpdateOrderCustomPayload> getId() {
        return ID;
    }

    public record Wire(BlockPos pos, WireOrientation orientation) {
        final static public PacketCodec<ByteBuf, Wire> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, Wire::pos, WireOrientation.PACKET_CODEC, Wire::orientation, Wire::new);
    }
}

