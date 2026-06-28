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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepWoolFeatureRenderer
extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sheep/sheep_wool.png");
    final private EntityModel<SheepEntityRenderState> woolModel;
    final private EntityModel<SheepEntityRenderState> babyWoolModel;

    public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.woolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL));
        this.babyWoolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntityRenderState sheepEntityRenderState, float f, float g) {
        EntityModel<SheepEntityRenderState> entityModel;
        if (sheepEntityRenderState.sheared) {
            return;
        }
        EntityModel<SheepEntityRenderState> entityModel2 = entityModel = sheepEntityRenderState.baby ? this.babyWoolModel : this.woolModel;
        if (sheepEntityRenderState.invisible) {
            if (sheepEntityRenderState.hasOutline) {
                entityModel.setAngles(sheepEntityRenderState);
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(TEXTURE));
                entityModel.render(matrixStack, vertexConsumer, 1, LivingEntityRenderer.getOverlay(sheepEntityRenderState, 0.0f), -16777216);
            }
            return;
        }
        SheepWoolFeatureRenderer.render(entityModel, TEXTURE, matrixStack, vertexConsumerProvider, 1, sheepEntityRenderState, sheepEntityRenderState.getRgbColor());
    }
}

