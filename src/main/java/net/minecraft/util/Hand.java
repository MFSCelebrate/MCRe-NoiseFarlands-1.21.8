/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public final class Hand
extends Enum<Hand> {
    final static public Hand MAIN_HAND = new Hand();
    final static public Hand OFF_HAND = new Hand();
    final static private Hand[] field_5809;

    public static Hand[] values() {
        return (Hand[])field_5809.clone();
    }

    public static Hand valueOf(String string) {
        return Enum.valueOf(Hand.class, string);
    }

    private static Hand[] method_36598() {
        return new Hand[]{MAIN_HAND, OFF_HAND};
    }

    static {
        field_5809 = Hand.method_36598();
    }
}

