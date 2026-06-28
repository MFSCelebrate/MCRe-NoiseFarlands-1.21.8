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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends EntityModel<ParrotEntityRenderState> {
    final static private String FEATHER = "feather";
    final private ModelPart body;
    final private ModelPart tail;
    final private ModelPart leftWing;
    final private ModelPart rightWing;
    final private ModelPart head;
    final private ModelPart leftLeg;
    final private ModelPart rightLeg;

    public ParrotEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
        this.leftWing = modelPart.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = modelPart.getChild(EntityModelPartNames.RIGHT_WING);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(2, 8).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), ModelTransform.of(0.0f, 16.5f, -3.0f, 0.4937f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(22, 1).cuboid(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), ModelTransform.of(0.0f, 21.07f, 1.16f, 1.015f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.of(1.5f, 16.94f, -2.76f, -0.6981f, (float)(-Math.PI), 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.of(-1.5f, 16.94f, -2.76f, -0.6981f, (float)(-Math.PI), 0.0f));
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), ModelTransform.origin(0.0f, 15.69f, -2.76f));
        modelPartData2.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), ModelTransform.origin(0.0f, -2.0f, -1.0f));
        modelPartData2.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.origin(0.0f, -0.5f, -1.5f));
        modelPartData2.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.origin(0.0f, -1.75f, -2.45f));
        modelPartData2.addChild(FEATHER, ModelPartBuilder.create().uv(2, 18).cuboid(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), ModelTransform.of(0.0f, -2.15f, 0.15f, -0.2214f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, modelPartBuilder, ModelTransform.of(1.0f, 22.0f, -1.05f, -0.0299f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, modelPartBuilder, ModelTransform.of(-1.0f, 22.0f, -1.05f, -0.0299f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ParrotEntityRenderState parrotEntityRenderState) {
        super.setAngles(parrotEntityRenderState);
        this.animateModel(parrotEntityRenderState.parrotPose);
        this.head.pitch = parrotEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = parrotEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        switch (parrotEntityRenderState.parrotPose.ordinal()) {
            case 2: {
                break;
            }
            case 3: {
                float f = MathHelper.cos(parrotEntityRenderState.age);
                float g = MathHelper.sin(parrotEntityRenderState.age);
                this.head.originX += f;
                this.head.originY += g;
                this.head.pitch = 0.0f;
                this.head.yaw = 0.0f;
                this.head.roll = MathHelper.sin(parrotEntityRenderState.age) * 0.4f;
                this.body.originX += f;
                this.body.originY += g;
                this.leftWing.roll = -0.0873f - parrotEntityRenderState.flapAngle;
                this.leftWing.originX += f;
                this.leftWing.originY += g;
                this.rightWing.roll = 0.0873f + parrotEntityRenderState.flapAngle;
                this.rightWing.originX += f;
                this.rightWing.originY += g;
                this.tail.originX += f;
                this.tail.originY += g;
                break;
            }
            case 1: {
                this.leftLeg.pitch += MathHelper.cos(parrotEntityRenderState.limbSwingAnimationProgress * 0.6662f) * 1.4f * parrotEntityRenderState.limbSwingAmplitude;
                this.rightLeg.pitch += MathHelper.cos(parrotEntityRenderState.limbSwingAnimationProgress * 0.6662f + (float)Math.PI) * 1.4f * parrotEntityRenderState.limbSwingAmplitude;
            }
            default: {
                float h = parrotEntityRenderState.flapAngle * 0.3f;
                this.head.originY += h;
                this.tail.pitch += MathHelper.cos(parrotEntityRenderState.limbSwingAnimationProgress * 0.6662f) * 0.3f * parrotEntityRenderState.limbSwingAmplitude;
                this.tail.originY += h;
                this.body.originY += h;
                this.leftWing.roll = -0.0873f - parrotEntityRenderState.flapAngle;
                this.leftWing.originY += h;
                this.rightWing.roll = 0.0873f + parrotEntityRenderState.flapAngle;
                this.rightWing.originY += h;
                this.leftLeg.originY += h;
                this.rightLeg.originY += h;
            }
        }
    }

    private void animateModel(Pose pose) {
        switch (pose.ordinal()) {
            case 0: {
                this.leftLeg.pitch += 0.6981317f;
                this.rightLeg.pitch += 0.6981317f;
                break;
            }
            case 2: {
                float f = 1.9f;
                this.head.originY += 1.9f;
                this.tail.pitch += 0.5235988f;
                this.tail.originY += 1.9f;
                this.body.originY += 1.9f;
                this.leftWing.roll = -0.0873f;
                this.leftWing.originY += 1.9f;
                this.rightWing.roll = 0.0873f;
                this.rightWing.originY += 1.9f;
                this.leftLeg.originY += 1.9f;
                this.rightLeg.originY += 1.9f;
                this.leftLeg.pitch += 1.5707964f;
                this.rightLeg.pitch += 1.5707964f;
                break;
            }
            case 3: {
                this.leftLeg.roll = -0.34906584f;
                this.rightLeg.roll = 0.34906584f;
                break;
            }
        }
    }

    public static Pose getPose(ParrotEntity parrot) {
        if (parrot.isSongPlaying()) {
            return Pose.PARTY;
        }
        if (parrot.isInSittingPose()) {
            return Pose.SITTING;
        }
        if (parrot.isInAir()) {
            return Pose.FLYING;
        }
        return Pose.STANDING;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Pose
    extends Enum<Pose> {
        final static public Pose FLYING = new Pose();
        final static public Pose STANDING = new Pose();
        final static public Pose SITTING = new Pose();
        final static public Pose PARTY = new Pose();
        final static public Pose ON_SHOULDER = new Pose();
        final static private Pose[] field_3467;

        public static Pose[] values() {
            return (Pose[])field_3467.clone();
        }

        public static Pose valueOf(String string) {
            return Enum.valueOf(Pose.class, string);
        }

        private static Pose[] method_36893() {
            return new Pose[]{FLYING, STANDING, SITTING, PARTY, ON_SHOULDER};
        }

        static {
            field_3467 = Pose.method_36893();
        }
    }
}

