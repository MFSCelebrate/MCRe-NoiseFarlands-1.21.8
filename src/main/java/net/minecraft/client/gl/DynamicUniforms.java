/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.DynamicUniformStorage;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class DynamicUniforms
implements AutoCloseable {
    final static public int SIZE = new Std140SizeCalculator().putMat4f().putVec4().putVec3().putMat4f().putFloat().get();
    final static private int DEFAULT_CAPACITY = 2;
    final private DynamicUniformStorage<UniformValue> storage = new DynamicUniformStorage("Dynamic Transforms UBO", SIZE, 2);

    public void clear() {
        this.storage.clear();
    }

    @Override
    public void close() {
        this.storage.close();
    }

    public GpuBufferSlice write(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth) {
        return this.storage.write(new UniformValue((Matrix4fc)new Matrix4f(modelView), (Vector4fc)new Vector4f(colorModulator), (Vector3fc)new Vector3f(modelOffset), (Matrix4fc)new Matrix4f(textureMatrix), lineWidth));
    }

    public GpuBufferSlice[] writeAll(UniformValue ... values) {
        return this.storage.writeAll(values);
    }

    @Environment(value=EnvType.CLIENT)
    public record UniformValue(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth) implements DynamicUniformStorage.Uploadable
    {
        @Override
        public void write(ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix).putFloat(this.lineWidth);
        }
    }
}

