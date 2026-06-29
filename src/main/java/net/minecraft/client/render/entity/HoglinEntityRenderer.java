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
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityRenderer
extends AbstractHoglinEntityRenderer<HoglinEntity> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/hoglin/hoglin.png");

    public HoglinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.HOGLIN, EntityModelLayers.HOGLIN_BABY, 0.7f);
    }

    @Override
    public Identifier getTexture(HoglinEntityRenderState hoglinEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public void updateRenderState(HoglinEntity hoglinEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
        super.updateRenderState(hoglinEntity, hoglinEntityRenderState, f);
        hoglinEntityRenderState.canConvert = hoglinEntity.canConvert();
    }

    @Override
    protected boolean isShaking(HoglinEntityRenderState hoglinEntityRenderState) {
        return super.isShaking(hoglinEntityRenderState) || hoglinEntityRenderState.canConvert;
    }

    @Override
    protected boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((HoglinEntityRenderState)state);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HoglinEntityRenderState)state);
    }
}

