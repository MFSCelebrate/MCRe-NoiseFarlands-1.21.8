/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util.math;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.math.MathHelper;

public final class AxisRotation
extends Enum<AxisRotation> {
    final static public AxisRotation R0 = new AxisRotation(0);
    final static public AxisRotation R90 = new AxisRotation(1);
    final static public AxisRotation R180 = new AxisRotation(2);
    final static public AxisRotation R270 = new AxisRotation(3);
    final static public Codec<AxisRotation> CODEC;
    final public int index;
    final static private AxisRotation[] field_57035;

    public static AxisRotation[] values() {
        return (AxisRotation[])field_57035.clone();
    }

    public static AxisRotation valueOf(String string) {
        return Enum.valueOf(AxisRotation.class, string);
    }

    private AxisRotation(int index) {
        this.index = index;
    }

    @Deprecated
    public static AxisRotation fromDegrees(int degrees) {
        return switch (MathHelper.floorMod(degrees, 360)) {
            case 0 -> R0;
            case 90 -> R90;
            case 180 -> R180;
            case 270 -> R270;
            default -> throw new JsonParseException("Invalid rotation " + degrees + " found, only 0/90/180/270 allowed");
        };
    }

    public int rotate(int index) {
        return (index + this.index) % 4;
    }

    private static AxisRotation[] method_68063() {
        return new AxisRotation[]{R0, R90, R180, R270};
    }

    static {
        field_57035 = AxisRotation.method_68063();
        CODEC = Codec.INT.comapFlatMap(degrees -> switch (MathHelper.floorMod(degrees, 360)) {
            case 0 -> DataResult.success((Object)((Object)R0));
            case 90 -> DataResult.success((Object)((Object)R90));
            case 180 -> DataResult.success((Object)((Object)R180));
            case 270 -> DataResult.success((Object)((Object)R270));
            default -> DataResult.error(() -> "Invalid rotation " + degrees + " found, only 0/90/180/270 allowed");
        }, rotation -> switch (rotation.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 0;
            case 1 -> 90;
            case 2 -> 180;
            case 3 -> 270;
        });
    }
}

