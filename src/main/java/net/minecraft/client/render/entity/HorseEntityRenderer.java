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
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public final class HorseEntityRenderer
extends AbstractHorseEntityRenderer<HorseEntity, HorseEntityRenderState, HorseEntityModel> {
    final static private Map<HorseColor, Identifier> TEXTURES = Maps.newEnumMap(Map.of(HorseColor.WHITE, Identifier.ofVanilla("textures/entity/horse/horse_white.png"), HorseColor.CREAMY, Identifier.ofVanilla("textures/entity/horse/horse_creamy.png"), HorseColor.CHESTNUT, Identifier.ofVanilla("textures/entity/horse/horse_chestnut.png"), HorseColor.BROWN, Identifier.ofVanilla("textures/entity/horse/horse_brown.png"), HorseColor.BLACK, Identifier.ofVanilla("textures/entity/horse/horse_black.png"), HorseColor.GRAY, Identifier.ofVanilla("textures/entity/horse/horse_gray.png"), HorseColor.DARK_BROWN, Identifier.ofVanilla("textures/entity/horse/horse_darkbrown.png")));

    public HorseEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HorseEntityModel(context.getPart(EntityModelLayers.HORSE)), new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_BABY)));
        this.addFeature(new HorseMarkingFeatureRenderer(this));
        this.addFeature(new SaddleFeatureRenderer<HorseEntityRenderState, HorseEntityModel, HorseEntityModel>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.HORSE_BODY, horseEntityRenderState -> horseEntityRenderState.armor, new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_ARMOR)), new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_ARMOR_BABY))));
        this.addFeature(new SaddleFeatureRenderer<HorseEntityRenderState, HorseEntityModel, HorseSaddleEntityModel>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.HORSE_SADDLE, horseEntityRenderState -> horseEntityRenderState.saddleStack, new HorseSaddleEntityModel(context.getPart(EntityModelLayers.HORSE_SADDLE)), new HorseSaddleEntityModel(context.getPart(EntityModelLayers.HORSE_BABY_SADDLE))));
    }

    @Override
    public Identifier getTexture(HorseEntityRenderState horseEntityRenderState) {
        return TEXTURES.get(horseEntityRenderState.color);
    }

    @Override
    public HorseEntityRenderState net_minecraft_client_render_entity_state_HorseEntityRenderState_createRenderState() {
        return new HorseEntityRenderState();
    }

    @Override
    public void updateRenderState(HorseEntity horseEntity, HorseEntityRenderState horseEntityRenderState, float f) {
        super.updateRenderState(horseEntity, horseEntityRenderState, f);
        horseEntityRenderState.color = horseEntity.getHorseColor();
        horseEntityRenderState.marking = horseEntity.getMarking();
        horseEntityRenderState.armor = horseEntity.getBodyArmor().copy();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HorseEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_HorseEntityRenderState_createRenderState();
    }
}

