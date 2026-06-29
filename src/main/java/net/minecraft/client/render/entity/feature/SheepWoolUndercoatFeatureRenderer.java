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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepWoolUndercoatFeatureRenderer
extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sheep/sheep_wool_undercoat.png");
    final private EntityModel<SheepEntityRenderState> model;
    final private EntityModel<SheepEntityRenderState> babyModel;

    public SheepWoolUndercoatFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL_UNDERCOAT));
        this.babyModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL_UNDERCOAT));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntityRenderState sheepEntityRenderState, float f, float g) {
        if (sheepEntityRenderState.invisible || !sheepEntityRenderState.isJeb() && sheepEntityRenderState.color == DyeColor.WHITE) {
            return;
        }
        EntityModel<SheepEntityRenderState> entityModel = sheepEntityRenderState.baby ? this.babyModel : this.model;
        SheepWoolUndercoatFeatureRenderer.render(entityModel, TEXTURE, matrixStack, vertexConsumerProvider, 1, sheepEntityRenderState, sheepEntityRenderState.getRgbColor());
    }
}

