/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.function.IntFunction;
import net.minecraft.util.function.ValueLists;

public final class HorseMarking
extends Enum<HorseMarking> {
    final static public HorseMarking NONE = new HorseMarking(0);
    final static public HorseMarking WHITE = new HorseMarking(1);
    final static public HorseMarking WHITE_FIELD = new HorseMarking(2);
    final static public HorseMarking WHITE_DOTS = new HorseMarking(3);
    final static public HorseMarking BLACK_DOTS = new HorseMarking(4);
    final static private IntFunction<HorseMarking> INDEX_MAPPER;
    final private int index;
    final static private HorseMarking[] field_23815;

    public static HorseMarking[] values() {
        return (HorseMarking[])field_23815.clone();
    }

    public static HorseMarking valueOf(String string) {
        return Enum.valueOf(HorseMarking.class, string);
    }

    private HorseMarking(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseMarking byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    private static HorseMarking[] method_36645() {
        return new HorseMarking[]{NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS};
    }

    static {
        field_23815 = HorseMarking.method_36645();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(HorseMarking::getIndex, HorseMarking.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

