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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityModel
extends ZombieEntityModel<ZombieEntityRenderState> {
    public DrownedEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(16, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(1.9f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(ZombieEntityRenderState zombieEntityRenderState) {
        float f;
        super.setAngles(zombieEntityRenderState);
        if (zombieEntityRenderState.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
            this.leftArm.yaw = 0.0f;
        }
        if (zombieEntityRenderState.rightArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
            this.rightArm.yaw = 0.0f;
        }
        if ((f = zombieEntityRenderState.leaningPitch) > 0.0f) {
            this.rightArm.pitch = MathHelper.lerpAngleRadians(f, this.rightArm.pitch, -2.5132742f) + f * 0.35f * MathHelper.sin(0.1f * zombieEntityRenderState.age);
            this.leftArm.pitch = MathHelper.lerpAngleRadians(f, this.leftArm.pitch, -2.5132742f) - f * 0.35f * MathHelper.sin(0.1f * zombieEntityRenderState.age);
            this.rightArm.roll = MathHelper.lerpAngleRadians(f, this.rightArm.roll, -0.15f);
            this.leftArm.roll = MathHelper.lerpAngleRadians(f, this.leftArm.roll, 0.15f);
            this.leftLeg.pitch -= f * 0.55f * MathHelper.sin(0.1f * zombieEntityRenderState.age);
            this.rightLeg.pitch += f * 0.55f * MathHelper.sin(0.1f * zombieEntityRenderState.age);
            this.head.pitch = 0.0f;
        }
    }
}

