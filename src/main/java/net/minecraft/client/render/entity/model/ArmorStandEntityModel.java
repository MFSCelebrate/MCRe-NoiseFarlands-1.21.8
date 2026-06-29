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
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    final static private String RIGHT_BODY_STICK = "right_body_stick";
    final static private String LEFT_BODY_STICK = "left_body_stick";
    final static private String SHOULDER_STICK = "shoulder_stick";
    final static private String BASE_PLATE = "base_plate";
    final private ModelPart rightBodyStick;
    final private ModelPart leftBodyStick;
    final private ModelPart shoulderStick;
    final private ModelPart basePlate;

    public ArmorStandEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.rightBodyStick = modelPart.getChild(RIGHT_BODY_STICK);
        this.leftBodyStick = modelPart.getChild(LEFT_BODY_STICK);
        this.shoulderStick = modelPart.getChild(SHOULDER_STICK);
        this.basePlate = modelPart.getChild(BASE_PLATE);
        this.hat.visible = false;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.origin(0.0f, 1.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 26).cuboid(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(24, 0).cuboid(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin(-5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(32, 16).mirrored().cuboid(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin(5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(8, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.origin(-1.9f, 12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.origin(1.9f, 12.0f, 0.0f));
        modelPartData.addChild(RIGHT_BODY_STICK, ModelPartBuilder.create().uv(16, 0).cuboid(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(LEFT_BODY_STICK, ModelPartBuilder.create().uv(48, 16).cuboid(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(SHOULDER_STICK, ModelPartBuilder.create().uv(0, 48).cuboid(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(BASE_PLATE, ModelPartBuilder.create().uv(0, 32).cuboid(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f), ModelTransform.origin(0.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(ArmorStandEntityRenderState armorStandEntityRenderState) {
        super.setAngles(armorStandEntityRenderState);
        this.basePlate.yaw = (float)Math.PI / 180 * -armorStandEntityRenderState.yaw;
        this.leftArm.visible = armorStandEntityRenderState.showArms;
        this.rightArm.visible = armorStandEntityRenderState.showArms;
        this.basePlate.visible = armorStandEntityRenderState.showBasePlate;
        this.rightBodyStick.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.pitch();
        this.rightBodyStick.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.yaw();
        this.rightBodyStick.roll = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.roll();
        this.leftBodyStick.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.pitch();
        this.leftBodyStick.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.yaw();
        this.leftBodyStick.roll = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.roll();
        this.shoulderStick.pitch = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.pitch();
        this.shoulderStick.yaw = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.yaw();
        this.shoulderStick.roll = (float)Math.PI / 180 * armorStandEntityRenderState.bodyRotation.roll();
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        ModelPart modelPart = this.getArm(arm);
        boolean bl = modelPart.visible;
        modelPart.visible = true;
        super.setArmAngle(arm, matrices);
        modelPart.visible = bl;
    }
}

