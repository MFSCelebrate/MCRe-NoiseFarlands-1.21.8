/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class FoxEntityRenderer
extends AgeableMobEntityRenderer<FoxEntity, FoxEntityRenderState, FoxEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fox/fox.png");
    final static private Identifier SLEEPING_TEXTURE = Identifier.ofVanilla("textures/entity/fox/fox_sleep.png");
    final static private Identifier SNOW_TEXTURE = Identifier.ofVanilla("textures/entity/fox/snow_fox.png");
    final static private Identifier SLEEPING_SNOW_TEXTURE = Identifier.ofVanilla("textures/entity/fox/snow_fox_sleep.png");

    public FoxEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FoxEntityModel(context.getPart(EntityModelLayers.FOX)), new FoxEntityModel(context.getPart(EntityModelLayers.FOX_BABY)), 0.4f);
        this.addFeature(new FoxHeldItemFeatureRenderer(this));
    }

    @Override
    protected void setupTransforms(FoxEntityRenderState foxEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(foxEntityRenderState, matrixStack, f, g);
        if (foxEntityRenderState.chasing || foxEntityRenderState.walking) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-foxEntityRenderState.pitch));
        }
    }

    @Override
    public Identifier getTexture(FoxEntityRenderState foxEntityRenderState) {
        if (foxEntityRenderState.type == FoxEntity.Variant.RED) {
            return foxEntityRenderState.sleeping ? SLEEPING_TEXTURE : TEXTURE;
        }
        return foxEntityRenderState.sleeping ? SLEEPING_SNOW_TEXTURE : SNOW_TEXTURE;
    }

    @Override
    public FoxEntityRenderState net_minecraft_client_render_entity_state_FoxEntityRenderState_createRenderState() {
        return new FoxEntityRenderState();
    }

    @Override
    public void updateRenderState(FoxEntity foxEntity, FoxEntityRenderState foxEntityRenderState, float f) {
        super.updateRenderState(foxEntity, foxEntityRenderState, f);
        ItemHolderEntityRenderState.update(foxEntity, foxEntityRenderState, this.itemModelResolver);
        foxEntityRenderState.headRoll = foxEntity.getHeadRoll(f);
        foxEntityRenderState.inSneakingPose = foxEntity.isInSneakingPose();
        foxEntityRenderState.bodyRotationHeightOffset = foxEntity.getBodyRotationHeightOffset(f);
        foxEntityRenderState.sleeping = foxEntity.isSleeping();
        foxEntityRenderState.sitting = foxEntity.isSitting();
        foxEntityRenderState.walking = foxEntity.isWalking();
        foxEntityRenderState.chasing = foxEntity.isChasing();
        foxEntityRenderState.type = foxEntity.getVariant();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FoxEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_FoxEntityRenderState_createRenderState();
    }
}

