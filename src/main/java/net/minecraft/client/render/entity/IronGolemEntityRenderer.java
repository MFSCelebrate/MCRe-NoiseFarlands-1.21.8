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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityRenderState, IronGolemEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/iron_golem/iron_golem.png");

    public IronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IronGolemEntityModel(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature(new IronGolemCrackFeatureRenderer(this));
        this.addFeature(new IronGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(IronGolemEntityRenderState ironGolemEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public IronGolemEntityRenderState net_minecraft_client_render_entity_state_IronGolemEntityRenderState_createRenderState() {
        return new IronGolemEntityRenderState();
    }

    @Override
    public void updateRenderState(IronGolemEntity ironGolemEntity, IronGolemEntityRenderState ironGolemEntityRenderState, float f) {
        super.updateRenderState(ironGolemEntity, ironGolemEntityRenderState, f);
        ironGolemEntityRenderState.attackTicksLeft = (float)ironGolemEntity.getAttackTicksLeft() > 0.0f ? (float)ironGolemEntity.getAttackTicksLeft() - f : 0.0f;
        ironGolemEntityRenderState.lookingAtVillagerTicks = ironGolemEntity.getLookingAtVillagerTicks();
        ironGolemEntityRenderState.crackLevel = ironGolemEntity.getCrackLevel();
    }

    @Override
    protected void setupTransforms(IronGolemEntityRenderState ironGolemEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(ironGolemEntityRenderState, matrixStack, f, g);
        if ((double)ironGolemEntityRenderState.limbSwingAmplitude < 0.01) {
            return;
        }
        float h = 13.0f;
        float i = ironGolemEntityRenderState.limbSwingAnimationProgress + 6.0f;
        float j = (Math.abs(i % 13.0f - 6.5f) - 3.25f) / 3.25f;
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * j));
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IronGolemEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_IronGolemEntityRenderState_createRenderState();
    }
}

