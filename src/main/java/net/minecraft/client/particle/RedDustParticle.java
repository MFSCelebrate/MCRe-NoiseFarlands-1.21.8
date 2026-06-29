/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class RedDustParticle
extends AbstractDustParticle<DustParticleEffect> {
    protected RedDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, DustParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        Vector3f vector3f = parameters.getColor();
        this.red = this.darken(vector3f.x(), f);
        this.green = this.darken(vector3f.y(), f);
        this.blue = this.darken(vector3f.z(), f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DustParticleEffect> {
        final private SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DustParticleEffect dustParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new RedDustParticle(clientWorld, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((DustParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

