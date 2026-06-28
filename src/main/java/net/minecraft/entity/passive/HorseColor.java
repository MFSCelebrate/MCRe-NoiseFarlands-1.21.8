/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class HorseColor
extends Enum<HorseColor>
implements StringIdentifiable {
    final static public HorseColor WHITE = new HorseColor(0, "white");
    final static public HorseColor CREAMY = new HorseColor(1, "creamy");
    final static public HorseColor CHESTNUT = new HorseColor(2, "chestnut");
    final static public HorseColor BROWN = new HorseColor(3, "brown");
    final static public HorseColor BLACK = new HorseColor(4, "black");
    final static public HorseColor GRAY = new HorseColor(5, "gray");
    final static public HorseColor DARK_BROWN = new HorseColor(6, "dark_brown");
    final static public Codec<HorseColor> CODEC;
    final static private IntFunction<HorseColor> INDEX_MAPPER;
    final static public PacketCodec<ByteBuf, HorseColor> PACKET_CODEC;
    final private int index;
    final private String id;
    final static private HorseColor[] field_23825;

    public static HorseColor[] values() {
        return (HorseColor[])field_23825.clone();
    }

    public static HorseColor valueOf(String string) {
        return Enum.valueOf(HorseColor.class, string);
    }

    private HorseColor(int index, String id) {
        this.index = index;
        this.id = id;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseColor byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static HorseColor[] method_36646() {
        return new HorseColor[]{WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN};
    }

    static {
        field_23825 = HorseColor.method_36646();
        CODEC = StringIdentifiable.createCodec(HorseColor::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(HorseColor::getIndex, HorseColor.values(), ValueLists.OutOfBoundsHandling.WRAP);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, HorseColor::getIndex);
    }
}

