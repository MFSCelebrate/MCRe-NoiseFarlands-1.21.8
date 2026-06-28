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
import net.minecraft.client.render.entity.AbstractHoglinEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZoglinEntityRenderer
extends AbstractHoglinEntityRenderer<ZoglinEntity> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/hoglin/zoglin.png");

    public ZoglinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.ZOGLIN, EntityModelLayers.ZOGLIN_BABY, 0.7f);
    }

    @Override
    public Identifier getTexture(HoglinEntityRenderState hoglinEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HoglinEntityRenderState)state);
    }
}

