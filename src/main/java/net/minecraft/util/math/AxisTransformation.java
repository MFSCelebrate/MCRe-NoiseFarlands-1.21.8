/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 */
package net.minecraft.util.math;

import java.util.Arrays;
import net.minecraft.util.Util;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public final class AxisTransformation
extends Enum<AxisTransformation> {
    final static public AxisTransformation P123 = new AxisTransformation(0, 1, 2);
    final static public AxisTransformation P213 = new AxisTransformation(1, 0, 2);
    final static public AxisTransformation P132 = new AxisTransformation(0, 2, 1);
    final static public AxisTransformation P231 = new AxisTransformation(1, 2, 0);
    final static public AxisTransformation P312 = new AxisTransformation(2, 0, 1);
    final static public AxisTransformation P321 = new AxisTransformation(2, 1, 0);
    final private int[] mappings;
    final private Matrix3fc matrix;
    final static private int NUM_AXES = 3;
    final static private AxisTransformation[][] COMBINATIONS;
    final static private AxisTransformation[] field_23371;

    public static AxisTransformation[] values() {
        return (AxisTransformation[])field_23371.clone();
    }

    public static AxisTransformation valueOf(String string) {
        return Enum.valueOf(AxisTransformation.class, string);
    }

    private AxisTransformation(int xMapping, int yMapping, int zMapping) {
        this.mappings = new int[]{xMapping, yMapping, zMapping};
        Matrix3f matrix3f = new Matrix3f().zero();
        matrix3f.set(this.map(0), 0, 1.0f);
        matrix3f.set(this.map(1), 1, 1.0f);
        matrix3f.set(this.map(2), 2, 1.0f);
        this.matrix = matrix3f;
    }

    public AxisTransformation prepend(AxisTransformation transformation) {
        return COMBINATIONS[this.ordinal()][transformation.ordinal()];
    }

    public int map(int oldAxis) {
        return this.mappings[oldAxis];
    }

    public Matrix3fc getMatrix() {
        return this.matrix;
    }

    private static AxisTransformation[] method_36937() {
        return new AxisTransformation[]{P123, P213, P132, P231, P312, P321};
    }

    static {
        field_23371 = AxisTransformation.method_36937();
        COMBINATIONS = Util.make(new AxisTransformation[AxisTransformation.values().length][AxisTransformation.values().length], combinations -> {
            for (AxisTransformation axisTransformation : AxisTransformation.values()) {
                for (AxisTransformation axisTransformation2 : AxisTransformation.values()) {
                    AxisTransformation axisTransformation3;
                    int[] is = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        is[i] = axisTransformation.mappings[axisTransformation2.mappings[i]];
                    }
                    combinations[axisTransformation.ordinal()][axisTransformation2.ordinal()] = axisTransformation3 = Arrays.stream(AxisTransformation.values()).filter(transformation -> Arrays.equals(transformation.mappings, is)).findFirst().get();
                }
            }
        });
    }
}

