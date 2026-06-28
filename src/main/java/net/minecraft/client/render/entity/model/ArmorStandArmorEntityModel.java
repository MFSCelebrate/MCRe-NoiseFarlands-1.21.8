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
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ArmorStandArmorEntityModel
extends BipedEntityModel<ArmorStandEntityRenderState> {
    public ArmorStandArmorEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin(0.0f, 1.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation.add(0.5f)), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(-0.1f)), ModelTransform.origin(-1.9f, 11.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(-0.1f)), ModelTransform.origin(1.9f, 11.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(ArmorStandEntityRenderState armorStandEntityRenderState) {
        super.setAngles(armorStandEntityRenderState);
        this.head.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.headRotation.pitch();
        this.head.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.headRotation.yaw();
        this.head.roll = (float)Math.PI / 180 * armorStandEntityRenderState.headRotation.roll();
        this.body.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.pitch();
        this.body.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.yaw();
        this.body.roll = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.roll();
        this.leftArm.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.leftArmRotation.pitch();
        this.leftArm.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.leftArmRotation.yaw();
        this.leftArm.roll = (float)Math.PI / 180 * armorStandEntityRenderState.leftArmRotation.roll();
        this.rightArm.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.rightArmRotation.pitch();
        this.rightArm.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.rightArmRotation.yaw();
        this.rightArm.roll = (float)Math.PI / 180 * armorStandEntityRenderState.rightArmRotation.roll();
        this.leftLeg.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.leftLegRotation.pitch();
        this.leftLeg.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.leftLegRotation.yaw();
        this.leftLeg.roll = (float)Math.PI / 180 * armorStandEntityRenderState.leftLegRotation.roll();
        this.rightLeg.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.rightLegRotation.pitch();
        this.rightLeg.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.rightLegRotation.yaw();
        this.rightLeg.roll = (float)Math.PI / 180 * armorStandEntityRenderState.rightLegRotation.roll();
    }
}

