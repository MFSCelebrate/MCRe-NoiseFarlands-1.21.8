/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public final class ModelPart {
    final static public float field_37937 = 1.0f;
    public float originX;
    public float originY;
    public float originZ;
    public float pitch;
    public float yaw;
    public float roll;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;
    public boolean visible = true;
    public boolean hidden;
    final private List<Cuboid> cuboids;
    final private Map<String, ModelPart> children;
    private ModelTransform defaultTransform = ModelTransform.NONE;

    public ModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        this.cuboids = cuboids;
        this.children = children;
    }

    public ModelTransform getTransform() {
        return ModelTransform.of(this.originX, this.originY, this.originZ, this.pitch, this.yaw, this.roll);
    }

    public ModelTransform getDefaultTransform() {
        return this.defaultTransform;
    }

    public void setDefaultTransform(ModelTransform transform) {
        this.defaultTransform = transform;
    }

    public void resetTransform() {
        this.setTransform(this.defaultTransform);
    }

    public void setTransform(ModelTransform transform) {
        this.originX = transform.x();
        this.originY = transform.y();
        this.originZ = transform.z();
        this.pitch = transform.pitch();
        this.yaw = transform.yaw();
        this.roll = transform.roll();
        this.xScale = transform.xScale();
        this.yScale = transform.yScale();
        this.zScale = transform.zScale();
    }

    public void copyTransform(ModelPart part) {
        this.xScale = part.xScale;
        this.yScale = part.yScale;
        this.zScale = part.zScale;
        this.pitch = part.pitch;
        this.yaw = part.yaw;
        this.roll = part.roll;
        this.originX = part.originX;
        this.originY = part.originY;
        this.originZ = part.originZ;
    }

    public boolean hasChild(String child) {
        return this.children.containsKey(child);
    }

    public ModelPart getChild(String name) {
        ModelPart modelPart = this.children.get(name);
        if (modelPart == null) {
            throw new NoSuchElementException("Can't find part " + name);
        }
        return modelPart;
    }

    public void setOrigin(float x, float y, float z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }

    public void setAngles(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, -1);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.applyTransform(matrices);
        if (!this.hidden) {
            this.renderCuboids(matrices.peek(), vertices, light, overlay, color);
        }
        for (ModelPart modelPart : this.children.values()) {
            modelPart.render(matrices, vertices, light, overlay, color);
        }
        matrices.pop();
    }

    public void rotate(Quaternionf quaternion) {
        Matrix3f matrix3f = new Matrix3f().rotationZYX(this.roll, this.yaw, this.pitch);
        Matrix3f matrix3f2 = matrix3f.rotate((Quaternionfc)quaternion);
        Vector3f vector3f = matrix3f2.getEulerAnglesZYX(new Vector3f());
        this.setAngles(vector3f.x, vector3f.y, vector3f.z);
    }

    public void collectVertices(MatrixStack matrices, Set<Vector3f> vertices) {
        this.forEachCuboid(matrices, (matrix, path, index, cuboid) -> {
            for (Quad quad : cuboid.sides) {
                for (Vertex vertex : quad.vertices()) {
                    float f = vertex.pos().x() / 16.0f;
                    float g = vertex.pos().y() / 16.0f;
                    float h = vertex.pos().z() / 16.0f;
                    Vector3f vector3f = matrix.getPositionMatrix().transformPosition(f, g, h, new Vector3f());
                    vertices.add(vector3f);
                }
            }
        });
    }

    public void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer) {
        this.forEachCuboid(matrices, consumer, "");
    }

    private void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer, String path) {
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.applyTransform(matrices);
        MatrixStack.Entry entry = matrices.peek();
        for (int i = 0; i < this.cuboids.size(); ++i) {
            consumer.accept(entry, path, i, this.cuboids.get(i));
        }
        String string = path + "/";
        this.children.forEach((name, part) -> part.forEachCuboid(matrices, consumer, string + name));
        matrices.pop();
    }

    public void applyTransform(MatrixStack matrices) {
        matrices.translate(this.originX / 16.0f, this.originY / 16.0f, this.originZ / 16.0f);
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            matrices.multiply((Quaternionfc)new Quaternionf().rotationZYX(this.roll, this.yaw, this.pitch));
        }
        if (this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            matrices.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        for (Cuboid cuboid : this.cuboids) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, color);
        }
    }

    public Cuboid getRandomCuboid(Random random) {
        return this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    public boolean isEmpty() {
        return this.cuboids.isEmpty();
    }

    public void moveOrigin(Vector3f vec3f) {
        this.originX += vec3f.x();
        this.originY += vec3f.y();
        this.originZ += vec3f.z();
    }

    public void rotate(Vector3f vec3f) {
        this.pitch += vec3f.x();
        this.yaw += vec3f.y();
        this.roll += vec3f.z();
    }

    public void scale(Vector3f vec3f) {
        this.xScale += vec3f.x();
        this.yScale += vec3f.y();
        this.zScale += vec3f.z();
    }

    public List<ModelPart> traverse() {
        ArrayList<ModelPart> list = new ArrayList<ModelPart>();
        list.add(this);
        this.forEachChild((key, part) -> list.add((ModelPart)part));
        return List.copyOf(list);
    }

    public Function<String, ModelPart> createPartGetter() {
        HashMap<String, ModelPart> map = new HashMap<String, ModelPart>();
        map.put("root", this);
        this.forEachChild(map::putIfAbsent);
        return map::get;
    }

    private void forEachChild(BiConsumer<String, ModelPart> partBiConsumer) {
        for (Map.Entry<String, ModelPart> entry : this.children.entrySet()) {
            partBiConsumer.accept(entry.getKey(), entry.getValue());
        }
        for (ModelPart modelPart : this.children.values()) {
            modelPart.forEachChild(partBiConsumer);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface CuboidConsumer {
        public void accept(MatrixStack.Entry var1, String var2, int var3, Cuboid var4);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cuboid {
        final public Quad[] sides;
        final public float minX;
        final public float minY;
        final public float minZ;
        final public float maxX;
        final public float maxY;
        final public float maxZ;

        public Cuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> sides) {
            this.minX = x;
            this.minY = y;
            this.minZ = z;
            this.maxX = x + sizeX;
            this.maxY = y + sizeY;
            this.maxZ = z + sizeZ;
            this.sides = new Quad[sides.size()];
            float f = x + sizeX;
            float g = y + sizeY;
            float h = z + sizeZ;
            x -= extraX;
            y -= extraY;
            z -= extraZ;
            f += extraX;
            g += extraY;
            h += extraZ;
            if (mirror) {
                float i = f;
                f = x;
                x = i;
            }
            Vertex vertex = new Vertex(x, y, z, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(f, y, z, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(f, g, z, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(x, g, z, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(x, y, h, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(f, y, h, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(f, g, h, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(x, g, h, 8.0f, 0.0f);
            float j = u;
            float k = (float)u + sizeZ;
            float l = (float)u + sizeZ + sizeX;
            float m = (float)u + sizeZ + sizeX + sizeX;
            float n = (float)u + sizeZ + sizeX + sizeZ;
            float o = (float)u + sizeZ + sizeX + sizeZ + sizeX;
            float p = v;
            float q = (float)v + sizeZ;
            float r = (float)v + sizeZ + sizeY;
            int s = 0;
            if (sides.contains(Direction.DOWN)) {
                this.sides[s++] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN);
            }
            if (sides.contains(Direction.UP)) {
                this.sides[s++] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP);
            }
            if (sides.contains(Direction.WEST)) {
                this.sides[s++] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST);
            }
            if (sides.contains(Direction.NORTH)) {
                this.sides[s++] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH);
            }
            if (sides.contains(Direction.EAST)) {
                this.sides[s++] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
            }
            if (sides.contains(Direction.SOUTH)) {
                this.sides[s] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
            }
        }

        public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
            Matrix4f matrix4f = entry.getPositionMatrix();
            Vector3f vector3f = new Vector3f();
            for (Quad quad : this.sides) {
                Vector3f vector3f2 = entry.transformNormal((Vector3fc)quad.direction, vector3f);
                float f = vector3f2.x();
                float g = vector3f2.y();
                float h = vector3f2.z();
                for (Vertex vertex : quad.vertices) {
                    float i = vertex.pos.x() / 16.0f;
                    float j = vertex.pos.y() / 16.0f;
                    float k = vertex.pos.z() / 16.0f;
                    Vector3f vector3f3 = matrix4f.transformPosition(i, j, k, vector3f);
                    vertexConsumer.vertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), color, vertex.u, vertex.v, overlay, light, f, g, h);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Quad
    extends Record {
        final Vertex[] vertices;
        final Vector3f direction;

        public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
            this(vertices, direction.getUnitVector());
            float f = 0.0f / squishU;
            float g = 0.0f / squishV;
            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
            if (flip) {
                int i = vertices.length;
                for (int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }
            if (flip) {
                this.direction.mul(-1.0f, 1.0f, 1.0f);
            }
        }

        public Quad(Vertex[] vertexs, Vector3f vector3f) {
            this.vertices = vertexs;
            this.direction = vector3f;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Quad.class, "vertices;normal", "vertices", "direction"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Quad.class, "vertices;normal", "vertices", "direction"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Quad.class, "vertices;normal", "vertices", "direction"}, this, object);
        }

        public Vertex[] vertices() {
            return this.vertices;
        }

        public Vector3f direction() {
            return this.direction;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Vertex
    extends Record {
        final Vector3f pos;
        final float u;
        final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Vertex.class, "pos;u;v", "pos", "u", "v"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Vertex.class, "pos;u;v", "pos", "u", "v"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Vertex.class, "pos;u;v", "pos", "u", "v"}, this, object);
        }

        public Vector3f pos() {
            return this.pos;
        }

        public float u() {
            return this.u;
        }

        public float v() {
            return this.v;
        }
    }
}

