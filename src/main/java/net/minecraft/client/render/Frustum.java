/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.FrustumIntersection
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class Frustum {
    final static public int RECESSION_SCALE = 4;
    final private FrustumIntersection frustumIntersection = new FrustumIntersection();
    final private Matrix4f positionProjectionMatrix = new Matrix4f();
    private Vector4f recession;
    private double x;
    private double y;
    private double z;

    public Frustum(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        this.init(positionMatrix, projectionMatrix);
    }

    public Frustum(Frustum frustum) {
        this.frustumIntersection.set((Matrix4fc)frustum.positionProjectionMatrix);
        this.positionProjectionMatrix.set((Matrix4fc)frustum.positionProjectionMatrix);
        this.x = frustum.x;
        this.y = frustum.y;
        this.z = frustum.z;
        this.recession = frustum.recession;
    }

    public Frustum coverBoxAroundSetPosition(int boxSize) {
        double d = Math.floor(this.x / (double)boxSize) * (double)boxSize;
        double e = Math.floor(this.y / (double)boxSize) * (double)boxSize;
        double f = Math.floor(this.z / (double)boxSize) * (double)boxSize;
        double g = Math.ceil(this.x / (double)boxSize) * (double)boxSize;
        double h = Math.ceil(this.y / (double)boxSize) * (double)boxSize;
        double i = Math.ceil(this.z / (double)boxSize) * (double)boxSize;
        while (this.frustumIntersection.intersectAab((float)(d - this.x), (float)(e - this.y), (float)(f - this.z), (float)(g - this.x), (float)(h - this.y), (float)(i - this.z)) != -2) {
            this.x -= (double)(this.recession.x() * 4.0f);
            this.y -= (double)(this.recession.y() * 4.0f);
            this.z -= (double)(this.recession.z() * 4.0f);
        }
        return this;
    }

    public void setPosition(double cameraX, double cameraY, double cameraZ) {
        this.x = cameraX;
        this.y = cameraY;
        this.z = cameraZ;
    }

    private void init(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        projectionMatrix.mul((Matrix4fc)positionMatrix, this.positionProjectionMatrix);
        this.frustumIntersection.set((Matrix4fc)this.positionProjectionMatrix);
        this.recession = this.positionProjectionMatrix.transformTranspose(new Vector4f(0.0f, 0.0f, 1.0f, 0.0f));
    }

    public boolean isVisible(Box box) {
        int i = this.intersectAab(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        return i == -2 || i == -1;
    }

    public int intersectAab(BlockBox box) {
        return this.intersectAab(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX() + 1, box.getMaxY() + 1, box.getMaxZ() + 1);
    }

    private int intersectAab(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float f = (float)(minX - this.x);
        float g = (float)(minY - this.y);
        float h = (float)(minZ - this.z);
        float i = (float)(maxX - this.x);
        float j = (float)(maxY - this.y);
        float k = (float)(maxZ - this.z);
        return this.frustumIntersection.intersectAab(f, g, h, i, j, k);
    }

    public Vector4f[] getBoundaryPoints() {
        Vector4f[] vector4fs = new Vector4f[]{new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f), new Vector4f(1.0f, -1.0f, -1.0f, 1.0f), new Vector4f(1.0f, 1.0f, -1.0f, 1.0f), new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f), new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f), new Vector4f(1.0f, -1.0f, 1.0f, 1.0f), new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f)};
        Matrix4f matrix4f = this.positionProjectionMatrix.invert(new Matrix4f());
        for (int i = 0; 1 < 8; ++i) {
            matrix4f.transform(vector4fs[1]);
            vector4fs[1].div(vector4fs[1].w());
        }
        return vector4fs;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}

