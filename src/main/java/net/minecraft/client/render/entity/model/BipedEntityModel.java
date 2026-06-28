/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BipedEntityModel<T extends BipedEntityRenderState>
extends EntityModel<T>
implements ModelWithArms,
ModelWithHead {
    final static public ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f, Set.of("head"));
    final static public float field_32505 = 0.25f;
    final static public float field_32506 = 0.5f;
    final static public float field_42513 = -0.1f;
    final static private float field_42512 = 0.005f;
    final static private float SPYGLASS_ARM_YAW_OFFSET = 0.2617994f;
    final static private float SPYGLASS_ARM_PITCH_OFFSET = 1.9198622f;
    final static private float SPYGLASS_SNEAKING_ARM_PITCH_OFFSET = 0.2617994f;
    final static private float field_46576 = -1.3962634f;
    final static private float field_46577 = 0.43633232f;
    final static private float field_46724 = 0.5235988f;
    final static public float field_39069 = 1.4835298f;
    final static public float field_39070 = 0.5235988f;
    final public ModelPart head;
    final public ModelPart hat;
    final public ModelPart body;
    final public ModelPart rightArm;
    final public ModelPart leftArm;
    final public ModelPart rightLeg;
    final public ModelPart leftLeg;

    public BipedEntityModel(ModelPart modelPart) {
        this(modelPart, RenderLayer::getEntityCutoutNoCull);
    }

    public BipedEntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
        super(modelPart, function);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.hat = this.head.getChild(EntityModelPartNames.HAT);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
    }

    public static ModelData getModelData(Dilation dilation, float pivotOffsetY) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin(0.0f, 0.0f + pivotOffsetY, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation.add(0.5f)), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(0.0f, 0.0f + pivotOffsetY, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(40, 16).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(-5.0f, 2.0f + pivotOffsetY, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(5.0f, 2.0f + pivotOffsetY, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(-1.9f, 12.0f + pivotOffsetY, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(1.9f, 12.0f + pivotOffsetY, 0.0f));
        return modelData;
    }

    @Override
    public void setAngles(T bipedEntityRenderState) {
        boolean bl2;
        super.setAngles(bipedEntityRenderState);
        ArmPose armPose = ((BipedEntityRenderState)bipedEntityRenderState).leftArmPose;
        ArmPose armPose2 = ((BipedEntityRenderState)bipedEntityRenderState).rightArmPose;
        float f = ((BipedEntityRenderState)bipedEntityRenderState).leaningPitch;
        boolean bl = ((BipedEntityRenderState)bipedEntityRenderState).isGliding;
        this.head.pitch = ((BipedEntityRenderState)bipedEntityRenderState).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((BipedEntityRenderState)bipedEntityRenderState).relativeHeadYaw * ((float)Math.PI / 180);
        if (bl) {
            this.head.pitch = -0.7853982f;
        } else if (f > 0.0f) {
            this.head.pitch = MathHelper.lerpAngleRadians(f, this.head.pitch, -0.7853982f);
        }
        float g = ((BipedEntityRenderState)bipedEntityRenderState).limbSwingAnimationProgress;
        float h = ((BipedEntityRenderState)bipedEntityRenderState).limbSwingAmplitude;
        this.rightArm.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 2.0f * h * 0.5f / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.leftArm.pitch = MathHelper.cos(g * 0.6662f) * 2.0f * h * 0.5f / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.rightLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * h / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.leftLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * h / ((BipedEntityRenderState)bipedEntityRenderState).limbAmplitudeInverse;
        this.rightLeg.yaw = 0.005f;
        this.leftLeg.yaw = -0.005f;
        this.rightLeg.roll = 0.005f;
        this.leftLeg.roll = -0.005f;
        if (((BipedEntityRenderState)bipedEntityRenderState).hasVehicle) {
            this.rightArm.pitch += -0.62831855f;
            this.leftArm.pitch += -0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        boolean bl3 = bl2 = ((BipedEntityRenderState)bipedEntityRenderState).mainArm == Arm.RIGHT;
        if (((BipedEntityRenderState)bipedEntityRenderState).isUsingItem) {
            boolean bl4 = bl3 = ((BipedEntityRenderState)bipedEntityRenderState).activeHand == Hand.MAIN_HAND;
            if (bl3 == bl2) {
                this.positionRightArm(bipedEntityRenderState, armPose2);
            } else {
                this.positionLeftArm(bipedEntityRenderState, armPose);
            }
        } else {
            boolean bl5 = bl3 = bl2 ? armPose.isTwoHanded() : armPose2.isTwoHanded();
            if (bl2 != bl3) {
                this.positionLeftArm(bipedEntityRenderState, armPose);
                this.positionRightArm(bipedEntityRenderState, armPose2);
            } else {
                this.positionRightArm(bipedEntityRenderState, armPose2);
                this.positionLeftArm(bipedEntityRenderState, armPose);
            }
        }
        this.animateArms(bipedEntityRenderState, ((BipedEntityRenderState)bipedEntityRenderState).age);
        if (((BipedEntityRenderState)bipedEntityRenderState).isInSneakingPose) {
            this.body.pitch = 0.5f;
            this.rightArm.pitch += 0.4f;
            this.leftArm.pitch += 0.4f;
            this.rightLeg.originZ += 4.0f;
            this.leftLeg.originZ += 4.0f;
            this.head.originY += 4.2f;
            this.body.originY += 3.2f;
            this.leftArm.originY += 3.2f;
            this.rightArm.originY += 3.2f;
        }
        if (armPose2 != ArmPose.SPYGLASS) {
            ArmPosing.swingArm(this.rightArm, ((BipedEntityRenderState)bipedEntityRenderState).age, 1.0f);
        }
        if (armPose != ArmPose.SPYGLASS) {
            ArmPosing.swingArm(this.leftArm, ((BipedEntityRenderState)bipedEntityRenderState).age, -1.0f);
        }
        if (f > 0.0f) {
            float l;
            float k;
            float i = g % 26.0f;
            Arm arm = ((BipedEntityRenderState)bipedEntityRenderState).preferredArm;
            float j = arm == Arm.RIGHT && ((BipedEntityRenderState)bipedEntityRenderState).handSwingProgress > 0.0f ? 0.0f : f;
            float f2 = k = arm == Arm.LEFT && ((BipedEntityRenderState)bipedEntityRenderState).handSwingProgress > 0.0f ? 0.0f : f;
            if (!((BipedEntityRenderState)bipedEntityRenderState).isUsingItem) {
                if (i < 14.0f) {
                    this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 0.0f);
                    this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 0.0f);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float)Math.PI);
                    this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float)Math.PI);
                    this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, (float)Math.PI + 1.8707964f * this.method_2807(i) / this.method_2807(14.0f));
                    this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, (float)Math.PI - 1.8707964f * this.method_2807(i) / this.method_2807(14.0f));
                } else if (i >= 14.0f && i < 22.0f) {
                    l = (i - 14.0f) / 8.0f;
                    this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 1.5707964f * l);
                    this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 1.5707964f * l);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float)Math.PI);
                    this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float)Math.PI);
                    this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, 5.012389f - 1.8707964f * l);
                    this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, 1.2707963f + 1.8707964f * l);
                } else if (i >= 22.0f && i < 26.0f) {
                    l = (i - 22.0f) / 4.0f;
                    this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 1.5707964f - 1.5707964f * l);
                    this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 1.5707964f - 1.5707964f * l);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float)Math.PI);
                    this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float)Math.PI);
                    this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, (float)Math.PI);
                    this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, (float)Math.PI);
                }
            }
            l = 0.3f;
            float m = 0.33333334f;
            this.leftLeg.pitch = MathHelper.lerp(f, this.leftLeg.pitch, 0.3f * MathHelper.cos(g * 0.33333334f + (float)Math.PI));
            this.rightLeg.pitch = MathHelper.lerp(f, this.rightLeg.pitch, 0.3f * MathHelper.cos(g * 0.33333334f));
        }
    }

    private void positionRightArm(T state, ArmPose armPose) {
        switch (armPose.ordinal()) {
            case 0: {
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 2: {
                this.positionBlockingArm(this.rightArm, true);
                break;
            }
            case 1: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 4: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 3: {
                this.rightArm.yaw = -0.1f + this.head.yaw;
                this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case 5: {
                ArmPosing.charge(this.rightArm, this.leftArm, ((BipedEntityRenderState)state).crossbowPullTime, ((BipedEntityRenderState)state).itemUseTime, true);
                break;
            }
            case 6: {
                ArmPosing.hold(this.rightArm, this.leftArm, this.head, true);
                break;
            }
            case 9: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.62831855f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 7: {
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622f - (((BipedEntityRenderState)state).isInSneakingPose ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.rightArm.yaw = this.head.yaw - 0.2617994f;
                break;
            }
            case 8: {
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2f, 1.2f) - 1.4835298f;
                this.rightArm.yaw = this.head.yaw - 0.5235988f;
            }
        }
    }

    private void positionLeftArm(T state, ArmPose armPose) {
        switch (armPose.ordinal()) {
            case 0: {
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 2: {
                this.positionBlockingArm(this.leftArm, false);
                break;
            }
            case 1: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 4: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 3: {
                this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
                this.leftArm.yaw = 0.1f + this.head.yaw;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case 5: {
                ArmPosing.charge(this.rightArm, this.leftArm, ((BipedEntityRenderState)state).crossbowPullTime, ((BipedEntityRenderState)state).itemUseTime, false);
                break;
            }
            case 6: {
                ArmPosing.hold(this.rightArm, this.leftArm, this.head, false);
                break;
            }
            case 9: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.62831855f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 7: {
                this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622f - (((BipedEntityRenderState)state).isInSneakingPose ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.leftArm.yaw = this.head.yaw + 0.2617994f;
                break;
            }
            case 8: {
                this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2f, 1.2f) - 1.4835298f;
                this.leftArm.yaw = this.head.yaw + 0.5235988f;
            }
        }
    }

    private void positionBlockingArm(ModelPart arm, boolean rightArm) {
        arm.pitch = arm.pitch * 0.5f - 0.9424779f + MathHelper.clamp(this.head.pitch, -1.3962634f, 0.43633232f);
        arm.yaw = (rightArm ? -30.0f : 30.0f) * ((float)Math.PI / 180) + MathHelper.clamp(this.head.yaw, -0.5235988f, 0.5235988f);
    }

    protected void animateArms(T state, float animationProgress) {
        float f = ((BipedEntityRenderState)state).handSwingProgress;
        if (f <= 0.0f) {
            return;
        }
        Arm arm = ((BipedEntityRenderState)state).preferredArm;
        ModelPart modelPart = this.getArm(arm);
        float g = f;
        this.body.yaw = MathHelper.sin(MathHelper.sqrt(g) * ((float)Math.PI * 2)) * 0.2f;
        if (arm == Arm.LEFT) {
            this.body.yaw *= -1.0f;
        }
        float h = ((BipedEntityRenderState)state).ageScale;
        this.rightArm.originZ = MathHelper.sin(this.body.yaw) * 5.0f * h;
        this.rightArm.originX = -MathHelper.cos(this.body.yaw) * 5.0f * h;
        this.leftArm.originZ = -MathHelper.sin(this.body.yaw) * 5.0f * h;
        this.leftArm.originX = MathHelper.cos(this.body.yaw) * 5.0f * h;
        this.rightArm.yaw += this.body.yaw;
        this.leftArm.yaw += this.body.yaw;
        this.leftArm.pitch += this.body.yaw;
        g = 1.0f - f;
        g *= g;
        g *= g;
        g = 1.0f - g;
        float i = MathHelper.sin(g * (float)Math.PI);
        float j = MathHelper.sin(f * (float)Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
        modelPart.pitch -= i * 1.2f + j;
        modelPart.yaw += this.body.yaw * 2.0f;
        modelPart.roll += MathHelper.sin(f * (float)Math.PI) * -0.4f;
    }

    private float method_2807(float f) {
        return -65.0f * f + f * f;
    }

    public void copyTransforms(BipedEntityModel<T> model) {
        model.head.copyTransform(this.head);
        model.body.copyTransform(this.body);
        model.rightArm.copyTransform(this.rightArm);
        model.leftArm.copyTransform(this.leftArm);
        model.rightLeg.copyTransform(this.rightLeg);
        model.leftLeg.copyTransform(this.leftLeg);
    }

    public void setVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.body.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.root.applyTransform(matrices);
        this.getArm(arm).applyTransform(matrices);
    }

    protected ModelPart getArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ArmPose
    extends Enum<ArmPose> {
        final static public ArmPose EMPTY = new ArmPose(false);
        final static public ArmPose ITEM = new ArmPose(false);
        final static public ArmPose BLOCK = new ArmPose(false);
        final static public ArmPose BOW_AND_ARROW = new ArmPose(true);
        final static public ArmPose THROW_SPEAR = new ArmPose(false);
        final static public ArmPose CROSSBOW_CHARGE = new ArmPose(true);
        final static public ArmPose CROSSBOW_HOLD = new ArmPose(true);
        final static public ArmPose SPYGLASS = new ArmPose(false);
        final static public ArmPose TOOT_HORN = new ArmPose(false);
        final static public ArmPose BRUSH = new ArmPose(false);
        final private boolean twoHanded;
        final static private ArmPose[] field_3404;

        public static ArmPose[] values() {
            return (ArmPose[])field_3404.clone();
        }

        public static ArmPose valueOf(String string) {
            return Enum.valueOf(ArmPose.class, string);
        }

        private ArmPose(boolean twoHanded) {
            this.twoHanded = twoHanded;
        }

        public boolean isTwoHanded() {
            return this.twoHanded;
        }

        private static ArmPose[] method_36892() {
            return new ArmPose[]{EMPTY, ITEM, BLOCK, BOW_AND_ARROW, THROW_SPEAR, CROSSBOW_CHARGE, CROSSBOW_HOLD, SPYGLASS, TOOT_HORN, BRUSH};
        }

        static {
            field_3404 = ArmPose.method_36892();
        }
    }
}

