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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.VexEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VexEntityModel
extends EntityModel<VexEntityRenderState>
implements ModelWithArms {
    final private ModelPart body;
    final private ModelPart rightArm;
    final private ModelPart leftArm;
    final private ModelPart rightWing;
    final private ModelPart leftWing;
    final private ModelPart head;

    public VexEntityModel(ModelPart modelPart) {
        super(modelPart.getChild(EntityModelPartNames.ROOT), RenderLayer::getEntityTranslucent);
        this.body = this.root.getChild(EntityModelPartNames.BODY);
        this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
        this.head = this.root.getChild(EntityModelPartNames.HEAD);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.origin(0.0f, -2.5f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, 20.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 10).cuboid(-1.5f, 0.0f, -1.0f, 3.0f, 4.0f, 2.0f, new Dilation(0.0f)).uv(0, 16).cuboid(-1.5f, 1.0f, -1.0f, 3.0f, 5.0f, 2.0f, new Dilation(-0.2f)), ModelTransform.origin(0.0f, 20.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(23, 0).cuboid(-1.25f, -0.5f, -1.0f, 2.0f, 4.0f, 2.0f, new Dilation(-0.1f)), ModelTransform.origin(-1.75f, 0.25f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(23, 6).cuboid(-0.75f, -0.5f, -1.0f, 2.0f, 4.0f, 2.0f, new Dilation(-0.1f)), ModelTransform.origin(1.75f, 0.25f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(16, 14).mirrored().cuboid(0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 8.0f, new Dilation(0.0f)).mirrored(false), ModelTransform.origin(0.5f, 1.0f, 1.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(16, 14).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin(-0.5f, 1.0f, 1.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(VexEntityRenderState vexEntityRenderState) {
        super.setAngles(vexEntityRenderState);
        this.head.yaw = vexEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = vexEntityRenderState.pitch * ((float)Math.PI / 180);
        float f = MathHelper.cos(vexEntityRenderState.age * 5.5f * ((float)Math.PI / 180)) * 0.1f;
        this.rightArm.roll = 0.62831855f + f;
        this.leftArm.roll = -(0.62831855f + f);
        if (vexEntityRenderState.charging) {
            this.body.pitch = 0.0f;
            this.setChargingArmAngles(!vexEntityRenderState.rightHandItemState.isEmpty(), !vexEntityRenderState.leftHandItemState.isEmpty(), f);
        } else {
            this.body.pitch = 0.15707964f;
        }
        this.leftWing.yaw = 1.0995574f + MathHelper.cos(vexEntityRenderState.age * 45.836624f * ((float)Math.PI / 180)) * ((float)Math.PI / 180) * 16.2f;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.leftWing.pitch = 0.47123888f;
        this.leftWing.roll = -0.47123888f;
        this.rightWing.pitch = 0.47123888f;
        this.rightWing.roll = 0.47123888f;
    }

    private void setChargingArmAngles(boolean bl, boolean bl2, float f) {
        if (!bl && !bl2) {
            this.rightArm.pitch = -1.2217305f;
            this.rightArm.yaw = 0.2617994f;
            this.rightArm.roll = -0.47123888f - f;
            this.leftArm.pitch = -1.2217305f;
            this.leftArm.yaw = -0.2617994f;
            this.leftArm.roll = 0.47123888f + f;
            return;
        }
        if (bl) {
            this.rightArm.pitch = 3.6651914f;
            this.rightArm.yaw = 0.2617994f;
            this.rightArm.roll = -0.47123888f - f;
        }
        if (bl2) {
            this.leftArm.pitch = 3.6651914f;
            this.leftArm.yaw = -0.2617994f;
            this.leftArm.roll = 0.47123888f + f;
        }
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        boolean bl = arm == Arm.RIGHT;
        ModelPart modelPart = bl ? this.rightArm : this.leftArm;
        this.root.applyTransform(matrices);
        this.body.applyTransform(matrices);
        modelPart.applyTransform(matrices);
        matrices.scale(0.55f, 0.55f, 0.55f);
        this.translateForHand(matrices, bl);
    }

    private void translateForHand(MatrixStack matrices, boolean mainHand) {
        if (mainHand) {
            matrices.translate(0.046875, -0.15625, 0.078125);
        } else {
            matrices.translate(-0.046875, -0.15625, 0.078125);
        }
    }
}

