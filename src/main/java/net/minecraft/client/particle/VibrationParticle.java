/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.PositionSource;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class VibrationParticle
extends SpriteBillboardParticle {
    final private PositionSource vibration;
    private float field_28250;
    private float field_28248;
    private float field_40507;
    private float field_40508;

    VibrationParticle(ClientWorld world, double x, double y, double z, PositionSource vibration, int maxAge) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.scale = 0.3f;
        this.vibration = vibration;
        this.maxAge = maxAge;
        Optional<Vec3d> optional = vibration.getPos(world);
        if (optional.isPresent()) {
            Vec3d vec3d = optional.get();
            double d = x - vec3d.getX();
            double e = y - vec3d.getY();
            double f = z - vec3d.getZ();
            this.field_28248 = this.field_28250 = (float)MathHelper.atan2(d, f);
            this.field_40508 = this.field_40507 = (float)MathHelper.atan2(e, Math.sqrt(d * d + f * f));
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
        float f = MathHelper.sin(((float)this.age + tickProgress - (float)Math.PI * 2) * 0.05f) * 2.0f;
        float g = MathHelper.lerp(tickProgress, this.field_28248, this.field_28250);
        float h = MathHelper.lerp(tickProgress, this.field_40508, this.field_40507) + 1.5707964f;
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationY(g).rotateX(-h).rotateY(f);
        this.render(vertexConsumer, camera, quaternionf, tickProgress);
        quaternionf.rotationY((float)(-Math.PI) + g).rotateX(h).rotateY(f);
        this.render(vertexConsumer, camera, quaternionf, tickProgress);
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        Optional<Vec3d> optional = this.vibration.getPos(this.world);
        if (optional.isEmpty()) {
            this.markDead();
            return;
        }
        int i = this.maxAge - this.age;
        double d = 1.0 / (double)i;
        Vec3d vec3d = optional.get();
        this.x = MathHelper.lerp(d, this.x, vec3d.getX());
        this.y = MathHelper.lerp(d, this.y, vec3d.getY());
        this.z = MathHelper.lerp(d, this.z, vec3d.getZ());
        double e = this.x - vec3d.getX();
        double f = this.y - vec3d.getY();
        double g = this.z - vec3d.getZ();
        this.field_28248 = this.field_28250;
        this.field_28250 = (float)MathHelper.atan2(e, g);
        this.field_40508 = this.field_40507;
        this.field_40507 = (float)MathHelper.atan2(f, Math.sqrt(e * e + g * g));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<VibrationParticleEffect> {
        final private SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(VibrationParticleEffect vibrationParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            VibrationParticle vibrationParticle = new VibrationParticle(clientWorld, d, e, f, vibrationParticleEffect.getVibration(), vibrationParticleEffect.getArrivalInTicks());
            vibrationParticle.setSprite(this.spriteProvider);
            vibrationParticle.setAlpha(1.0f);
            return vibrationParticle;
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((VibrationParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

