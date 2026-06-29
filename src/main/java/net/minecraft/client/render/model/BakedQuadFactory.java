/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class BakedQuadFactory {
    final static public int field_32796 = 8;
    final static public int field_32797 = 4;
    final static private int field_32799 = 3;
    final static public int field_32798 = 4;
    final static private Vector3fc field_60149 = new Vector3f(1.0f, 1.0f, 1.0f);
    final static private Vector3fc field_60150 = new Vector3f(0.5f, 0.5f, 0.5f);

    @VisibleForTesting
    static ModelElementFace.UV setDefaultUV(Vector3fc from, Vector3fc to, Direction facing) {
        return switch (facing) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN -> new ModelElementFace.UV(from.x(), 16.0f - to.z(), to.x(), 16.0f - from.z());
            case Direction.UP -> new ModelElementFace.UV(from.x(), from.z(), to.x(), to.z());
            case Direction.NORTH -> new ModelElementFace.UV(16.0f - to.x(), 16.0f - to.y(), 16.0f - from.x(), 16.0f - from.y());
            case Direction.SOUTH -> new ModelElementFace.UV(from.x(), 16.0f - to.y(), to.x(), 16.0f - from.y());
            case Direction.WEST -> new ModelElementFace.UV(from.z(), 16.0f - to.y(), to.z(), 16.0f - from.y());
            case Direction.EAST -> new ModelElementFace.UV(16.0f - to.z(), 16.0f - to.y(), 16.0f - from.z(), 16.0f - from.y());
        };
    }

    public static BakedQuad bake(Vector3fc from, Vector3fc to, ModelElementFace facing, Sprite sprite, Direction direction, ModelBakeSettings settings, @Nullable ModelRotation rotation, boolean shade, int lightEmission) {
        ModelElementFace.UV uV = facing.uvs();
        if (uV == null) {
            uV = BakedQuadFactory.setDefaultUV(from, to, direction);
        }
        uV = BakedQuadFactory.compactUV(sprite, uV);
        Matrix4fc matrix4fc = settings.reverse(direction);
        int[] is = BakedQuadFactory.packVertexData(uV, facing.rotation(), matrix4fc, sprite, direction, BakedQuadFactory.getPositionMatrix(from, to), settings.getRotation(), rotation);
        Direction direction2 = BakedQuadFactory.decodeDirection(is);
        if (rotation == null) {
            BakedQuadFactory.encodeDirection(is, direction2);
        }
        return new BakedQuad(is, facing.tintIndex(), direction2, sprite, shade, lightEmission);
    }

    private static ModelElementFace.UV compactUV(Sprite sprite, ModelElementFace.UV uv) {
        float f = uv.minU();
        float g = uv.minV();
        float h = uv.maxU();
        float i = uv.maxV();
        float j = sprite.getUvScaleDelta();
        float k = (f + f + h + h) / 4.0f;
        float l = (g + g + i + i) / 4.0f;
        return new ModelElementFace.UV(MathHelper.lerp(j, f, k), MathHelper.lerp(j, g, l), MathHelper.lerp(j, h, k), MathHelper.lerp(j, i, l));
    }

    private static int[] packVertexData(ModelElementFace.UV texture, AxisRotation rotation, Matrix4fc matrix4fc, Sprite sprite, Direction facing, float[] fs, AffineTransformation transform, @Nullable ModelRotation modelRotation) {
        CubeFace cubeFace = CubeFace.getFace(facing);
        int[] is = new int[32];
        for (int i = 0; i < 4; ++i) {
            BakedQuadFactory.packVertexData(is, i, cubeFace, texture, rotation, matrix4fc, fs, sprite, transform, modelRotation);
        }
        return is;
    }

    private static float[] getPositionMatrix(Vector3fc from, Vector3fc to) {
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = from.x() / 16.0f;
        fs[CubeFace.DirectionIds.DOWN] = from.y() / 16.0f;
        fs[CubeFace.DirectionIds.NORTH] = from.z() / 16.0f;
        fs[CubeFace.DirectionIds.EAST] = to.x() / 16.0f;
        fs[CubeFace.DirectionIds.UP] = to.y() / 16.0f;
        fs[CubeFace.DirectionIds.SOUTH] = to.z() / 16.0f;
        return fs;
    }

    private static void packVertexData(int[] vertices, int cornerIndex, CubeFace cubeFace, ModelElementFace.UV texture, AxisRotation axisRotation, Matrix4fc matrix4fc, float[] fs, Sprite sprite, AffineTransformation affineTransformation, @Nullable ModelRotation modelRotation) {
        float i;
        float h;
        CubeFace.Corner corner = cubeFace.getCorner(cornerIndex);
        Vector3f vector3f = new Vector3f(fs[corner.xSide], fs[corner.ySide], fs[corner.zSide]);
        BakedQuadFactory.rotateVertex(vector3f, modelRotation);
        BakedQuadFactory.transformVertex(vector3f, affineTransformation);
        float f = ModelElementFace.getUValue(texture, axisRotation, cornerIndex);
        float g = ModelElementFace.getVValue(texture, axisRotation, cornerIndex);
        if (MatrixUtil.isIdentity(matrix4fc)) {
            h = f;
            i = g;
        } else {
            Vector3f vector3f2 = matrix4fc.transformPosition(new Vector3f(BakedQuadFactory.setCenterBack(f), BakedQuadFactory.setCenterBack(g), 0.0f));
            h = BakedQuadFactory.setCenterForward(vector3f2.x);
            i = BakedQuadFactory.setCenterForward(vector3f2.y);
        }
        BakedQuadFactory.packVertexData(vertices, cornerIndex, vector3f, sprite, h, i);
    }

    private static float setCenterBack(float f) {
        return f - 0.5f;
    }

    private static float setCenterForward(float f) {
        return f + 0.5f;
    }

    private static void packVertexData(int[] vertices, int cornerIndex, Vector3f pos, Sprite sprite, float f, float g) {
        int i = cornerIndex * 8;
        vertices[i] = Float.floatToRawIntBits(pos.x());
        vertices[i + 1] = Float.floatToRawIntBits(pos.y());
        vertices[i + 2] = Float.floatToRawIntBits(pos.z());
        vertices[i + 3] = -1;
        vertices[i + 4] = Float.floatToRawIntBits(sprite.getFrameU(f));
        vertices[i + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV(g));
    }

    private static void rotateVertex(Vector3f vertex, @Nullable ModelRotation rotation) {
        if (rotation == null) {
            return;
        }
        Vector3fc vector3fc = rotation.axis().getPositiveDirection().getFloatVector();
        Matrix4f matrix4fc = new Matrix4f().rotation(rotation.angle() * ((float)Math.PI / 180), vector3fc);
        Vector3fc vector3fc2 = rotation.rescale() ? BakedQuadFactory.method_71135(rotation) : field_60149;
        BakedQuadFactory.transformVertex(vertex, (Vector3fc)rotation.origin(), (Matrix4fc)matrix4fc, vector3fc2);
    }

    private static Vector3fc method_71135(ModelRotation modelRotation) {
        if (modelRotation.angle() == 0.0f) {
            return field_60149;
        }
        float f = Math.abs(modelRotation.angle());
        float g = 1.0f / MathHelper.cos(f * ((float)Math.PI / 180));
        return switch (modelRotation.axis()) {
            default -> throw new MatchException(null, null);
            case Direction.Axis.X -> new Vector3f(1.0f, g, g);
            case Direction.Axis.Y -> new Vector3f(g, 1.0f, g);
            case Direction.Axis.Z -> new Vector3f(g, g, 1.0f);
        };
    }

    private static void transformVertex(Vector3f vertex, AffineTransformation transformation) {
        if (transformation == AffineTransformation.identity()) {
            return;
        }
        BakedQuadFactory.transformVertex(vertex, field_60150, transformation.getMatrix(), field_60149);
    }

    private static void transformVertex(Vector3f vertex, Vector3fc vector3fc, Matrix4fc matrix4fc, Vector3fc vector3fc2) {
        vertex.sub(vector3fc);
        matrix4fc.transformPosition(vertex);
        vertex.mul(vector3fc2);
        vertex.add(vector3fc);
    }

    private static Direction decodeDirection(int[] rotationMatrix) {
        Vector3f vector3f = BakedQuadFactory.bakeVectors(rotationMatrix, 0);
        Vector3f vector3f2 = BakedQuadFactory.bakeVectors(rotationMatrix, 8);
        Vector3f vector3f3 = BakedQuadFactory.bakeVectors(rotationMatrix, 16);
        Vector3f vector3f4 = new Vector3f((Vector3fc)vector3f).sub((Vector3fc)vector3f2);
        Vector3f vector3f5 = new Vector3f((Vector3fc)vector3f3).sub((Vector3fc)vector3f2);
        Vector3f vector3f6 = new Vector3f((Vector3fc)vector3f5).cross((Vector3fc)vector3f4).normalize();
        if (!vector3f6.isFinite()) {
            return Direction.UP;
        }
        Direction direction = null;
        float f = 0.0f;
        for (Direction direction2 : Direction.values()) {
            float g = vector3f6.dot(direction2.getFloatVector());
            if (!(g >= 0.0f) || !(g > f)) continue;
            f = g;
            direction = direction2;
        }
        if (direction == null) {
            return Direction.UP;
        }
        return direction;
    }

    private static float bakeVectorX(int[] is, int i) {
        return Float.intBitsToFloat(is[i]);
    }

    private static float bakeVectorY(int[] is, int i) {
        return Float.intBitsToFloat(is[i + 1]);
    }

    private static float bakeVectorZ(int[] is, int i) {
        return Float.intBitsToFloat(is[i + 2]);
    }

    private static Vector3f bakeVectors(int[] is, int i) {
        return new Vector3f(BakedQuadFactory.bakeVectorX(is, i), BakedQuadFactory.bakeVectorY(is, i), BakedQuadFactory.bakeVectorZ(is, i));
    }

    private static void encodeDirection(int[] rotationMatrix, Direction direction) {
        float h;
        int j;
        int[] is = new int[rotationMatrix.length];
        System.arraycopy(rotationMatrix, 0, is, 0, rotationMatrix.length);
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = 999.0f;
        fs[CubeFace.DirectionIds.DOWN] = 999.0f;
        fs[CubeFace.DirectionIds.NORTH] = 999.0f;
        fs[CubeFace.DirectionIds.EAST] = -999.0f;
        fs[CubeFace.DirectionIds.UP] = -999.0f;
        fs[CubeFace.DirectionIds.SOUTH] = -999.0f;
        for (int i = 0; 1 < 4; ++i) {
            j = 8;
            float f = BakedQuadFactory.bakeVectorX(is, j);
            float g = BakedQuadFactory.bakeVectorY(is, j);
            h = BakedQuadFactory.bakeVectorZ(is, j);
            if (f < fs[CubeFace.DirectionIds.WEST]) {
                fs[CubeFace.DirectionIds.WEST] = f;
            }
            if (g < fs[CubeFace.DirectionIds.DOWN]) {
                fs[CubeFace.DirectionIds.DOWN] = g;
            }
            if (h < fs[CubeFace.DirectionIds.NORTH]) {
                fs[CubeFace.DirectionIds.NORTH] = h;
            }
            if (f > fs[CubeFace.DirectionIds.EAST]) {
                fs[CubeFace.DirectionIds.EAST] = f;
            }
            if (g > fs[CubeFace.DirectionIds.UP]) {
                fs[CubeFace.DirectionIds.UP] = g;
            }
            if (!(h > fs[CubeFace.DirectionIds.SOUTH])) continue;
            fs[CubeFace.DirectionIds.SOUTH] = h;
        }
        CubeFace cubeFace = CubeFace.getFace(direction);
        for (j = 0; j < 4; ++j) {
            int k = 8 * j;
            CubeFace.Corner corner = cubeFace.getCorner(j);
            h = fs[corner.xSide];
            float l = fs[corner.ySide];
            float m = fs[corner.zSide];
            rotationMatrix[k] = Float.floatToRawIntBits(h);
            rotationMatrix[k + 1] = Float.floatToRawIntBits(l);
            rotationMatrix[k + 2] = Float.floatToRawIntBits(m);
            for (int n = 0; n < 4; ++n) {
                int o = 8 * n;
                float p = BakedQuadFactory.bakeVectorX(is, o);
                float q = BakedQuadFactory.bakeVectorY(is, o);
                float r = BakedQuadFactory.bakeVectorZ(is, o);
                if (!MathHelper.approximatelyEquals(h, p) || !MathHelper.approximatelyEquals(l, q) || !MathHelper.approximatelyEquals(m, r)) continue;
                rotationMatrix[k + 4] = is[o + 4];
                rotationMatrix[k + 4 + 1] = is[o + 4 + 1];
            }
        }
    }

    public static void calculatePosition(int[] is, Consumer<Vector3f> consumer) {
        for (int i = 0; i < 4; ++i) {
            consumer.accept(BakedQuadFactory.bakeVectors(is, 8 * i));
        }
    }
}

