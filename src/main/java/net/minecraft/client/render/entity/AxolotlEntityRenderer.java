/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityRenderer
extends AgeableMobEntityRenderer<AxolotlEntity, AxolotlEntityRenderState, AxolotlEntityModel> {
    final static private Map<AxolotlEntity.Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), variants -> {
        for (AxolotlEntity.Variant variant : AxolotlEntity.Variant.values()) {
            variants.put(variant, Identifier.ofVanilla(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", variant.getId())));
        }
    });

    public AxolotlEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL)), new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL_BABY)), 0.5f);
    }

    @Override
    public Identifier getTexture(AxolotlEntityRenderState axolotlEntityRenderState) {
        return TEXTURES.get(axolotlEntityRenderState.variant);
    }

    @Override
    public AxolotlEntityRenderState net_minecraft_client_render_entity_state_AxolotlEntityRenderState_createRenderState() {
        return new AxolotlEntityRenderState();
    }

    @Override
    public void updateRenderState(AxolotlEntity axolotlEntity, AxolotlEntityRenderState axolotlEntityRenderState, float f) {
        super.updateRenderState(axolotlEntity, axolotlEntityRenderState, f);
        axolotlEntityRenderState.variant = axolotlEntity.getVariant();
        axolotlEntityRenderState.playingDeadValue = axolotlEntity.playingDeadFf.getValue(f);
        axolotlEntityRenderState.inWaterValue = axolotlEntity.inWaterFf.getValue(f);
        axolotlEntityRenderState.onGroundValue = axolotlEntity.onGroundFf.getValue(f);
        axolotlEntityRenderState.isMovingValue = axolotlEntity.isMovingFf.getValue(f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((AxolotlEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_AxolotlEntityRenderState_createRenderState();
    }
}

