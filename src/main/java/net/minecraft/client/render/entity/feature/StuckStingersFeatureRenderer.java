/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.StingerModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StuckStingersFeatureRenderer<M extends PlayerEntityModel>
extends StuckObjectsFeatureRenderer<M> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_stinger.png");

    public StuckStingersFeatureRenderer(LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer, EntityRendererFactory.Context context) {
        super(entityRenderer, new StingerModel(context.getPart(EntityModelLayers.BEE_STINGER)), TEXTURE, StuckObjectsFeatureRenderer.RenderPosition.ON_SURFACE);
    }

    @Override
    protected int getObjectCount(PlayerEntityRenderState playerRenderState) {
        return playerRenderState.stingerCount;
    }
}

