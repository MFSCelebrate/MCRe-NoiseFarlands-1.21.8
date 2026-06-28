/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public interface UniformValue {
    final static public Codec<UniformValue> CODEC = Type.CODEC.dispatch(UniformValue::getType, type -> type.mapCodec);

    public void write(Std140Builder var1);

    public void addSize(Std140SizeCalculator var1);

    public Type getType();

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type>
    implements StringIdentifiable {
        final static public Type INT = new Type("int", IntValue.CODEC);
        final static public Type IVEC3 = new Type("ivec3", Vec3iValue.CODEC);
        final static public Type FLOAT = new Type("float", FloatValue.CODEC);
        final static public Type VEC2 = new Type("vec2", Vec2fValue.CODEC);
        final static public Type VEC3 = new Type("vec3", Vec3fValue.CODEC);
        final static public Type VEC4 = new Type("vec4", Vec4fValue.CODEC);
        final static public Type MATRIX4X4 = new Type("matrix4x4", Matrix4fValue.CODEC);
        final static public StringIdentifiable.EnumCodec<Type> CODEC;
        final private String name;
        final MapCodec<? extends UniformValue> mapCodec;
        final static private Type[] field_60145;

        public static Type[] values() {
            return (Type[])field_60145.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String name, Codec<? extends UniformValue> codec) {
            this.name = name;
            this.mapCodec = codec.fieldOf("value");
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Type[] method_71134() {
            return new Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
        }

        static {
            field_60145 = Type.method_71134();
            CODEC = StringIdentifiable.createCodec(Type::values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Matrix4fValue(Matrix4fc value) implements UniformValue
    {
        final static public Codec<Matrix4fValue> CODEC = Codecs.MATRIX_4F.xmap(Matrix4fValue::new, Matrix4fValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putMat4f(this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putMat4f();
        }

        @Override
        public Type getType() {
            return Type.MATRIX4X4;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Vec4fValue(Vector4f value) implements UniformValue
    {
        final static public Codec<Vec4fValue> CODEC = Codecs.VECTOR_4F.xmap(Vec4fValue::new, Vec4fValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putVec4((Vector4fc)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putVec4();
        }

        @Override
        public Type getType() {
            return Type.VEC4;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Vec3fValue(Vector3f value) implements UniformValue
    {
        final static public Codec<Vec3fValue> CODEC = Codecs.VECTOR_3F.xmap(Vec3fValue::new, Vec3fValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putVec3((Vector3fc)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putVec3();
        }

        @Override
        public Type getType() {
            return Type.VEC3;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Vec2fValue(Vector2f value) implements UniformValue
    {
        final static public Codec<Vec2fValue> CODEC = Codecs.VECTOR_2F.xmap(Vec2fValue::new, Vec2fValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putVec2((Vector2fc)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putVec2();
        }

        @Override
        public Type getType() {
            return Type.VEC2;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record FloatValue(float value) implements UniformValue
    {
        final static public Codec<FloatValue> CODEC = Codec.FLOAT.xmap(FloatValue::new, FloatValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putFloat(this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putFloat();
        }

        @Override
        public Type getType() {
            return Type.FLOAT;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Vec3iValue(Vector3i value) implements UniformValue
    {
        final static public Codec<Vec3iValue> CODEC = Codecs.VECTOR_3I.xmap(Vec3iValue::new, Vec3iValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putIVec3((Vector3ic)this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putIVec3();
        }

        @Override
        public Type getType() {
            return Type.IVEC3;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record IntValue(int value) implements UniformValue
    {
        final static public Codec<IntValue> CODEC = Codec.INT.xmap(IntValue::new, IntValue::value);

        @Override
        public void write(Std140Builder builder) {
            builder.putInt(this.value);
        }

        @Override
        public void addSize(Std140SizeCalculator calculator) {
            calculator.putInt();
        }

        @Override
        public Type getType() {
            return Type.INT;
        }
    }
}

