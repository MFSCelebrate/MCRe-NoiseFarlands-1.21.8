/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.entity.boss.dragon.EnderDragonFrameTracker;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DragonEntityModel
extends EntityModel<EnderDragonEntityRenderState> {
    final static private int NUM_NECK_PARTS = 5;
    final static private int NUM_TAIL_PARTS = 12;
    final private ModelPart head;
    final private ModelPart[] neckParts = new ModelPart[5];
    final private ModelPart[] tailParts = new ModelPart[12];
    final private ModelPart jaw;
    final private ModelPart body;
    final private ModelPart leftWing;
    final private ModelPart leftWingTip;
    final private ModelPart leftFrontLeg;
    final private ModelPart leftFrontLegTip;
    final private ModelPart leftFrontFoot;
    final private ModelPart leftHindLeg;
    final private ModelPart leftHindLegTip;
    final private ModelPart leftHindFoot;
    final private ModelPart rightWing;
    final private ModelPart rightWingTip;
    final private ModelPart rightFrontLeg;
    final private ModelPart rightFrontLegTip;
    final private ModelPart rightFrontFoot;
    final private ModelPart rightHindLeg;
    final private ModelPart rightHindLegTip;
    final private ModelPart rightHindFoot;

    private static String neck(int id) {
        return "neck" + id;
    }

    private static String tail(int id) {
        return "tail" + id;
    }

    public DragonEntityModel(ModelPart modelPart) {
        super(modelPart);
        int i;
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.jaw = this.head.getChild(EntityModelPartNames.JAW);
        for (i = 0; i < this.neckParts.length; ++i) {
            this.neckParts[i] = modelPart.getChild(DragonEntityModel.neck(i));
        }
        for (i = 0; i < this.tailParts.length; ++i) {
            this.tailParts[i] = modelPart.getChild(DragonEntityModel.tail(i));
        }
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
        this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
        this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.leftFrontLegTip = this.leftFrontLeg.getChild(EntityModelPartNames.LEFT_FRONT_LEG_TIP);
        this.leftFrontFoot = this.leftFrontLegTip.getChild(EntityModelPartNames.LEFT_FRONT_FOOT);
        this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.leftHindLegTip = this.leftHindLeg.getChild(EntityModelPartNames.LEFT_HIND_LEG_TIP);
        this.leftHindFoot = this.leftHindLegTip.getChild(EntityModelPartNames.LEFT_HIND_FOOT);
        this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
        this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
        this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.rightFrontLegTip = this.rightFrontLeg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG_TIP);
        this.rightFrontFoot = this.rightFrontLegTip.getChild(EntityModelPartNames.RIGHT_FRONT_FOOT);
        this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.rightHindLegTip = this.rightHindLeg.getChild(EntityModelPartNames.RIGHT_HIND_LEG_TIP);
        this.rightHindFoot = this.rightHindLegTip.getChild(EntityModelPartNames.RIGHT_HIND_FOOT);
    }

    public static TexturedModelData createTexturedModelData() {
        int i;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = -16.0f;
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().cuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).cuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).mirrored().cuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).mirrored().cuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), ModelTransform.origin(0.0f, 20.0f, -62.0f));
        modelPartData2.addChild(EntityModelPartNames.JAW, ModelPartBuilder.create().cuboid(EntityModelPartNames.JAW, -6.0f, 0.0f, -16.0f, 12, 4, 16, 176, 65), ModelTransform.origin(0.0f, 4.0f, -8.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().cuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 192, 104).cuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 48, 0);
        for (i = 0; i < 5; ++i) {
            modelPartData.addChild(DragonEntityModel.neck(i), modelPartBuilder, ModelTransform.origin(0.0f, 20.0f, -12.0f - (float)i * 10.0f));
        }
        for (i = 0; i < 12; ++i) {
            modelPartData.addChild(DragonEntityModel.tail(i), modelPartBuilder, ModelTransform.origin(0.0f, 10.0f, 60.0f + (float)i * 10.0f));
        }
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().cuboid(EntityModelPartNames.BODY, -12.0f, 1.0f, -16.0f, 24, 24, 64, 0, 0).cuboid("scale", -1.0f, -5.0f, -10.0f, 2, 6, 12, 220, 53).cuboid("scale", -1.0f, -5.0f, 10.0f, 2, 6, 12, 220, 53).cuboid("scale", -1.0f, -5.0f, 30.0f, 2, 6, 12, 220, 53), ModelTransform.origin(0.0f, 3.0f, 8.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().mirrored().cuboid(EntityModelPartNames.BONE, 0.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).cuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), ModelTransform.origin(12.0f, 2.0f, -6.0f));
        modelPartData4.addChild(EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().mirrored().cuboid(EntityModelPartNames.BONE, 0.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).cuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), ModelTransform.origin(56.0f, 0.0f, 0.0f));
        ModelPartData modelPartData5 = modelPartData3.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().cuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), ModelTransform.of(12.0f, 17.0f, -6.0f, 1.3f, 0.0f, 0.0f));
        ModelPartData modelPartData6 = modelPartData5.addChild(EntityModelPartNames.LEFT_FRONT_LEG_TIP, ModelPartBuilder.create().cuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), ModelTransform.of(0.0f, 20.0f, -1.0f, -0.5f, 0.0f, 0.0f));
        modelPartData6.addChild(EntityModelPartNames.LEFT_FRONT_FOOT, ModelPartBuilder.create().cuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), ModelTransform.of(0.0f, 23.0f, 0.0f, 0.75f, 0.0f, 0.0f));
        ModelPartData modelPartData7 = modelPartData3.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().cuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), ModelTransform.of(16.0f, 13.0f, 34.0f, 1.0f, 0.0f, 0.0f));
        ModelPartData modelPartData8 = modelPartData7.addChild(EntityModelPartNames.LEFT_HIND_LEG_TIP, ModelPartBuilder.create().cuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), ModelTransform.of(0.0f, 32.0f, -4.0f, 0.5f, 0.0f, 0.0f));
        modelPartData8.addChild(EntityModelPartNames.LEFT_HIND_FOOT, ModelPartBuilder.create().cuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), ModelTransform.of(0.0f, 31.0f, 4.0f, 0.75f, 0.0f, 0.0f));
        ModelPartData modelPartData9 = modelPartData3.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().cuboid(EntityModelPartNames.BONE, -56.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).cuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), ModelTransform.origin(-12.0f, 2.0f, -6.0f));
        modelPartData9.addChild(EntityModelPartNames.RIGHT_WING_TIP, ModelPartBuilder.create().cuboid(EntityModelPartNames.BONE, -56.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).cuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), ModelTransform.origin(-56.0f, 0.0f, 0.0f));
        ModelPartData modelPartData10 = modelPartData3.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().cuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), ModelTransform.of(-12.0f, 17.0f, -6.0f, 1.3f, 0.0f, 0.0f));
        ModelPartData modelPartData11 = modelPartData10.addChild(EntityModelPartNames.RIGHT_FRONT_LEG_TIP, ModelPartBuilder.create().cuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), ModelTransform.of(0.0f, 20.0f, -1.0f, -0.5f, 0.0f, 0.0f));
        modelPartData11.addChild(EntityModelPartNames.RIGHT_FRONT_FOOT, ModelPartBuilder.create().cuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), ModelTransform.of(0.0f, 23.0f, 0.0f, 0.75f, 0.0f, 0.0f));
        ModelPartData modelPartData12 = modelPartData3.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().cuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), ModelTransform.of(-16.0f, 13.0f, 34.0f, 1.0f, 0.0f, 0.0f));
        ModelPartData modelPartData13 = modelPartData12.addChild(EntityModelPartNames.RIGHT_HIND_LEG_TIP, ModelPartBuilder.create().cuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), ModelTransform.of(0.0f, 32.0f, -4.0f, 0.5f, 0.0f, 0.0f));
        modelPartData13.addChild(EntityModelPartNames.RIGHT_HIND_FOOT, ModelPartBuilder.create().cuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), ModelTransform.of(0.0f, 31.0f, 4.0f, 0.75f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void setAngles(EnderDragonEntityRenderState enderDragonEntityRenderState) {
        super.setAngles(enderDragonEntityRenderState);
        float f = enderDragonEntityRenderState.wingPosition * ((float)Math.PI * 2);
        this.jaw.pitch = (MathHelper.sin(f) + 1.0f) * 0.2f;
        float g = MathHelper.sin(f - 1.0f) + 1.0f;
        g = (g * g + g * 2.0f) * 0.05f;
        this.root.originY = (g - 2.0f) * 16.0f;
        this.root.originZ = -48.0f;
        this.root.pitch = g * 2.0f * ((float)Math.PI / 180);
        float h = this.neckParts[0].originX;
        float i = this.neckParts[0].originY;
        float j = this.neckParts[0].originZ;
        float k = 1.5f;
        EnderDragonFrameTracker.Frame frame = enderDragonEntityRenderState.getLerpedFrame(6);
        float l = MathHelper.wrapDegrees(enderDragonEntityRenderState.getLerpedFrame(5).yRot() - enderDragonEntityRenderState.getLerpedFrame(10).yRot());
        float m = MathHelper.wrapDegrees(enderDragonEntityRenderState.getLerpedFrame(5).yRot() + l / 2.0f);
        for (int n = 0; n < 5; ++n) {
            ModelPart modelPart = this.neckParts[n];
            EnderDragonFrameTracker.Frame frame2 = enderDragonEntityRenderState.getLerpedFrame(5 - n);
            float o = MathHelper.cos((float)n * 0.45f + f) * 0.15f;
            modelPart.yaw = MathHelper.wrapDegrees(frame2.yRot() - frame.yRot()) * ((float)Math.PI / 180) * 1.5f;
            modelPart.pitch = o + enderDragonEntityRenderState.getNeckPartPitchOffset(n, frame, frame2) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            modelPart.roll = -MathHelper.wrapDegrees(frame2.yRot() - m) * ((float)Math.PI / 180) * 1.5f;
            modelPart.originY = i;
            modelPart.originZ = j;
            modelPart.originX = h;
            h -= MathHelper.sin(modelPart.yaw) * MathHelper.cos(modelPart.pitch) * 10.0f;
            i += MathHelper.sin(modelPart.pitch) * 10.0f;
            j -= MathHelper.cos(modelPart.yaw) * MathHelper.cos(modelPart.pitch) * 10.0f;
        }
        this.head.originY = i;
        this.head.originZ = j;
        this.head.originX = h;
        EnderDragonFrameTracker.Frame frame3 = enderDragonEntityRenderState.getLerpedFrame(0);
        this.head.yaw = MathHelper.wrapDegrees(frame3.yRot() - frame.yRot()) * ((float)Math.PI / 180);
        this.head.pitch = MathHelper.wrapDegrees(enderDragonEntityRenderState.getNeckPartPitchOffset(6, frame, frame3)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
        this.head.roll = -MathHelper.wrapDegrees(frame3.yRot() - m) * ((float)Math.PI / 180);
        this.body.roll = -l * 1.5f * ((float)Math.PI / 180);
        this.leftWing.pitch = 0.125f - MathHelper.cos(f) * 0.2f;
        this.leftWing.yaw = -0.25f;
        this.leftWing.roll = -(MathHelper.sin(f) + 0.125f) * 0.8f;
        this.leftWingTip.roll = (MathHelper.sin(f + 2.0f) + 0.5f) * 0.75f;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.roll = -this.leftWing.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.setLegAngles(g, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftHindLeg, this.leftHindLegTip, this.leftHindFoot);
        this.setLegAngles(g, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightHindLeg, this.rightHindLegTip, this.rightHindFoot);
        float p = 0.0f;
        i = this.tailParts[0].originY;
        j = this.tailParts[0].originZ;
        h = this.tailParts[0].originX;
        frame = enderDragonEntityRenderState.getLerpedFrame(11);
        for (int q = 0; q < 12; ++q) {
            EnderDragonFrameTracker.Frame frame4 = enderDragonEntityRenderState.getLerpedFrame(12 + q);
            ModelPart modelPart2 = this.tailParts[q];
            modelPart2.yaw = (MathHelper.wrapDegrees(frame4.yRot() - frame.yRot()) * 1.5f + 180.0f) * ((float)Math.PI / 180);
            modelPart2.pitch = (p += MathHelper.sin((float)q * 0.45f + f) * 0.05f) + (float)(frame4.y() - frame.y()) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            modelPart2.roll = MathHelper.wrapDegrees(frame4.yRot() - m) * ((float)Math.PI / 180) * 1.5f;
            modelPart2.originY = i;
            modelPart2.originZ = j;
            modelPart2.originX = h;
            i += MathHelper.sin(modelPart2.pitch) * 10.0f;
            j -= MathHelper.cos(modelPart2.yaw) * MathHelper.cos(modelPart2.pitch) * 10.0f;
            h -= MathHelper.sin(modelPart2.yaw) * MathHelper.cos(modelPart2.pitch) * 10.0f;
        }
    }

    private void setLegAngles(float offset, ModelPart frontLeg, ModelPart frontLegTip, ModelPart frontFoot, ModelPart hindLeg, ModelPart hindLegTip, ModelPart hindFoot) {
        hindLeg.pitch = 1.0f + offset * 0.1f;
        hindLegTip.pitch = 0.5f + offset * 0.1f;
        hindFoot.pitch = 0.75f + offset * 0.1f;
        frontLeg.pitch = 1.3f + offset * 0.1f;
        frontLegTip.pitch = -0.5f - offset * 0.1f;
        frontFoot.pitch = 0.75f + offset * 0.1f;
    }
}

