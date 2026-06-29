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
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LargeTropicalFishEntityModel
extends EntityModel<TropicalFishEntityRenderState> {
    final private ModelPart tail;

    public LargeTropicalFishEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 19;
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 20).cuboid(-1.0f, -3.0f, -3.0f, 2.0f, 6.0f, 6.0f, dilation), ModelTransform.origin(0.0f, 19.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(21, 16).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 6.0f, 5.0f, dilation), ModelTransform.origin(0.0f, 19.0f, 3.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FIN, ModelPartBuilder.create().uv(2, 16).cuboid(-2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of(-1.0f, 20.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FIN, ModelPartBuilder.create().uv(2, 12).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of(1.0f, 20.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TOP_FIN, ModelPartBuilder.create().uv(20, 11).cuboid(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 6.0f, dilation), ModelTransform.origin(0.0f, 16.0f, -3.0f));
        modelPartData.addChild(EntityModelPartNames.BOTTOM_FIN, ModelPartBuilder.create().uv(20, 21).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 6.0f, dilation), ModelTransform.origin(0.0f, 22.0f, -3.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
        super.setAngles(tropicalFishEntityRenderState);
        float f = tropicalFishEntityRenderState.touchingWater ? 1.0f : 1.5f;
        this.tail.yaw = -f * 0.45f * MathHelper.sin(0.6f * tropicalFishEntityRenderState.age);
    }
}

