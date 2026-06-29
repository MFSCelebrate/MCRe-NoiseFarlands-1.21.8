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
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreeperChargeFeatureRenderer
extends EnergySwirlOverlayFeatureRenderer<CreeperEntityRenderState, CreeperEntityModel> {
    final static private Identifier SKIN = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
    final private CreeperEntityModel model;

    public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntityRenderState, CreeperEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new CreeperEntityModel(loader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
    }

    @Override
    protected boolean shouldRender(CreeperEntityRenderState creeperEntityRenderState) {
        return creeperEntityRenderState.charged;
    }

    @Override
    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.01f;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    @Override
    protected CreeperEntityModel net_minecraft_client_render_entity_model_CreeperEntityModel_getEnergySwirlModel() {
        return this.model;
    }

    @Override
    protected EntityModel net_minecraft_client_render_entity_model_EntityModel_getEnergySwirlModel() {
        return this.net_minecraft_client_render_entity_model_CreeperEntityModel_getEnergySwirlModel();
    }
}

