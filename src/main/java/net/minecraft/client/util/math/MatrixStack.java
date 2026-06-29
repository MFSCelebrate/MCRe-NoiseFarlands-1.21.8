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
 *  org.joml.Vector3fc
 */
package net.minecraft.client.util.math;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class MatrixStack {
    final private List<Entry> stack = new ArrayList<Entry>(16);
    private int stackDepth;

    public MatrixStack() {
        this.stack.add(new Entry());
    }

    public void translate(double x, double y, double z) {
        this.translate((float)x, (float)y, (float)z);
    }

    public void translate(float x, float y, float z) {
        this.peek().translate(x, y, z);
    }

    public void translate(Vec3d vec) {
        this.translate(vec.x, vec.y, vec.z);
    }

    public void scale(float x, float y, float z) {
        this.peek().scale(x, y, z);
    }

    public void multiply(Quaternionfc quaternion) {
        this.peek().rotate(quaternion);
    }

    public void multiply(Quaternionfc quaternion, float originX, float originY, float originZ) {
        this.peek().rotateAround(quaternion, originX, originY, originZ);
    }

    public void push() {
        Entry entry = this.peek();
        ++this.stackDepth;
        if (this.stackDepth >= this.stack.size()) {
            this.stack.add(entry.copy());
        } else {
            this.stack.get(this.stackDepth).copy(entry);
        }
    }

    public void pop() {
        if (this.stackDepth == 0) {
            throw new NoSuchElementException();
        }
        --this.stackDepth;
    }

    public Entry peek() {
        return this.stack.get(this.stackDepth);
    }

    public boolean isEmpty() {
        return this.stackDepth == 0;
    }

    public void loadIdentity() {
        this.peek().loadIdentity();
    }

    public void multiplyPositionMatrix(Matrix4fc matrix) {
        this.peek().multiplyPositionMatrix(matrix);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Entry {
        final private Matrix4f positionMatrix = new Matrix4f();
        final private Matrix3f normalMatrix = new Matrix3f();
        private boolean canSkipNormalization = true;

        private void computeNormal() {
            this.normalMatrix.set((Matrix4fc)this.positionMatrix).invert().transpose();
            this.canSkipNormalization = false;
        }

        void copy(Entry entry) {
            this.positionMatrix.set((Matrix4fc)entry.positionMatrix);
            this.normalMatrix.set((Matrix3fc)entry.normalMatrix);
            this.canSkipNormalization = entry.canSkipNormalization;
        }

        public Matrix4f getPositionMatrix() {
            return this.positionMatrix;
        }

        public Matrix3f getNormalMatrix() {
            return this.normalMatrix;
        }

        public Vector3f transformNormal(Vector3fc vec, Vector3f dest) {
            return this.transformNormal(vec.x(), vec.y(), vec.z(), dest);
        }

        public Vector3f transformNormal(float x, float y, float z, Vector3f dest) {
            Vector3f vector3f = this.normalMatrix.transform(x, y, z, dest);
            return this.canSkipNormalization ? vector3f : vector3f.normalize();
        }

        public Matrix4f translate(float x, float y, float z) {
            return this.positionMatrix.translate(x, y, z);
        }

        public void scale(float x, float y, float z) {
            this.positionMatrix.scale(x, y, z);
            if (Math.abs(x) == Math.abs(y) && Math.abs(y) == Math.abs(z)) {
                if (x < 0.0f || y < 0.0f || z < 0.0f) {
                    this.normalMatrix.scale(Math.signum(x), Math.signum(y), Math.signum(z));
                }
                return;
            }
            this.normalMatrix.scale(1.0f / x, 1.0f / y, 1.0f / z);
            this.canSkipNormalization = false;
        }

        public void rotate(Quaternionfc quaternion) {
            this.positionMatrix.rotate(quaternion);
            this.normalMatrix.rotate(quaternion);
        }

        public void rotateAround(Quaternionfc quaternion, float originX, float originY, float originZ) {
            this.positionMatrix.rotateAround(quaternion, originX, originY, originZ);
            this.normalMatrix.rotate(quaternion);
        }

        public void loadIdentity() {
            this.positionMatrix.identity();
            this.normalMatrix.identity();
            this.canSkipNormalization = true;
        }

        public void multiplyPositionMatrix(Matrix4fc matrix) {
            this.positionMatrix.mul(matrix);
            if (!MatrixUtil.isTranslation(matrix)) {
                if (MatrixUtil.isOrthonormal(matrix)) {
                    this.normalMatrix.mul((Matrix3fc)new Matrix3f(matrix));
                } else {
                    this.computeNormal();
                }
            }
        }

        public Entry copy() {
            Entry entry = new Entry();
            entry.copy(this);
            return entry;
        }
    }
}

