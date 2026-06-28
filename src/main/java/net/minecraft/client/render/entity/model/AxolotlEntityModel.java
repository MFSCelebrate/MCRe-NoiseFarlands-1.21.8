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
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityModel
extends EntityModel<AxolotlEntityRenderState> {
    final static public float MOVING_IN_WATER_LEG_PITCH = 1.8849558f;
    final static public ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    final private ModelPart tail;
    final private ModelPart leftHindLeg;
    final private ModelPart rightHindLeg;
    final private ModelPart leftFrontLeg;
    final private ModelPart rightFrontLeg;
    final private ModelPart body;
    final private ModelPart head;
    final private ModelPart topGills;
    final private ModelPart leftGills;
    final private ModelPart rightGills;

    public AxolotlEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
        this.topGills = this.head.getChild(EntityModelPartNames.TOP_GILLS);
        this.leftGills = this.head.getChild(EntityModelPartNames.LEFT_GILLS);
        this.rightGills = this.head.getChild(EntityModelPartNames.RIGHT_GILLS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 11).cuboid(-4.0f, -2.0f, -9.0f, 8.0f, 4.0f, 10.0f).uv(2, 17).cuboid(0.0f, -3.0f, -8.0f, 0.0f, 5.0f, 9.0f), ModelTransform.origin(0.0f, 20.0f, 5.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 1).cuboid(-4.0f, -3.0f, -5.0f, 8.0f, 5.0f, 5.0f, dilation), ModelTransform.origin(0.0f, 0.0f, -9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0f, -3.0f, 0.0f, 8.0f, 3.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder3 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        modelPartData3.addChild(EntityModelPartNames.TOP_GILLS, modelPartBuilder, ModelTransform.origin(0.0f, -3.0f, -1.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_GILLS, modelPartBuilder2, ModelTransform.origin(-4.0f, 0.0f, -1.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_GILLS, modelPartBuilder3, ModelTransform.origin(4.0f, 0.0f, -1.0f));
        ModelPartBuilder modelPartBuilder4 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder5 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        modelPartData2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder5, ModelTransform.origin(-3.5f, 1.0f, -1.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder4, ModelTransform.origin(3.5f, 1.0f, -1.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder5, ModelTransform.origin(-3.5f, 1.0f, -8.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder4, ModelTransform.origin(3.5f, 1.0f, -8.0f));
        modelPartData2.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(2, 19).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 5.0f, 12.0f), ModelTransform.origin(0.0f, 0.0f, 1.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(AxolotlEntityRenderState axolotlEntityRenderState) {
        super.setAngles(axolotlEntityRenderState);
        float f = axolotlEntityRenderState.playingDeadValue;
        float g = axolotlEntityRenderState.inWaterValue;
        float h = axolotlEntityRenderState.onGroundValue;
        float i = axolotlEntityRenderState.isMovingValue;
        float j = 1.0f - i;
        float k = 1.0f - Math.min(h, i);
        this.body.yaw += axolotlEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.setMovingInWaterAngles(axolotlEntityRenderState.age, axolotlEntityRenderState.pitch, Math.min(i, g));
        this.setStandingInWaterAngles(axolotlEntityRenderState.age, Math.min(j, g));
        this.setMovingOnGroundAngles(axolotlEntityRenderState.age, Math.min(i, h));
        this.setStandingOnGroundAngles(axolotlEntityRenderState.age, Math.min(j, h));
        this.setPlayingDeadAngles(f);
        this.copyLegAngles(k);
    }

    private void setStandingOnGroundAngles(float animationProgress, float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        float f = animationProgress * 0.09f;
        float g = MathHelper.sin(f);
        float h = MathHelper.cos(f);
        float i = g * g - 2.0f * g;
        float j = h * h - 3.0f * g;
        this.head.pitch += -0.09f * i * headYaw;
        this.head.roll += -0.2f * headYaw;
        this.tail.yaw += (-0.1f + 0.1f * i) * headYaw;
        float k = (0.6f + 0.05f * j) * headYaw;
        this.topGills.pitch += k;
        this.leftGills.yaw -= k;
        this.rightGills.yaw += k;
        this.leftHindLeg.pitch += 1.1f * headYaw;
        this.leftHindLeg.yaw += 1.0f * headYaw;
        this.leftFrontLeg.pitch += 0.8f * headYaw;
        this.leftFrontLeg.yaw += 2.3f * headYaw;
        this.leftFrontLeg.roll -= 0.5f * headYaw;
    }

    private void setMovingOnGroundAngles(float animationProgress, float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        float f = animationProgress * 0.11f;
        float g = MathHelper.cos(f);
        float h = (g * g - 2.0f * g) / 5.0f;
        float i = 0.7f * g;
        float j = 0.09f * g * headYaw;
        this.head.yaw += j;
        this.tail.yaw += j;
        float k = (0.6f - 0.08f * (g * g + 2.0f * MathHelper.sin(f))) * headYaw;
        this.topGills.pitch += k;
        this.leftGills.yaw -= k;
        this.rightGills.yaw += k;
        float l = 0.9424779f * headYaw;
        float m = 1.0995574f * headYaw;
        this.leftHindLeg.pitch += l;
        this.leftHindLeg.yaw += (1.5f - h) * headYaw;
        this.leftHindLeg.roll += -0.1f * headYaw;
        this.leftFrontLeg.pitch += m;
        this.leftFrontLeg.yaw += (1.5707964f - i) * headYaw;
        this.rightHindLeg.pitch += l;
        this.rightHindLeg.yaw += (-1.0f - h) * headYaw;
        this.rightFrontLeg.pitch += m;
        this.rightFrontLeg.yaw += (-1.5707964f - i) * headYaw;
    }

    private void setStandingInWaterAngles(float f, float g) {
        if (g <= 1.0E-5f) {
            return;
        }
        float h = f * 0.075f;
        float i = MathHelper.cos(h);
        float j = MathHelper.sin(h) * 0.15f;
        float k = -0.075f * g;
        this.body.pitch += k;
        this.body.originY -= j * g;
        this.head.pitch -= k;
        this.topGills.pitch += 0.2f * g;
        float l = -0.49f * g;
        this.leftGills.yaw += l;
        this.rightGills.yaw -= l;
        this.leftHindLeg.pitch += 2.2461946f * g;
        this.leftHindLeg.yaw += 0.47123894f * g;
        this.leftHindLeg.roll += 1.7278761f * g;
        this.leftFrontLeg.pitch += 0.5853982f * g;
        this.leftFrontLeg.yaw += 2.042035f * g;
        this.tail.yaw += 0.5f * g;
    }

    private void setMovingInWaterAngles(float f, float headPitch, float g) {
        if (g <= 1.0E-5f) {
            return;
        }
        float h = f * 0.33f;
        float i = MathHelper.sin(h);
        float j = MathHelper.cos(h);
        float k = 0.13f * i;
        this.body.pitch += (headPitch * ((float)Math.PI / 180) + k) * g;
        this.head.pitch -= k * 1.8f * g;
        this.body.originY -= 0.45f * j * g;
        this.topGills.pitch += (-0.5f * i - 0.8f) * g;
        float l = (0.3f * i + 0.9f) * g;
        this.leftGills.yaw += l;
        this.rightGills.yaw -= l;
        this.tail.yaw += 0.3f * MathHelper.cos(h * 0.9f) * g;
        this.leftHindLeg.pitch += 1.8849558f * g;
        this.leftHindLeg.yaw += -0.4f * i * g;
        this.leftHindLeg.roll += 1.5707964f * g;
        this.leftFrontLeg.pitch += 1.8849558f * g;
        this.leftFrontLeg.yaw += (-0.2f * j - 0.1f) * g;
        this.leftFrontLeg.roll += 1.5707964f * g;
    }

    private void setPlayingDeadAngles(float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        this.leftHindLeg.pitch += 1.4137167f * headYaw;
        this.leftHindLeg.yaw += 1.0995574f * headYaw;
        this.leftHindLeg.roll += 0.7853982f * headYaw;
        this.leftFrontLeg.pitch += 0.7853982f * headYaw;
        this.leftFrontLeg.yaw += 2.042035f * headYaw;
        this.body.pitch += -0.15f * headYaw;
        this.body.roll += 0.35f * headYaw;
    }

    private void copyLegAngles(float f) {
        if (f <= 1.0E-5f) {
            return;
        }
        this.rightHindLeg.pitch += this.leftHindLeg.pitch * f;
        ModelPart modelPart = this.rightHindLeg;
        modelPart.yaw = modelPart.yaw + -this.leftHindLeg.yaw * f;
        modelPart = this.rightHindLeg;
        modelPart.roll = modelPart.roll + -this.leftHindLeg.roll * f;
        this.rightFrontLeg.pitch += this.leftFrontLeg.pitch * f;
        modelPart = this.rightFrontLeg;
        modelPart.yaw = modelPart.yaw + -this.leftFrontLeg.yaw * f;
        modelPart = this.rightFrontLeg;
        modelPart.roll = modelPart.roll + -this.leftFrontLeg.roll * f;
    }
}

