/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugWorldgenAttemptCustomPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugWorldgenAttemptCustomPayload> CODEC = CustomPayload.codecOf(DebugWorldgenAttemptCustomPayload::write, DebugWorldgenAttemptCustomPayload::new);
    final static public CustomPayload.Id<DebugWorldgenAttemptCustomPayload> ID = CustomPayload.id("debug/worldgen_attempt");

    private DebugWorldgenAttemptCustomPayload(PacketByteBuf buf) {
        this(buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.scale);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.red);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.green);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.blue);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.alpha);
    }

    public CustomPayload.Id<DebugWorldgenAttemptCustomPayload> getId() {
        return ID;
    }
}

