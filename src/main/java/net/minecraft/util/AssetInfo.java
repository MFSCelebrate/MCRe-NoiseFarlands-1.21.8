/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record AssetInfo(Identifier id, Identifier texturePath) {
    final static public Codec<AssetInfo> CODEC = Identifier.CODEC.xmap(AssetInfo::new, AssetInfo::id);
    final static public MapCodec<AssetInfo> MAP_CODEC = CODEC.fieldOf("asset_id");
    final static public PacketCodec<ByteBuf, AssetInfo> PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, AssetInfo::id, AssetInfo::new);

    public AssetInfo(Identifier id) {
        this(id, id.withPath(path -> "textures/" + path + ".png"));
    }
}

