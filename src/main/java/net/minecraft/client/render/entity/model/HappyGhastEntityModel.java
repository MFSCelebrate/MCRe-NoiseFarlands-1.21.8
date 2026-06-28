/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.HappyGhastEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class HappyGhastEntityModel
extends EntityModel<HappyGhastEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.2375f);
    final static private float HARNESSED_SCALE = 0.9375f;
    final private ModelPart[] tentacles = new ModelPart[9];
    final private ModelPart body;

    public HappyGhastEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = this.body.getChild(EntityModelPartNames.getTentacleName(i));
        }
    }

    public static TexturedModelData getTexturedModelData(boolean baby, Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f, dilation), ModelTransform.origin(0.0f, 16.0f, 0.0f));
        if (baby) {
            modelPartData2.addChild(EntityModelPartNames.INNER_BODY, ModelPartBuilder.create().uv(0, 32).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 16.0f, 16.0f, dilation.add(-0.5f)), ModelTransform.origin(0.0f, 8.0f, 0.0f));
        }
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(0), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin(-3.75f, 7.0f, -5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(1), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f, dilation), ModelTransform.origin(1.25f, 7.0f, -5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(2), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f, dilation), ModelTransform.origin(6.25f, 7.0f, -5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(3), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin(-6.25f, 7.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(4), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin(-1.25f, 7.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(5), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f, dilation), ModelTransform.origin(3.75f, 7.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(6), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation), ModelTransform.origin(-3.75f, 7.0f, 5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(7), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation), ModelTransform.origin(1.25f, 7.0f, 5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName(8), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin(6.25f, 7.0f, 5.0f));
        return TexturedModelData.of(modelData, 64, 64).transform(ModelTransformer.scaling(4.0f));
    }

    @Override
    public void setAngles(HappyGhastEntityRenderState happyGhastEntityRenderState) {
        super.setAngles(happyGhastEntityRenderState);
        if (!happyGhastEntityRenderState.harnessStack.isEmpty()) {
            this.body.xScale = 0.9375f;
            this.body.yScale = 0.9375f;
            this.body.zScale = 0.9375f;
        }
        GhastEntityModel.setTentacleAngles(happyGhastEntityRenderState, this.tentacles);
    }
}

