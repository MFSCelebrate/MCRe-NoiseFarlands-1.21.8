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
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel
extends EntityModel<WolfEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(Set.of("head"));
    final static private String REAL_HEAD = "real_head";
    final static private String UPPER_BODY = "upper_body";
    final static private String REAL_TAIL = "real_tail";
    final private ModelPart head;
    final private ModelPart realHead;
    final private ModelPart torso;
    final private ModelPart rightHindLeg;
    final private ModelPart leftHindLeg;
    final private ModelPart rightFrontLeg;
    final private ModelPart leftFrontLeg;
    final private ModelPart tail;
    final private ModelPart realTail;
    final private ModelPart neck;
    final static private int field_32580 = 8;

    public WolfEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.realHead = this.head.getChild(REAL_HEAD);
        this.torso = modelPart.getChild(EntityModelPartNames.BODY);
        this.neck = modelPart.getChild(EntityModelPartNames.UPPER_BODY);
        this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
        this.realTail = this.tail.getChild(REAL_TAIL);
    }

    public static ModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 13.5f;
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.origin(-1.0f, 13.5f, -7.0f));
        modelPartData2.addChild(REAL_HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, dilation).uv(16, 14).cuboid(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, dilation).uv(16, 14).cuboid(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, dilation).uv(0, 10).cuboid(-0.5f, -0.001f, -5.0f, 3.0f, 3.0f, 4.0f, dilation), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(18, 14).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f, dilation), ModelTransform.of(0.0f, 14.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.UPPER_BODY, ModelPartBuilder.create().uv(21, 0).cuboid(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f, dilation), ModelTransform.of(-1.0f, 14.0f, -3.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().mirrored().uv(0, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder2, ModelTransform.origin(-2.5f, 16.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.origin(0.5f, 16.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.origin(-2.5f, 16.0f, -4.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(0.5f, 16.0f, -4.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create(), ModelTransform.of(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));
        modelPartData3.addChild(REAL_TAIL, ModelPartBuilder.create().uv(9, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation), ModelTransform.NONE);
        return modelData;
    }

    @Override
    public void setAngles(WolfEntityRenderState wolfEntityRenderState) {
        super.setAngles(wolfEntityRenderState);
        float f = wolfEntityRenderState.limbSwingAnimationProgress;
        float g = wolfEntityRenderState.limbSwingAmplitude;
        this.tail.yaw = wolfEntityRenderState.angerTime ? 0.0f : MathHelper.cos(f * 0.6662f) * 1.4f * g;
        if (wolfEntityRenderState.inSittingPose) {
            float h = wolfEntityRenderState.ageScale;
            this.neck.originY += 2.0f * h;
            this.neck.pitch = 1.2566371f;
            this.neck.yaw = 0.0f;
            this.torso.originY += 4.0f * h;
            this.torso.originZ -= 2.0f * h;
            this.torso.pitch = 0.7853982f;
            this.tail.originY += 9.0f * h;
            this.tail.originZ -= 2.0f * h;
            this.rightHindLeg.originY += 6.7f * h;
            this.rightHindLeg.originZ -= 5.0f * h;
            this.rightHindLeg.pitch = 4.712389f;
            this.leftHindLeg.originY += 6.7f * h;
            this.leftHindLeg.originZ -= 5.0f * h;
            this.leftHindLeg.pitch = 4.712389f;
            this.rightFrontLeg.pitch = 5.811947f;
            this.rightFrontLeg.originX += 0.01f * h;
            this.rightFrontLeg.originY += 1.0f * h;
            this.leftFrontLeg.pitch = 5.811947f;
            this.leftFrontLeg.originX -= 0.01f * h;
            this.leftFrontLeg.originY += 1.0f * h;
        } else {
            this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
            this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        }
        this.realHead.roll = wolfEntityRenderState.begAnimationProgress + wolfEntityRenderState.getRoll(0.0f);
        this.neck.roll = wolfEntityRenderState.getRoll(-0.08f);
        this.torso.roll = wolfEntityRenderState.getRoll(-0.16f);
        this.realTail.roll = wolfEntityRenderState.getRoll(-0.2f);
        this.head.pitch = wolfEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = wolfEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.tail.pitch = wolfEntityRenderState.tailAngle;
    }
}

