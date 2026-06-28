/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class DustColorTransitionParticle
extends AbstractDustParticle<DustColorTransitionParticleEffect> {
    final private Vector3f startColor;
    final private Vector3f endColor;

    protected DustColorTransitionParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, DustColorTransitionParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        this.startColor = this.darken(parameters.getFromColor(), f);
        this.endColor = this.darken(parameters.getToColor(), f);
    }

    private Vector3f darken(Vector3f color, float multiplier) {
        return new Vector3f(this.darken(color.x(), multiplier), this.darken(color.y(), multiplier), this.darken(color.z(), multiplier));
    }

    private void updateColor(float tickProgress) {
        float f = ((float)this.age + tickProgress) / ((float)this.maxAge + 1.0f);
        Vector3f vector3f = new Vector3f((Vector3fc)this.startColor).lerp((Vector3fc)this.endColor, f);
        this.red = vector3f.x();
        this.green = vector3f.y();
        this.blue = vector3f.z();
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
        this.updateColor(tickProgress);
        super.render(vertexConsumer, camera, tickProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DustColorTransitionParticleEffect> {
        final private SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DustColorTransitionParticleEffect dustColorTransitionParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new DustColorTransitionParticle(clientWorld, d, e, f, g, h, i, dustColorTransitionParticleEffect, this.spriteProvider);
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((DustColorTransitionParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

