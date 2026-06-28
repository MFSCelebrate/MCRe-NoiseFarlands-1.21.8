/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record BrandCustomPayload(String brand) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, BrandCustomPayload> CODEC = CustomPayload.codecOf(BrandCustomPayload::write, BrandCustomPayload::new);
    final static public CustomPayload.Id<BrandCustomPayload> ID = CustomPayload.id("brand");

    private BrandCustomPayload(PacketByteBuf buf) {
        this(buf.readString());
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(this.brand);
    }

    public CustomPayload.Id<BrandCustomPayload> getId() {
        return ID;
    }
}

