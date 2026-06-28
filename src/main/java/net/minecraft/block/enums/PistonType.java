/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class PistonType
extends Enum<PistonType>
implements StringIdentifiable {
    final static public PistonType DEFAULT = new PistonType("normal");
    final static public PistonType STICKY = new PistonType("sticky");
    final private String name;
    final static private PistonType[] field_12636;

    public static PistonType[] values() {
        return (PistonType[])field_12636.clone();
    }

    public static PistonType valueOf(String string) {
        return Enum.valueOf(PistonType.class, string);
    }

    private PistonType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static PistonType[] method_36731() {
        return new PistonType[]{DEFAULT, STICKY};
    }

    static {
        field_12636 = PistonType.method_36731();
    }
}

