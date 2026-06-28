/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public final class ClickType
extends Enum<ClickType> {
    final static public ClickType LEFT = new ClickType();
    final static public ClickType RIGHT = new ClickType();
    final static private ClickType[] field_27015;

    public static ClickType[] values() {
        return (ClickType[])field_27015.clone();
    }

    public static ClickType valueOf(String string) {
        return Enum.valueOf(ClickType.class, string);
    }

    private static ClickType[] method_36672() {
        return new ClickType[]{LEFT, RIGHT};
    }

    static {
        field_27015 = ClickType.method_36672();
    }
}

