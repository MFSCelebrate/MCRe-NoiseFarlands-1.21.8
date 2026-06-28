/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class Unit
extends Enum<Unit> {
    final static public Unit INSTANCE = new Unit();
    final static public Codec<Unit> CODEC;
    final static public PacketCodec<ByteBuf, Unit> PACKET_CODEC;
    final static private Unit[] field_17275;

    public static Unit[] values() {
        return (Unit[])field_17275.clone();
    }

    public static Unit valueOf(String string) {
        return Enum.valueOf(Unit.class, string);
    }

    private static Unit[] method_36588() {
        return new Unit[]{INSTANCE};
    }

    static {
        field_17275 = Unit.method_36588();
        CODEC = Codec.unit((Object)((Object)INSTANCE));
        PACKET_CODEC = PacketCodec.unit(INSTANCE);
    }
}

