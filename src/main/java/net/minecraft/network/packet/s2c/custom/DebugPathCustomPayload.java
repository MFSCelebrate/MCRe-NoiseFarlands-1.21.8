/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DebugPathCustomPayload(int entityId, Path path, float maxNodeDistance) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugPathCustomPayload> CODEC = CustomPayload.codecOf(DebugPathCustomPayload::write, DebugPathCustomPayload::new);
    final static public CustomPayload.Id<DebugPathCustomPayload> ID = CustomPayload.id("debug/path");

    private DebugPathCustomPayload(PacketByteBuf buf) {
        this(buf.readInt(), Path.fromBuf(buf), buf.readFloat());
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.entityId);
        this.path.toBuf(buf);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.maxNodeDistance);
    }

    public CustomPayload.Id<DebugPathCustomPayload> getId() {
        return ID;
    }
}

