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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityRenderer
extends BipedEntityRenderer<AbstractPiglinEntity, PiglinEntityRenderState, PiglinEntityModel> {
    final static private Identifier PIGLIN_TEXTURE = Identifier.ofVanilla("textures/entity/piglin/piglin.png");
    final static private Identifier PIGLIN_BRUTE_TEXTURE = Identifier.ofVanilla("textures/entity/piglin/piglin_brute.png");
    final static public HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(0.0f, 0.0f, 1.0019531f);

    public PiglinEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer mainLayer, EntityModelLayer babyMainLayer, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer, EntityModelLayer babyArmorInnerLayer, EntityModelLayer babyArmorOuterLayer) {
        super(ctx, new PiglinEntityModel(ctx.getPart(mainLayer)), new PiglinEntityModel(ctx.getPart(babyMainLayer)), 0.5f, HEAD_TRANSFORMATION);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(ctx.getPart(armorInnerLayer)), new ArmorEntityModel(ctx.getPart(armorOuterLayer)), new ArmorEntityModel(ctx.getPart(babyArmorInnerLayer)), new ArmorEntityModel(ctx.getPart(babyArmorOuterLayer)), ctx.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(PiglinEntityRenderState piglinEntityRenderState) {
        return piglinEntityRenderState.brute ? PIGLIN_BRUTE_TEXTURE : PIGLIN_TEXTURE;
    }

    @Override
    public PiglinEntityRenderState net_minecraft_client_render_entity_state_PiglinEntityRenderState_createRenderState() {
        return new PiglinEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractPiglinEntity abstractPiglinEntity, PiglinEntityRenderState piglinEntityRenderState, float f) {
        super.updateRenderState(abstractPiglinEntity, piglinEntityRenderState, f);
        piglinEntityRenderState.brute = abstractPiglinEntity.getType() == EntityType.PIGLIN_BRUTE;
        piglinEntityRenderState.activity = abstractPiglinEntity.getActivity();
        piglinEntityRenderState.piglinCrossbowPullTime = CrossbowItem.getPullTime(abstractPiglinEntity.getActiveItem(), abstractPiglinEntity);
        piglinEntityRenderState.shouldZombify = abstractPiglinEntity.shouldZombify();
    }

    @Override
    protected boolean isShaking(PiglinEntityRenderState piglinEntityRenderState) {
        return super.isShaking(piglinEntityRenderState) || piglinEntityRenderState.shouldZombify;
    }

    @Override
    protected boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((PiglinEntityRenderState)state);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PiglinEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PiglinEntityRenderState_createRenderState();
    }
}

