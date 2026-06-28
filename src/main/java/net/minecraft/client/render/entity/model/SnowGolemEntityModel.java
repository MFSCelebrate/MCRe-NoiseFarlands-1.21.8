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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityModel
extends EntityModel<LivingEntityRenderState> {
    final static private String UPPER_BODY = "upper_body";
    final private ModelPart upperBody;
    final private ModelPart head;
    final private ModelPart leftArm;
    final private ModelPart rightArm;

    public SnowGolemEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
        this.upperBody = modelPart.getChild(EntityModelPartNames.UPPER_BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 4.0f;
        Dilation dilation = new Dilation(-0.5f);
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin(0.0f, 4.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, modelPartBuilder, ModelTransform.of(5.0f, 6.0f, 1.0f, 0.0f, 0.0f, 1.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, modelPartBuilder, ModelTransform.of(-5.0f, 6.0f, -1.0f, 0.0f, (float)Math.PI, -1.0f));
        modelPartData.addChild(EntityModelPartNames.UPPER_BODY, ModelPartBuilder.create().uv(0, 16).cuboid(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, dilation), ModelTransform.origin(0.0f, 13.0f, 0.0f));
        modelPartData.addChild("lower_body", ModelPartBuilder.create().uv(0, 36).cuboid(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, dilation), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        super.setAngles(livingEntityRenderState);
        this.head.yaw = livingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = livingEntityRenderState.pitch * ((float)Math.PI / 180);
        this.upperBody.yaw = livingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180) * 0.25f;
        float f = MathHelper.sin(this.upperBody.yaw);
        float g = MathHelper.cos(this.upperBody.yaw);
        this.leftArm.yaw = this.upperBody.yaw;
        this.rightArm.yaw = this.upperBody.yaw + (float)Math.PI;
        this.leftArm.originX = g * 5.0f;
        this.leftArm.originZ = -f * 5.0f;
        this.rightArm.originX = -g * 5.0f;
        this.rightArm.originZ = f * 5.0f;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

