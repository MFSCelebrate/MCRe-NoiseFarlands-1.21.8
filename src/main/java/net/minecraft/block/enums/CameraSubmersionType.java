/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

public final class CameraSubmersionType
extends Enum<CameraSubmersionType> {
    final static public CameraSubmersionType LAVA = new CameraSubmersionType();
    final static public CameraSubmersionType WATER = new CameraSubmersionType();
    final static public CameraSubmersionType POWDER_SNOW = new CameraSubmersionType();
    final static public CameraSubmersionType DIMENSION_OR_BOSS = new CameraSubmersionType();
    final static public CameraSubmersionType ATMOSPHERIC = new CameraSubmersionType();
    final static public CameraSubmersionType NONE = new CameraSubmersionType();
    final static private CameraSubmersionType[] field_27889;

    public static CameraSubmersionType[] values() {
        return (CameraSubmersionType[])field_27889.clone();
    }

    public static CameraSubmersionType valueOf(String string) {
        return Enum.valueOf(CameraSubmersionType.class, string);
    }

    private static CameraSubmersionType[] method_36764() {
        return new CameraSubmersionType[]{LAVA, WATER, POWDER_SNOW, DIMENSION_OR_BOSS, ATMOSPHERIC, NONE};
    }

    static {
        field_27889 = CameraSubmersionType.method_36764();
    }
}

