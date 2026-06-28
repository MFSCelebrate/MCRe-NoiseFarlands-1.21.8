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
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityModel<S extends SkeletonEntityRenderState>
extends BipedEntityModel<S> {
    public SkeletonEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        SkeletonEntityModel.addLimbs(modelPartData);
        return TexturedModelData.of(modelData, 64, 32);
    }

    protected static void addLimbs(ModelPartData data) {
        data.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(40, 16).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin(-5.0f, 2.0f, 0.0f));
        data.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin(5.0f, 2.0f, 0.0f));
        data.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin(-2.0f, 12.0f, 0.0f));
        data.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin(2.0f, 12.0f, 0.0f));
    }

    @Override
    public void setAngles(S skeletonEntityRenderState) {
        super.setAngles(skeletonEntityRenderState);
        if (((SkeletonEntityRenderState)skeletonEntityRenderState).attacking && !((SkeletonEntityRenderState)skeletonEntityRenderState).holdingBow) {
            float f = ((SkeletonEntityRenderState)skeletonEntityRenderState).handSwingProgress;
            float g = MathHelper.sin(f * (float)Math.PI);
            float h = MathHelper.sin((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI);
            this.rightArm.roll = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - g * 0.6f);
            this.leftArm.yaw = 0.1f - g * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.leftArm.pitch = -1.5707964f;
            this.rightArm.pitch -= g * 1.2f - h * 0.4f;
            this.leftArm.pitch -= g * 1.2f - h * 0.4f;
            ArmPosing.swingArms(this.rightArm, this.leftArm, ((SkeletonEntityRenderState)skeletonEntityRenderState).age);
        }
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.getRootPart().applyTransform(matrices);
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        ModelPart modelPart = this.getArm(arm);
        modelPart.originX += f;
        modelPart.applyTransform(matrices);
        modelPart.originX -= f;
    }
}

