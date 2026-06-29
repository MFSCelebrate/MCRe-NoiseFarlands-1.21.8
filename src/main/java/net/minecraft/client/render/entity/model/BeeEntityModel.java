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
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BeeEntityModel
extends EntityModel<BeeEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    final static private String BONE = "bone";
    final static private String STINGER = "stinger";
    final static private String LEFT_ANTENNA = "left_antenna";
    final static private String RIGHT_ANTENNA = "right_antenna";
    final static private String FRONT_LEGS = "front_legs";
    final static private String MIDDLE_LEGS = "middle_legs";
    final static private String BACK_LEGS = "back_legs";
    final private ModelPart bone;
    final private ModelPart rightWing;
    final private ModelPart leftWing;
    final private ModelPart frontLegs;
    final private ModelPart middleLegs;
    final private ModelPart backLegs;
    final private ModelPart stinger;
    final private ModelPart leftAntenna;
    final private ModelPart rightAntenna;
    private float bodyPitch;

    public BeeEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.bone = modelPart.getChild(EntityModelPartNames.BONE);
        ModelPart modelPart2 = this.bone.getChild(EntityModelPartNames.BODY);
        this.stinger = modelPart2.getChild(STINGER);
        this.leftAntenna = modelPart2.getChild(LEFT_ANTENNA);
        this.rightAntenna = modelPart2.getChild(RIGHT_ANTENNA);
        this.rightWing = this.bone.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = this.bone.getChild(EntityModelPartNames.LEFT_WING);
        this.frontLegs = this.bone.getChild(FRONT_LEGS);
        this.middleLegs = this.bone.getChild(MIDDLE_LEGS);
        this.backLegs = this.bone.getChild(BACK_LEGS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 19.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), ModelTransform.NONE);
        modelPartData3.addChild(STINGER, ModelPartBuilder.create().uv(26, 7).cuboid(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild(LEFT_ANTENNA, ModelPartBuilder.create().uv(2, 0).cuboid(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.origin(0.0f, -2.0f, -5.0f));
        modelPartData3.addChild(RIGHT_ANTENNA, ModelPartBuilder.create().uv(2, 3).cuboid(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.origin(0.0f, -2.0f, -5.0f));
        Dilation dilation = new Dilation(0.001f);
        modelPartData2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, 18).cuboid(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of(-1.5f, -4.0f, -3.0f, 0.0f, -0.2618f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, 18).mirrored().cuboid(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of(1.5f, -4.0f, -3.0f, 0.0f, 0.2618f, 0.0f));
        modelPartData2.addChild(FRONT_LEGS, ModelPartBuilder.create().cuboid(FRONT_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), ModelTransform.origin(1.5f, 3.0f, -2.0f));
        modelPartData2.addChild(MIDDLE_LEGS, ModelPartBuilder.create().cuboid(MIDDLE_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), ModelTransform.origin(1.5f, 3.0f, 0.0f));
        modelPartData2.addChild(BACK_LEGS, ModelPartBuilder.create().cuboid(BACK_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), ModelTransform.origin(1.5f, 3.0f, 2.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(BeeEntityRenderState beeEntityRenderState) {
        float f;
        super.setAngles(beeEntityRenderState);
        this.bodyPitch = beeEntityRenderState.bodyPitch;
        this.stinger.visible = beeEntityRenderState.hasStinger;
        if (!beeEntityRenderState.stoppedOnGround) {
            f = beeEntityRenderState.age * 120.32113f * ((float)Math.PI / 180);
            this.rightWing.yaw = 0.0f;
            this.rightWing.roll = MathHelper.cos(f) * (float)Math.PI * 0.15f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = this.rightWing.yaw;
            this.leftWing.roll = -this.rightWing.roll;
            this.frontLegs.pitch = 0.7853982f;
            this.middleLegs.pitch = 0.7853982f;
            this.backLegs.pitch = 0.7853982f;
        }
        if (!beeEntityRenderState.angry && !beeEntityRenderState.stoppedOnGround) {
            f = MathHelper.cos(beeEntityRenderState.age * 0.18f);
            this.bone.pitch = 0.1f + f * (float)Math.PI * 0.025f;
            this.leftAntenna.pitch = f * (float)Math.PI * 0.03f;
            this.rightAntenna.pitch = f * (float)Math.PI * 0.03f;
            this.frontLegs.pitch = -f * (float)Math.PI * 0.1f + 0.3926991f;
            this.backLegs.pitch = -f * (float)Math.PI * 0.05f + 0.7853982f;
            this.bone.originY -= MathHelper.cos(beeEntityRenderState.age * 0.18f) * 0.9f;
        }
        if (this.bodyPitch > 0.0f) {
            this.bone.pitch = MathHelper.lerpAngleRadians(this.bodyPitch, this.bone.pitch, 3.0915928f);
        }
    }
}

