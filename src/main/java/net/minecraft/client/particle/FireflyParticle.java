/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FireflyParticle
extends SpriteBillboardParticle {
    final static private float field_56803 = 0.3f;
    final static private float field_56804 = 0.1f;
    final static private float field_56801 = 0.5f;
    final static private float field_56802 = 0.3f;
    final static private int MIN_MAX_AGE = 200;
    final static private int MAX_MAX_AGE = 300;

    FireflyParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.ascending = true;
        this.velocityMultiplier = 0.96f;
        this.scale *= 0.75f;
        this.velocityY *= (double)0.8f;
        this.velocityX *= (double)0.8f;
        this.velocityZ *= (double)0.8f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getBrightness(float tint) {
        return (int)(255.0f * FireflyParticle.method_67878(this.method_67879((float)this.age + tint), 0.1f, 0.3f));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.getBlockState(BlockPos.ofFloored(this.x, this.y, this.z)).isAir()) {
            this.markDead();
            return;
        }
        this.setAlpha(FireflyParticle.method_67878(this.method_67879(this.age), 0.3f, 0.5f));
        if (Math.random() > 0.95 || this.age == 1) {
            this.setVelocity((double)-0.05f + (double)0.1f * Math.random(), (double)-0.05f + (double)0.1f * Math.random(), (double)-0.05f + (double)0.1f * Math.random());
        }
    }

    private float method_67879(float f) {
        return MathHelper.clamp(f / (float)this.maxAge, 0.0f, 1.0f);
    }

    private static float method_67878(float f, float g, float h) {
        if (f >= 1.0f - g) {
            return (1.0f - f) / g;
        }
        if (f <= h) {
            return f / h;
        }
        return 1.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<SimpleParticleType> {
        final private SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            FireflyParticle fireflyParticle = new FireflyParticle(clientWorld, d, e, f, 0.5 - clientWorld.random.nextDouble(), clientWorld.random.nextBoolean() ? h : -h, 0.5 - clientWorld.random.nextDouble());
            fireflyParticle.setMaxAge(clientWorld.random.nextBetween(200, 300));
            fireflyParticle.scale(1.5f);
            fireflyParticle.setSprite(this.spriteProvider);
            fireflyParticle.setAlpha(0.0f);
            return fireflyParticle;
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

