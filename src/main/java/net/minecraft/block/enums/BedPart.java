/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class BedPart
extends Enum<BedPart>
implements StringIdentifiable {
    final static public BedPart HEAD = new BedPart("head");
    final static public BedPart FOOT = new BedPart("foot");
    final private String name;
    final static private BedPart[] field_12558;

    public static BedPart[] values() {
        return (BedPart[])field_12558.clone();
    }

    public static BedPart valueOf(String string) {
        return Enum.valueOf(BedPart.class, string);
    }

    private BedPart(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static BedPart[] method_36722() {
        return new BedPart[]{HEAD, FOOT};
    }

    static {
        field_12558 = BedPart.method_36722();
    }
}

