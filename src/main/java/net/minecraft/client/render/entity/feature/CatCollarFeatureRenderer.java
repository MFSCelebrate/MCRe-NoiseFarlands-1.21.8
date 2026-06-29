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
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CatCollarFeatureRenderer
extends FeatureRenderer<CatEntityRenderState, CatEntityModel> {
    final static private Identifier SKIN = Identifier.ofVanilla("textures/entity/cat/cat_collar.png");
    final private CatEntityModel model;
    final private CatEntityModel babyModel;

    public CatCollarFeatureRenderer(FeatureRendererContext<CatEntityRenderState, CatEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new CatEntityModel(loader.getModelPart(EntityModelLayers.CAT_COLLAR));
        this.babyModel = new CatEntityModel(loader.getModelPart(EntityModelLayers.CAT_BABY_COLLAR));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CatEntityRenderState catEntityRenderState, float f, float g) {
        DyeColor dyeColor = catEntityRenderState.collarColor;
        if (dyeColor == null) {
            return;
        }
        int j = dyeColor.getEntityColor();
        CatEntityModel catEntityModel = catEntityRenderState.baby ? this.babyModel : this.model;
        CatCollarFeatureRenderer.render(catEntityModel, SKIN, matrixStack, vertexConsumerProvider, 1, catEntityRenderState, j);
    }
}

