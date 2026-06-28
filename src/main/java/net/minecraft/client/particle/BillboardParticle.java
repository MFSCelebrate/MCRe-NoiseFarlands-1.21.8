/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public abstract class BillboardParticle
extends Particle {
    protected float scale;

    protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    public Rotator getRotator() {
        return Rotator.ALL_AXIS;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
        Quaternionf quaternionf = new Quaternionf();
        this.getRotator().setRotation(quaternionf, camera, tickProgress);
        if (this.angle != 0.0f) {
            quaternionf.rotateZ(MathHelper.lerp(tickProgress, this.lastAngle, this.angle));
        }
        this.render(vertexConsumer, camera, quaternionf, tickProgress);
    }

    protected void render(VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float tickProgress) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickProgress, this.lastX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickProgress, this.lastY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickProgress, this.lastZ, this.z) - vec3d.getZ());
        this.render(vertexConsumer, quaternionf, f, g, h, tickProgress);
    }

    protected void render(VertexConsumer vertexConsumer, Quaternionf quaternionf, float x, float y, float z, float tickProgress) {
        float f = this.getSize(tickProgress);
        float g = this.getMinU();
        float h = this.getMaxU();
        float i = this.getMinV();
        float j = this.getMaxV();
        int k = this.getBrightness(tickProgress);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, 1.0f, -1.0f, f, h, j, k);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, 1.0f, 1.0f, f, h, i, k);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, -1.0f, 1.0f, f, g, i, k);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, -1.0f, -1.0f, f, g, j, k);
    }

    private void renderVertex(VertexConsumer vertexConsumer, Quaternionf quaternionf, float x, float y, float z, float f, float g, float size, float u, float v, int light) {
        Vector3f vector3f = new Vector3f(f, g, 0.0f).rotate((Quaternionfc)quaternionf).mul(size).add(x, y, z);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(u, v).color(this.red, this.green, this.blue, this.alpha).light(light);
    }

    public float getSize(float tickProgress) {
        return this.scale;
    }

    @Override
    public Particle scale(float scale) {
        this.scale *= scale;
        return super.scale(scale);
    }

    protected abstract float getMinU();

    protected abstract float getMaxU();

    protected abstract float getMinV();

    protected abstract float getMaxV();

    @Environment(value=EnvType.CLIENT)
    public static interface Rotator {
        final static public Rotator ALL_AXIS = (quaternion, camera, tickProgress) -> quaternion.set((Quaternionfc)camera.getRotation());
        final static public Rotator Y_AND_W_ONLY = (quaternion, camera, tickProgress) -> quaternion.set(0.0f, camera.getRotation().y, 0.0f, camera.getRotation().w);

        public void setRotation(Quaternionf var1, Camera var2, float var3);
    }
}

