/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class SculkSensorPhase
extends Enum<SculkSensorPhase>
implements StringIdentifiable {
    final static public SculkSensorPhase INACTIVE = new SculkSensorPhase("inactive");
    final static public SculkSensorPhase ACTIVE = new SculkSensorPhase("active");
    final static public SculkSensorPhase COOLDOWN = new SculkSensorPhase("cooldown");
    final private String name;
    final static private SculkSensorPhase[] field_28125;

    public static SculkSensorPhase[] values() {
        return (SculkSensorPhase[])field_28125.clone();
    }

    public static SculkSensorPhase valueOf(String string) {
        return Enum.valueOf(SculkSensorPhase.class, string);
    }

    private SculkSensorPhase(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static SculkSensorPhase[] method_36734() {
        return new SculkSensorPhase[]{INACTIVE, ACTIVE, COOLDOWN};
    }

    static {
        field_28125 = SculkSensorPhase.method_36734();
    }
}

