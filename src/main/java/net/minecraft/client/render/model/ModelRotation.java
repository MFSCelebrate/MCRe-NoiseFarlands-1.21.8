/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.model;

import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AffineTransformations;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public final class ModelRotation
extends Enum<ModelRotation>
implements ModelBakeSettings {
    final static public ModelRotation X0_Y0 = new ModelRotation(AxisRotation.R0, AxisRotation.R0);
    final static public ModelRotation X0_Y90 = new ModelRotation(AxisRotation.R0, AxisRotation.R90);
    final static public ModelRotation X0_Y180 = new ModelRotation(AxisRotation.R0, AxisRotation.R180);
    final static public ModelRotation X0_Y270 = new ModelRotation(AxisRotation.R0, AxisRotation.R270);
    final static public ModelRotation X90_Y0 = new ModelRotation(AxisRotation.R90, AxisRotation.R0);
    final static public ModelRotation X90_Y90 = new ModelRotation(AxisRotation.R90, AxisRotation.R90);
    final static public ModelRotation X90_Y180 = new ModelRotation(AxisRotation.R90, AxisRotation.R180);
    final static public ModelRotation X90_Y270 = new ModelRotation(AxisRotation.R90, AxisRotation.R270);
    final static public ModelRotation X180_Y0 = new ModelRotation(AxisRotation.R180, AxisRotation.R0);
    final static public ModelRotation X180_Y90 = new ModelRotation(AxisRotation.R180, AxisRotation.R90);
    final static public ModelRotation X180_Y180 = new ModelRotation(AxisRotation.R180, AxisRotation.R180);
    final static public ModelRotation X180_Y270 = new ModelRotation(AxisRotation.R180, AxisRotation.R270);
    final static public ModelRotation X270_Y0 = new ModelRotation(AxisRotation.R270, AxisRotation.R0);
    final static public ModelRotation X270_Y90 = new ModelRotation(AxisRotation.R270, AxisRotation.R90);
    final static public ModelRotation X270_Y180 = new ModelRotation(AxisRotation.R270, AxisRotation.R180);
    final static public ModelRotation X270_Y270 = new ModelRotation(AxisRotation.R270, AxisRotation.R270);
    final static private ModelRotation[][] ROTATION_MAP;
    final private AxisRotation xRotation;
    final private AxisRotation yRotation;
    final AffineTransformation rotation;
    final private DirectionTransformation directionTransformation;
    final Map<Direction, Matrix4fc> faces = new EnumMap<Direction, Matrix4fc>(Direction.class);
    final Map<Direction, Matrix4fc> invertedFaces = new EnumMap<Direction, Matrix4fc>(Direction.class);
    final private UVModel uvModel = new UVModel(this);
    final static private ModelRotation[] field_5365;

    public static ModelRotation[] values() {
        return (ModelRotation[])field_5365.clone();
    }

    public static ModelRotation valueOf(String string) {
        return Enum.valueOf(ModelRotation.class, string);
    }

    private ModelRotation(AxisRotation x, AxisRotation y) {
        this.xRotation = x;
        this.yRotation = y;
        this.directionTransformation = DirectionTransformation.fromRotations(x, y);
        this.rotation = this.directionTransformation != DirectionTransformation.IDENTITY ? new AffineTransformation((Matrix4fc)new Matrix4f(this.directionTransformation.getMatrix())) : AffineTransformation.identity();
        for (Direction direction : Direction.values()) {
            Matrix4fc matrix4fc = AffineTransformations.getTransformed(this.rotation, direction).getMatrix();
            this.faces.put(direction, matrix4fc);
            this.invertedFaces.put(direction, (Matrix4fc)matrix4fc.invertAffine(new Matrix4f()));
        }
    }

    @Override
    public AffineTransformation getRotation() {
        return this.rotation;
    }

    public static ModelRotation rotate(AxisRotation xRotation, AxisRotation yRotation) {
        return ROTATION_MAP[xRotation.ordinal()][yRotation.ordinal()];
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }

    public ModelBakeSettings getUVModel() {
        return this.uvModel;
    }

    private static ModelRotation[] method_36925() {
        return new ModelRotation[]{X0_Y0, X0_Y90, X0_Y180, X0_Y270, X90_Y0, X90_Y90, X90_Y180, X90_Y270, X180_Y0, X180_Y90, X180_Y180, X180_Y270, X270_Y0, X270_Y90, X270_Y180, X270_Y270};
    }

    static {
        field_5365 = ModelRotation.method_36925();
        ROTATION_MAP = Util.make(new ModelRotation[AxisRotation.values().length][AxisRotation.values().length], modelRotations -> {
            ModelRotation[] modelRotationArray = ModelRotation.values();
            int n = modelRotationArray.length;
            for (int i = 0; i < n; ++i) {
                ModelRotation modelRotation;
                modelRotations[modelRotation.xRotation.ordinal()][modelRotation.yRotation.ordinal()] = modelRotation = modelRotationArray[i];
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    record UVModel(ModelRotation parent) implements ModelBakeSettings
    {
        @Override
        public AffineTransformation getRotation() {
            return this.parent.rotation;
        }

        @Override
        public Matrix4fc forward(Direction facing) {
            return this.parent.faces.getOrDefault(facing, TRANSFORM_NONE);
        }

        @Override
        public Matrix4fc reverse(Direction facing) {
            return this.parent.invertedFaces.getOrDefault(facing, TRANSFORM_NONE);
        }
    }
}

