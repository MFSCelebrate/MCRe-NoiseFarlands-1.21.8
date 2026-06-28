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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel
extends EntityModel<ShulkerEntityRenderState> {
    final static public String LID = "lid";
    final static private String BASE = "base";
    final private ModelPart lid;
    final private ModelPart head;

    public ShulkerEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayer::getEntityCutoutNoCullZOffset);
        this.lid = modelPart.getChild(LID);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
    }

    private static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(LID, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        modelPartData.addChild(BASE, ModelPartBuilder.create().uv(0, 28).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ShulkerEntityModel.getModelData();
        modelData.getRoot().addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 52).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.origin(0.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getShulkerBoxTexturedModelData() {
        ModelData modelData = ShulkerEntityModel.getModelData();
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(ShulkerEntityRenderState shulkerEntityRenderState) {
        super.setAngles(shulkerEntityRenderState);
        float f = (0.5f + shulkerEntityRenderState.openProgress) * (float)Math.PI;
        float g = -1.0f + MathHelper.sin(f);
        float h = 0.0f;
        if (f > (float)Math.PI) {
            h = MathHelper.sin(shulkerEntityRenderState.age * 0.1f) * 0.7f;
        }
        this.lid.setOrigin(0.0f, 16.0f + MathHelper.sin(f) * 8.0f + h, 0.0f);
        this.lid.yaw = shulkerEntityRenderState.openProgress > 0.3f ? g * g * g * g * (float)Math.PI * 0.125f : 0.0f;
        this.head.pitch = shulkerEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = (shulkerEntityRenderState.headYaw - 180.0f - shulkerEntityRenderState.shellYaw) * ((float)Math.PI / 180);
    }
}

