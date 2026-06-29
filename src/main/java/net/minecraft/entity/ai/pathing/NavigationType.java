/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.pathing;

public final class NavigationType
extends Enum<NavigationType> {
    final static public NavigationType LAND = new NavigationType();
    final static public NavigationType WATER = new NavigationType();
    final static public NavigationType AIR = new NavigationType();
    final static private NavigationType[] field_49;

    public static NavigationType[] values() {
        return (NavigationType[])field_49.clone();
    }

    public static NavigationType valueOf(String string) {
        return Enum.valueOf(NavigationType.class, string);
    }

    private static NavigationType[] method_36789() {
        return new NavigationType[]{LAND, WATER, AIR};
    }

    static {
        field_49 = NavigationType.method_36789();
    }
}

