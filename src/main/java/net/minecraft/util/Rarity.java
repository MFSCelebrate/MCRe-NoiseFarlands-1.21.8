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
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class Rarity
extends Enum<Rarity>
implements StringIdentifiable {
    final static public Rarity COMMON = new Rarity(0, "common", Formatting.WHITE);
    final static public Rarity UNCOMMON = new Rarity(1, "uncommon", Formatting.YELLOW);
    final static public Rarity RARE = new Rarity(2, "rare", Formatting.AQUA);
    final static public Rarity EPIC = new Rarity(3, "epic", Formatting.LIGHT_PURPLE);
    final static public Codec<Rarity> CODEC;
    final static public IntFunction<Rarity> ID_TO_VALUE;
    final static public PacketCodec<ByteBuf, Rarity> PACKET_CODEC;
    final private int index;
    final private String name;
    final private Formatting formatting;
    final static private Rarity[] field_8905;

    public static Rarity[] values() {
        return (Rarity[])field_8905.clone();
    }

    public static Rarity valueOf(String string) {
        return Enum.valueOf(Rarity.class, string);
    }

    private Rarity(int index, String name, Formatting formatting) {
        this.index = index;
        this.name = name;
        this.formatting = formatting;
    }

    public Formatting getFormatting() {
        return this.formatting;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static Rarity[] method_36683() {
        return new Rarity[]{COMMON, UNCOMMON, RARE, EPIC};
    }

    static {
        field_8905 = Rarity.method_36683();
        CODEC = StringIdentifiable.createBasicCodec(Rarity::values);
        ID_TO_VALUE = ValueLists.createIndexToValueFunction(value -> value.index, Rarity.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, value -> value.index);
    }
}

