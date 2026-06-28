/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class WallShape
extends Enum<WallShape>
implements StringIdentifiable {
    final static public WallShape NONE = new WallShape("none");
    final static public WallShape LOW = new WallShape("low");
    final static public WallShape TALL = new WallShape("tall");
    final private String name;
    final static private WallShape[] field_22182;

    public static WallShape[] values() {
        return (WallShape[])field_22182.clone();
    }

    public static WallShape valueOf(String string) {
        return Enum.valueOf(WallShape.class, string);
    }

    private WallShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static WallShape[] method_36739() {
        return new WallShape[]{NONE, LOW, TALL};
    }

    static {
        field_22182 = WallShape.method_36739();
    }
}

