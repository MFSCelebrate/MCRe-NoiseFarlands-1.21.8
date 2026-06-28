/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class DiffuseLighting
implements AutoCloseable {
    final static private Vector3f DEFAULT_DIFFUSION_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    final static private Vector3f DEFAULT_DIFFUSION_LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    final static private Vector3f DARKENED_DIFFUSION_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    final static private Vector3f DARKENED_DIFFUSION_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.7f).normalize();
    final static private Vector3f INVENTORY_DIFFUSION_LIGHT_0 = new Vector3f(0.2f, -1.0f, 1.0f).normalize();
    final static private Vector3f INVENTORY_DIFFUSION_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.0f).normalize();
    final static public int UBO_SIZE = new Std140SizeCalculator().putVec3().putVec3().get();
    final private GpuBuffer buffer;
    final private int roundedUboSize;

    public DiffuseLighting() {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.roundedUboSize = MathHelper.roundUpToMultiple(UBO_SIZE, gpuDevice.getUniformOffsetAlignment());
        this.buffer = gpuDevice.createBuffer(() -> "Lighting UBO", 136, this.roundedUboSize * Type.values().length);
        Matrix4f matrix4f = new Matrix4f().rotationY(-0.3926991f).rotateX(2.3561945f);
        this.updateBuffer(Type.ITEMS_FLAT, matrix4f.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_0, new Vector3f()), matrix4f.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_1, new Vector3f()));
        Matrix4f matrix4f2 = new Matrix4f().scaling(1.0f, -1.0f, 1.0f).rotateYXZ(1.0821041f, 3.2375858f, 0.0f).rotateYXZ(-0.3926991f, 2.3561945f, 0.0f);
        this.updateBuffer(Type.ITEMS_3D, matrix4f2.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_0, new Vector3f()), matrix4f2.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_1, new Vector3f()));
        this.updateBuffer(Type.ENTITY_IN_UI, INVENTORY_DIFFUSION_LIGHT_0, INVENTORY_DIFFUSION_LIGHT_1);
        Matrix4f matrix4f3 = new Matrix4f();
        this.updateBuffer(Type.PLAYER_SKIN, matrix4f3.transformDirection((Vector3fc)INVENTORY_DIFFUSION_LIGHT_0, new Vector3f()), matrix4f3.transformDirection((Vector3fc)INVENTORY_DIFFUSION_LIGHT_1, new Vector3f()));
    }

    public void updateLevelBuffer(boolean darkened) {
        if (darkened) {
            this.updateBuffer(Type.LEVEL, DARKENED_DIFFUSION_LIGHT_0, DARKENED_DIFFUSION_LIGHT_1);
        } else {
            this.updateBuffer(Type.LEVEL, DEFAULT_DIFFUSION_LIGHT_0, DEFAULT_DIFFUSION_LIGHT_1);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void updateBuffer(Type type, Vector3f light0Diffusion, Vector3f light1Diffusion) {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, UBO_SIZE).putVec3((Vector3fc)light0Diffusion).putVec3((Vector3fc)light1Diffusion).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(type.ordinal() * this.roundedUboSize, this.roundedUboSize), byteBuffer);
            if (memoryStack == null) return;
        }
    }

    public void setShaderLights(Type type) {
        RenderSystem.setShaderLights(this.buffer.slice(type.ordinal() * this.roundedUboSize, UBO_SIZE));
    }

    @Override
    public void close() {
        this.buffer.close();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        final static public Type LEVEL = new Type();
        final static public Type ITEMS_FLAT = new Type();
        final static public Type ITEMS_3D = new Type();
        final static public Type ENTITY_IN_UI = new Type();
        final static public Type PLAYER_SKIN = new Type();
        final static private Type[] field_60030;

        public static Type[] values() {
            return (Type[])field_60030.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static Type[] method_71037() {
            return new Type[]{LEVEL, ITEMS_FLAT, ITEMS_3D, ENTITY_IN_UI, PLAYER_SKIN};
        }

        static {
            field_60030 = Type.method_71037();
        }
    }
}

