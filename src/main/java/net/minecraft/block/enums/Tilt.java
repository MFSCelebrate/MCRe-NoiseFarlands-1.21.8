/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class Tilt
extends Enum<Tilt>
implements StringIdentifiable {
    final static public Tilt NONE = new Tilt("none", true);
    final static public Tilt UNSTABLE = new Tilt("unstable", false);
    final static public Tilt PARTIAL = new Tilt("partial", true);
    final static public Tilt FULL = new Tilt("full", true);
    final private String name;
    final private boolean stable;
    final static private Tilt[] field_28724;

    public static Tilt[] values() {
        return (Tilt[])field_28724.clone();
    }

    public static Tilt valueOf(String string) {
        return Enum.valueOf(Tilt.class, string);
    }

    private Tilt(String name, boolean stable) {
        this.name = name;
        this.stable = stable;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public boolean isStable() {
        return this.stable;
    }

    private static Tilt[] method_36738() {
        return new Tilt[]{NONE, UNSTABLE, PARTIAL, FULL};
    }

    static {
        field_28724 = Tilt.method_36738();
    }
}

