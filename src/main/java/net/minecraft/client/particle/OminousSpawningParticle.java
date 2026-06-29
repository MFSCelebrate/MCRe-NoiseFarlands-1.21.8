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
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class OminousSpawningParticle
extends SpriteBillboardParticle {
    final private double startX;
    final private double startY;
    final private double startZ;
    final private int fromColor;
    final private int toColor;

    OminousSpawningParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int fromColor, int toColor) {
        super(world, x, y, z);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.lastX = x + velocityX;
        this.lastY = y + velocityY;
        this.lastZ = z + velocityZ;
        this.x = this.lastX;
        this.y = this.lastY;
        this.z = this.lastZ;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        this.collidesWithWorld = false;
        this.maxAge = (int)(Math.random() * 5.0) + 25;
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
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
        float f = (float)this.age / (float)this.maxAge;
        float g = 1.0f - f;
        this.x = this.startX + this.velocityX * (double)g;
        this.y = this.startY + this.velocityY * (double)g;
        this.z = this.startZ + this.velocityZ * (double)g;
        int i = ColorHelper.lerp(f, this.fromColor, this.toColor);
        this.setColor((float)ColorHelper.getRed(1) / 255.0f, (float)ColorHelper.getGreen(1) / 255.0f, (float)ColorHelper.getBlue(1) / 255.0f);
        this.setAlpha((float)ColorHelper.getAlpha(1) / 255.0f);
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
            OminousSpawningParticle ominousSpawningParticle = new OminousSpawningParticle(clientWorld, d, e, f, g, h, i, -12210434, -1);
            ominousSpawningParticle.scale(MathHelper.nextBetween(clientWorld.getRandom(), 3.0f, 5.0f));
            ominousSpawningParticle.setSprite(this.spriteProvider);
            return ominousSpawningParticle;
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

