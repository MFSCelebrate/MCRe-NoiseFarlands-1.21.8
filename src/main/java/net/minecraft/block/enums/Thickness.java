/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class Thickness
extends Enum<Thickness>
implements StringIdentifiable {
    final static public Thickness TIP_MERGE = new Thickness("tip_merge");
    final static public Thickness TIP = new Thickness("tip");
    final static public Thickness FRUSTUM = new Thickness("frustum");
    final static public Thickness MIDDLE = new Thickness("middle");
    final static public Thickness BASE = new Thickness("base");
    final private String name;
    final static private Thickness[] field_28070;

    public static Thickness[] values() {
        return (Thickness[])field_28070.clone();
    }

    public static Thickness valueOf(String string) {
        return Enum.valueOf(Thickness.class, string);
    }

    private Thickness(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static Thickness[] method_36728() {
        return new Thickness[]{TIP_MERGE, TIP, FRUSTUM, MIDDLE, BASE};
    }

    static {
        field_28070 = Thickness.method_36728();
    }
}

