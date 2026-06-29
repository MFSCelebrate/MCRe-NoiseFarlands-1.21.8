/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public final class TriState
extends Enum<TriState> {
    final static public TriState TRUE = new TriState();
    final static public TriState FALSE = new TriState();
    final static public TriState DEFAULT = new TriState();
    final static private TriState[] field_52397;

    public static TriState[] values() {
        return (TriState[])field_52397.clone();
    }

    public static TriState valueOf(String string) {
        return Enum.valueOf(TriState.class, string);
    }

    public boolean asBoolean(boolean fallback) {
        return switch (this.ordinal()) {
            case 0 -> true;
            case 1 -> false;
            default -> fallback;
        };
    }

    private static TriState[] method_61347() {
        return new TriState[]{TRUE, FALSE, DEFAULT};
    }

    static {
        field_52397 = TriState.method_61347();
    }
}

