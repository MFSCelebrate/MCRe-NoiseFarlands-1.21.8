/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class PandaEntityRenderer
extends AgeableMobEntityRenderer<PandaEntity, PandaEntityRenderState, PandaEntityModel> {
    final static private Map<PandaEntity.Gene, Identifier> TEXTURES = Maps.newEnumMap(Map.of(PandaEntity.Gene.NORMAL, Identifier.ofVanilla("textures/entity/panda/panda.png"), PandaEntity.Gene.LAZY, Identifier.ofVanilla("textures/entity/panda/lazy_panda.png"), PandaEntity.Gene.WORRIED, Identifier.ofVanilla("textures/entity/panda/worried_panda.png"), PandaEntity.Gene.PLAYFUL, Identifier.ofVanilla("textures/entity/panda/playful_panda.png"), PandaEntity.Gene.BROWN, Identifier.ofVanilla("textures/entity/panda/brown_panda.png"), PandaEntity.Gene.WEAK, Identifier.ofVanilla("textures/entity/panda/weak_panda.png"), PandaEntity.Gene.AGGRESSIVE, Identifier.ofVanilla("textures/entity/panda/aggressive_panda.png")));

    public PandaEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PandaEntityModel(context.getPart(EntityModelLayers.PANDA)), new PandaEntityModel(context.getPart(EntityModelLayers.PANDA_BABY)), 0.9f);
        this.addFeature(new PandaHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(PandaEntityRenderState pandaEntityRenderState) {
        return TEXTURES.getOrDefault(pandaEntityRenderState.gene, TEXTURES.get(PandaEntity.Gene.NORMAL));
    }

    @Override
    public PandaEntityRenderState net_minecraft_client_render_entity_state_PandaEntityRenderState_createRenderState() {
        return new PandaEntityRenderState();
    }

    @Override
    public void updateRenderState(PandaEntity pandaEntity, PandaEntityRenderState pandaEntityRenderState, float f) {
        super.updateRenderState(pandaEntity, pandaEntityRenderState, f);
        ItemHolderEntityRenderState.update(pandaEntity, pandaEntityRenderState, this.itemModelResolver);
        pandaEntityRenderState.gene = pandaEntity.getProductGene();
        pandaEntityRenderState.askingForBamboo = pandaEntity.getAskForBambooTicks() > 0;
        pandaEntityRenderState.sneezing = pandaEntity.isSneezing();
        pandaEntityRenderState.sneezeProgress = pandaEntity.getSneezeProgress();
        pandaEntityRenderState.eating = pandaEntity.isEating();
        pandaEntityRenderState.scaredByThunderstorm = pandaEntity.isScaredByThunderstorm();
        pandaEntityRenderState.sitting = pandaEntity.isSitting();
        pandaEntityRenderState.sittingAnimationProgress = pandaEntity.getSittingAnimationProgress(f);
        pandaEntityRenderState.lieOnBackAnimationProgress = pandaEntity.getLieOnBackAnimationProgress(f);
        pandaEntityRenderState.rollOverAnimationProgress = pandaEntity.isBaby() ? 0.0f : pandaEntity.getRollOverAnimationProgress(f);
        pandaEntityRenderState.playingTicks = pandaEntity.playingTicks > 0 ? (float)pandaEntity.playingTicks + f : 0.0f;
    }

    @Override
    protected void setupTransforms(PandaEntityRenderState pandaEntityRenderState, MatrixStack matrixStack, float f, float g) {
        float q;
        float h;
        super.setupTransforms(pandaEntityRenderState, matrixStack, f, g);
        if (pandaEntityRenderState.playingTicks > 0.0f) {
            float l;
            h = MathHelper.fractionalPart(pandaEntityRenderState.playingTicks);
            int i = MathHelper.floor(pandaEntityRenderState.playingTicks);
            int j = i + 1;
            float k = 7.0f;
            float f2 = l = pandaEntityRenderState.baby ? 0.3f : 0.8f;
            if ((float)i < 8.0f) {
                float m = 90.0f * (float)i / 7.0f;
                float n = 90.0f * (float)j / 7.0f;
                float o = this.getAngle(m, n, j, h, 8.0f);
                matrixStack.translate(0.0f, (l + 0.2f) * (o / 90.0f), 0.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-o));
            } else if ((float)i < 16.0f) {
                float m = ((float)i - 8.0f) / 7.0f;
                float n = 90.0f + 90.0f * m;
                float p = 90.0f + 90.0f * ((float)j - 8.0f) / 7.0f;
                float o = this.getAngle(n, p, j, h, 16.0f);
                matrixStack.translate(0.0f, l + 0.2f + (l - 0.2f) * (o - 90.0f) / 90.0f, 0.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-o));
            } else if ((float)i < 24.0f) {
                float m = ((float)i - 16.0f) / 7.0f;
                float n = 180.0f + 90.0f * m;
                float p = 180.0f + 90.0f * ((float)j - 16.0f) / 7.0f;
                float o = this.getAngle(n, p, j, h, 24.0f);
                matrixStack.translate(0.0f, l + l * (270.0f - o) / 90.0f, 0.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-o));
            } else if (i < 32) {
                float m = ((float)i - 24.0f) / 7.0f;
                float n = 270.0f + 90.0f * m;
                float p = 270.0f + 90.0f * ((float)j - 24.0f) / 7.0f;
                float o = this.getAngle(n, p, j, h, 32.0f);
                matrixStack.translate(0.0f, l * ((360.0f - o) / 90.0f), 0.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-o));
            }
        }
        if ((h = pandaEntityRenderState.sittingAnimationProgress) > 0.0f) {
            matrixStack.translate(0.0f, 0.8f * h, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(h, pandaEntityRenderState.pitch, pandaEntityRenderState.pitch + 90.0f)));
            matrixStack.translate(0.0f, -1.0f * h, 0.0f);
            if (pandaEntityRenderState.scaredByThunderstorm) {
                float q2 = (float)(Math.cos(pandaEntityRenderState.age * 1.25f) * Math.PI * (double)0.05f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(q2));
                if (pandaEntityRenderState.baby) {
                    matrixStack.translate(0.0f, 0.8f, 0.55f);
                }
            }
        }
        if ((q = pandaEntityRenderState.lieOnBackAnimationProgress) > 0.0f) {
            float r = pandaEntityRenderState.baby ? 0.5f : 1.3f;
            matrixStack.translate(0.0f, r * q, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(q, pandaEntityRenderState.pitch, pandaEntityRenderState.pitch + 180.0f)));
        }
    }

    private float getAngle(float f, float g, int i, float h, float j) {
        if (1.0f < j) {
            return MathHelper.lerp(h, f, g);
        }
        return f;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PandaEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PandaEntityRenderState_createRenderState();
    }
}

