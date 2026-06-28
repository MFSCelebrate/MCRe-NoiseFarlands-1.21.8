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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityModel
extends EntityModel<SalmonEntityRenderState> {
    final static public ModelTransformer SMALL_TRANSFORMER = ModelTransformer.scaling(0.5f);
    final static public ModelTransformer LARGE_TRANSFORMER = ModelTransformer.scaling(1.5f);
    final static private String BODY_FRONT = "body_front";
    final static private String BODY_BACK = "body_back";
    final static private float field_54015 = -7.2f;
    final private ModelPart tail;

    public SalmonEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.tail = modelPart.getChild(BODY_BACK);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 20;
        ModelPartData modelPartData2 = modelPartData.addChild(BODY_FRONT, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), ModelTransform.origin(0.0f, 20.0f, -7.2f));
        ModelPartData modelPartData3 = modelPartData.addChild(BODY_BACK, ModelPartBuilder.create().uv(0, 13).cuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), ModelTransform.origin(0.0f, 20.0f, 0.8000002f));
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(22, 0).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), ModelTransform.origin(0.0f, 20.0f, -7.2f));
        modelPartData3.addChild(EntityModelPartNames.BACK_FIN, ModelPartBuilder.create().uv(20, 10).cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f), ModelTransform.origin(0.0f, 0.0f, 8.0f));
        modelPartData2.addChild("top_front_fin", ModelPartBuilder.create().uv(2, 1).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f), ModelTransform.origin(0.0f, -4.5f, 5.0f));
        modelPartData3.addChild("top_back_fin", ModelPartBuilder.create().uv(0, 2).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f), ModelTransform.origin(0.0f, -4.5f, -1.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FIN, ModelPartBuilder.create().uv(-4, 0).cuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of(-1.5f, 21.5f, -7.2f, 0.0f, 0.0f, -0.7853982f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FIN, ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of(1.5f, 21.5f, -7.2f, 0.0f, 0.0f, 0.7853982f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(SalmonEntityRenderState salmonEntityRenderState) {
        super.setAngles(salmonEntityRenderState);
        float f = 1.0f;
        float g = 1.0f;
        if (!salmonEntityRenderState.touchingWater) {
            f = 1.3f;
            g = 1.7f;
        }
        this.tail.yaw = -f * 0.25f * MathHelper.sin(g * 0.6f * salmonEntityRenderState.age);
    }
}

