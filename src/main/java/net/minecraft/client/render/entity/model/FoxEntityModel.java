/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityModel
extends EntityModel<FoxEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0f, 3.35f, Set.of("head"));
    final public ModelPart head;
    final private ModelPart body;
    final private ModelPart rightHindLeg;
    final private ModelPart leftHindLeg;
    final private ModelPart rightFrontLeg;
    final private ModelPart leftFrontLeg;
    final private ModelPart tail;
    final static private int field_32477 = 6;
    final static private float HEAD_Y_PIVOT = 16.5f;
    final static private float LEG_Y_PIVOT = 17.5f;
    private float legPitchModifier;

    public FoxEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(1, 5).cuboid(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f), ModelTransform.origin(-1.0f, 16.5f, -3.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(8, 1).cuboid(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(15, 1).cuboid(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(6, 18).cuboid(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(24, 15).cuboid(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, -6.0f, 1.5707964f, 0.0f, 0.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(4, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder2, ModelTransform.origin(-5.0f, 17.5f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.origin(-1.0f, 17.5f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.origin(-5.0f, 17.5f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(-1.0f, 17.5f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(30, 0).cuboid(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f), ModelTransform.of(-4.0f, 15.0f, -1.0f, -0.05235988f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 48, 32);
    }

    @Override
    public void setAngles(FoxEntityRenderState foxEntityRenderState) {
        float i;
        super.setAngles(foxEntityRenderState);
        float f = foxEntityRenderState.limbSwingAmplitude;
        float g = foxEntityRenderState.limbSwingAnimationProgress;
        this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.head.roll = foxEntityRenderState.headRoll;
        this.rightHindLeg.visible = true;
        this.leftHindLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        float h = foxEntityRenderState.ageScale;
        if (foxEntityRenderState.inSneakingPose) {
            this.body.pitch += 0.10471976f;
            i = foxEntityRenderState.bodyRotationHeightOffset;
            this.body.originY += i * h;
            this.head.originY += i * h;
        } else if (foxEntityRenderState.sleeping) {
            this.body.roll = -1.5707964f;
            this.body.originY += 5.0f * h;
            this.tail.pitch = -2.6179938f;
            if (foxEntityRenderState.baby) {
                this.tail.pitch = -2.1816616f;
                this.body.originZ += 2.0f;
            }
            this.head.originX += 2.0f * h;
            this.head.originY += 2.99f * h;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.rightHindLeg.visible = false;
            this.leftHindLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if (foxEntityRenderState.sitting) {
            this.body.pitch = 0.5235988f;
            this.body.originY -= 7.0f * h;
            this.body.originZ += 3.0f * h;
            this.tail.pitch = 0.7853982f;
            this.tail.originZ -= 1.0f * h;
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (foxEntityRenderState.baby) {
                this.head.originY -= 1.75f;
                this.head.originZ -= 0.375f;
            } else {
                this.head.originY -= 6.5f;
                this.head.originZ += 2.75f;
            }
            this.rightHindLeg.pitch = -1.3089969f;
            this.rightHindLeg.originY += 4.0f * h;
            this.rightHindLeg.originZ -= 0.25f * h;
            this.leftHindLeg.pitch = -1.3089969f;
            this.leftHindLeg.originY += 4.0f * h;
            this.leftHindLeg.originZ -= 0.25f * h;
            this.rightFrontLeg.pitch = -0.2617994f;
            this.leftFrontLeg.pitch = -0.2617994f;
        }
        if (!(foxEntityRenderState.sleeping || foxEntityRenderState.walking || foxEntityRenderState.inSneakingPose)) {
            this.head.pitch = foxEntityRenderState.pitch * ((float)Math.PI / 180);
            this.head.yaw = foxEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        }
        if (foxEntityRenderState.sleeping) {
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(foxEntityRenderState.age * 0.027f) / 22.0f;
        }
        if (foxEntityRenderState.inSneakingPose) {
            this.body.yaw = i = MathHelper.cos(foxEntityRenderState.age) * 0.01f;
            this.rightHindLeg.roll = i;
            this.leftHindLeg.roll = i;
            this.rightFrontLeg.roll = i / 2.0f;
            this.leftFrontLeg.roll = i / 2.0f;
        }
        if (foxEntityRenderState.walking) {
            i = 0.1f;
            this.legPitchModifier += 0.67f;
            this.rightHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
            this.leftHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.rightFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.leftFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
        }
    }
}

