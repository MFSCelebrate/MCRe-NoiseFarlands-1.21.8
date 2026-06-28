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
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.MooshroomEntityRenderState;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MooshroomEntityRenderer
extends AgeableMobEntityRenderer<MooshroomEntity, MooshroomEntityRenderState, CowEntityModel> {
    final static private Map<MooshroomEntity.Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), map -> {
        map.put(MooshroomEntity.Variant.BROWN, Identifier.ofVanilla("textures/entity/cow/brown_mooshroom.png"));
        map.put(MooshroomEntity.Variant.RED, Identifier.ofVanilla("textures/entity/cow/red_mooshroom.png"));
    });

    public MooshroomEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM)), new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM_BABY)), 0.7f);
        this.addFeature(new MooshroomMushroomFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(MooshroomEntityRenderState mooshroomEntityRenderState) {
        return TEXTURES.get(mooshroomEntityRenderState.type);
    }

    @Override
    public MooshroomEntityRenderState net_minecraft_client_render_entity_state_MooshroomEntityRenderState_createRenderState() {
        return new MooshroomEntityRenderState();
    }

    @Override
    public void updateRenderState(MooshroomEntity mooshroomEntity, MooshroomEntityRenderState mooshroomEntityRenderState, float f) {
        super.updateRenderState(mooshroomEntity, mooshroomEntityRenderState, f);
        mooshroomEntityRenderState.type = mooshroomEntity.getVariant();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((MooshroomEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_MooshroomEntityRenderState_createRenderState();
    }
}

