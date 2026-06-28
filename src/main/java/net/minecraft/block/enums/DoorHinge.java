/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class DoorHinge
extends Enum<DoorHinge>
implements StringIdentifiable {
    final static public DoorHinge LEFT = new DoorHinge();
    final static public DoorHinge RIGHT = new DoorHinge();
    final static private DoorHinge[] field_12587;

    public static DoorHinge[] values() {
        return (DoorHinge[])field_12587.clone();
    }

    public static DoorHinge valueOf(String string) {
        return Enum.valueOf(DoorHinge.class, string);
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return this == LEFT ? "left" : "right";
    }

    private static DoorHinge[] method_36726() {
        return new DoorHinge[]{LEFT, RIGHT};
    }

    static {
        field_12587 = DoorHinge.method_36726();
    }
}

