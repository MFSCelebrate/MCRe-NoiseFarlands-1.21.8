/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

public final class LightType
extends Enum<LightType> {
    final static public LightType SKY = new LightType();
    final static public LightType BLOCK = new LightType();
    final static private LightType[] field_9285;

    public static LightType[] values() {
        return (LightType[])field_9285.clone();
    }

    public static LightType valueOf(String string) {
        return Enum.valueOf(LightType.class, string);
    }

    private static LightType[] method_36696() {
        return new LightType[]{SKY, BLOCK};
    }

    static {
        field_9285 = LightType.method_36696();
    }
}

