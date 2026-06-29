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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;

@Environment(value=EnvType.CLIENT)
public class CrackParticle
extends SpriteBillboardParticle {
    final private float sampleU;
    final private float sampleV;

    CrackParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ItemRenderState itemRenderState) {
        this(world, x, y, z, itemRenderState);
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    protected CrackParticle(ClientWorld world, double x, double y, double z, ItemRenderState itemRenderState) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        Sprite sprite = itemRenderState.getParticleSprite(this.random);
        if (sprite != null) {
            this.setSprite(sprite);
        } else {
            this.setSprite(MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(MissingSprite.getMissingSpriteId()));
        }
        this.gravityStrength = 1.0f;
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SnowballFactory
    extends Factory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(clientWorld, d, e, f, this.getItemRenderState(new ItemStack(Items.SNOWBALL), clientWorld));
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CobwebFactory
    extends Factory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(clientWorld, d, e, f, this.getItemRenderState(new ItemStack(Items.COBWEB), clientWorld));
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SlimeballFactory
    extends Factory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(clientWorld, d, e, f, this.getItemRenderState(new ItemStack(Items.SLIME_BALL), clientWorld));
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ItemFactory
    extends Factory<ItemStackParticleEffect> {
        @Override
        public Particle createParticle(ItemStackParticleEffect itemStackParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(clientWorld, d, e, f, g, h, i, this.getItemRenderState(itemStackParticleEffect.getItemStack(), clientWorld));
        }

        @Override
        public Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((ItemStackParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Factory<T extends ParticleEffect>
    implements ParticleFactory<T> {
        final private ItemRenderState itemRenderState = new ItemRenderState();

        protected ItemRenderState getItemRenderState(ItemStack stack, ClientWorld world) {
            MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(this.itemRenderState, stack, ItemDisplayContext.GROUND, world, null, 0);
            return this.itemRenderState;
        }
    }
}

