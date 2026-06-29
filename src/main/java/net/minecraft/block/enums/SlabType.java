/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class SlabType
extends Enum<SlabType>
implements StringIdentifiable {
    final static public SlabType TOP = new SlabType("top");
    final static public SlabType BOTTOM = new SlabType("bottom");
    final static public SlabType DOUBLE = new SlabType("double");
    final private String name;
    final static private SlabType[] field_12680;

    public static SlabType[] values() {
        return (SlabType[])field_12680.clone();
    }

    public static SlabType valueOf(String string) {
        return Enum.valueOf(SlabType.class, string);
    }

    private SlabType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static SlabType[] method_36735() {
        return new SlabType[]{TOP, BOTTOM, DOUBLE};
    }

    static {
        field_12680 = SlabType.method_36735();
    }
}

