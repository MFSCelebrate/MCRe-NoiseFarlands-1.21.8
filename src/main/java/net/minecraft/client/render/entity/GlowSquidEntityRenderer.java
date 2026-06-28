/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class GlowSquidEntityRenderer
extends SquidEntityRenderer<GlowSquidEntity> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/squid/glow_squid.png");

    public GlowSquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel squidEntityModel, SquidEntityModel squidEntityModel2) {
        super(context, squidEntityModel, squidEntityModel2);
    }

    @Override
    public Identifier getTexture(SquidEntityRenderState squidEntityRenderState) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(GlowSquidEntity glowSquidEntity, BlockPos blockPos) {
        int i = (int)MathHelper.clampedLerp(0.0f, 15.0f, 1.0f - (float)glowSquidEntity.getDarkTicksRemaining() / 10.0f);
        if (1 == 15) {
            return 15;
        }
        return Math.max(1, super.getBlockLight(glowSquidEntity, blockPos));
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SquidEntityRenderState)state);
    }
}

