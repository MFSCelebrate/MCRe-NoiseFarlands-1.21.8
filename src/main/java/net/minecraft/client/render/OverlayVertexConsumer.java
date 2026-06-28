/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class OverlayVertexConsumer
implements VertexConsumer {
    final private VertexConsumer delegate;
    final private Matrix4f inverseTextureMatrix;
    final private Matrix3f inverseNormalMatrix;
    final private float textureScale;
    final private Vector3f normal = new Vector3f();
    final private Vector3f pos = new Vector3f();
    private float x;
    private float y;
    private float z;

    public OverlayVertexConsumer(VertexConsumer delegate, MatrixStack.Entry matrix, float textureScale) {
        this.delegate = delegate;
        this.inverseTextureMatrix = new Matrix4f((Matrix4fc)matrix.getPositionMatrix()).invert();
        this.inverseNormalMatrix = new Matrix3f((Matrix3fc)matrix.getNormalMatrix()).invert();
        this.textureScale = textureScale;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        this.delegate.color(Colors.WHITE);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        this.delegate.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        this.delegate.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.delegate.normal(x, y, z);
        Vector3f vector3f = this.inverseNormalMatrix.transform(x, y, z, this.pos);
        Direction direction = Direction.getFacing(vector3f.x(), vector3f.y(), vector3f.z());
        Vector3f vector3f2 = this.inverseTextureMatrix.transformPosition(this.x, this.y, this.z, this.normal);
        vector3f2.rotateY((float)Math.PI);
        vector3f2.rotateX(-1.5707964f);
        vector3f2.rotate((Quaternionfc)direction.getRotationQuaternion());
        this.delegate.texture(-vector3f2.x() * this.textureScale, -vector3f2.y() * this.textureScale);
        return this;
    }
}

