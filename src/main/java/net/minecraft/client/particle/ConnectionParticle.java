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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;

@Environment(value=EnvType.CLIENT)
public class ConnectionParticle
extends SpriteBillboardParticle {
    final private double startX;
    final private double startY;
    final private double startZ;
    final private boolean fullBrightness;
    final private Particle.DynamicAlpha dynamicAlpha;

    ConnectionParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        this(clientWorld, d, e, f, g, h, i, false, Particle.DynamicAlpha.OPAQUE);
    }

    ConnectionParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, boolean fullBrightness, Particle.DynamicAlpha dynamicAlpha) {
        super(world, x, y, z);
        this.fullBrightness = fullBrightness;
        this.dynamicAlpha = dynamicAlpha;
        this.setAlpha(dynamicAlpha.startAlpha());
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
        float f = this.random.nextFloat() * 0.6f + 0.4f;
        this.red = 0.9f * f;
        this.green = 0.9f * f;
        this.blue = f;
        this.collidesWithWorld = false;
        this.maxAge = (int)(Math.random() * 10.0) + 30;
    }

    @Override
    public ParticleTextureSheet getType() {
        if (this.dynamicAlpha.isOpaque()) {
            return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        }
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public int getBrightness(float tint) {
        if (this.fullBrightness) {
            return 240;
        }
        int i = super.getBrightness(tint);
        float f = (float)this.age / (float)this.maxAge;
        f *= f;
        f *= f;
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((k += (int)(f * 15.0f * 16.0f)) > 240) {
            k = 240;
        }
        return j | k << 16;
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
        f = 1.0f - f;
        float g = 1.0f - f;
        g *= g;
        g *= g;
        this.x = this.startX + this.velocityX * (double)f;
        this.y = this.startY + this.velocityY * (double)f - (double)(g * 1.2f);
        this.z = this.startZ + this.velocityZ * (double)f;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
        this.setAlpha(this.dynamicAlpha.getAlpha(this.age, this.maxAge, tickProgress));
        super.render(vertexConsumer, camera, tickProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public static class VaultConnectionFactory
    implements ParticleFactory<SimpleParticleType> {
        final private SpriteProvider spriteProvider;

        public VaultConnectionFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ConnectionParticle connectionParticle = new ConnectionParticle(clientWorld, d, e, f, g, h, i, true, new Particle.DynamicAlpha(0.0f, 0.6f, 0.25f, 1.0f));
            connectionParticle.scale(1.5f);
            connectionParticle.setSprite(this.spriteProvider);
            return connectionParticle;
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class NautilusFactory
    implements ParticleFactory<SimpleParticleType> {
        final private SpriteProvider spriteProvider;

        public NautilusFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ConnectionParticle connectionParticle = new ConnectionParticle(clientWorld, d, e, f, g, h, i);
            connectionParticle.setSprite(this.spriteProvider);
            return connectionParticle;
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class EnchantFactory
    implements ParticleFactory<SimpleParticleType> {
        final private SpriteProvider spriteProvider;

        public EnchantFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ConnectionParticle connectionParticle = new ConnectionParticle(clientWorld, d, e, f, g, h, i);
            connectionParticle.setSprite(this.spriteProvider);
            return connectionParticle;
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

