/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class BambooLeaves
extends Enum<BambooLeaves>
implements StringIdentifiable {
    final static public BambooLeaves NONE = new BambooLeaves("none");
    final static public BambooLeaves SMALL = new BambooLeaves("small");
    final static public BambooLeaves LARGE = new BambooLeaves("large");
    final private String name;
    final static private BambooLeaves[] field_12470;

    public static BambooLeaves[] values() {
        return (BambooLeaves[])field_12470.clone();
    }

    public static BambooLeaves valueOf(String string) {
        return Enum.valueOf(BambooLeaves.class, string);
    }

    private BambooLeaves(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static BambooLeaves[] method_36721() {
        return new BambooLeaves[]{NONE, SMALL, LARGE};
    }

    static {
        field_12470 = BambooLeaves.method_36721();
    }
}

