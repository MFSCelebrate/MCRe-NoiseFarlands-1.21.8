/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.WardenAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.WardenEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WardenEntityModel
extends EntityModel<WardenEntityRenderState> {
    final static private float field_38324 = 13.0f;
    final static private float field_38325 = 1.0f;
    final protected ModelPart bone;
    final protected ModelPart body;
    final protected ModelPart head;
    final protected ModelPart rightTendril;
    final protected ModelPart leftTendril;
    final protected ModelPart leftLeg;
    final protected ModelPart leftArm;
    final protected ModelPart leftRibcage;
    final protected ModelPart rightArm;
    final protected ModelPart rightLeg;
    final protected ModelPart rightRibcage;
    final private List<ModelPart> tendrils;
    final private List<ModelPart> justBody;
    final private List<ModelPart> headAndLimbs;
    final private List<ModelPart> bodyHeadAndLimbs;
    final private Animation attackingAnimation;
    final private Animation chargingSonicBoomAnimation;
    final private Animation diggingAnimation;
    final private Animation emergingAnimation;
    final private Animation roaringAnimation;
    final private Animation sniffingAnimation;

    public WardenEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayer::getEntityCutoutNoCull);
        this.bone = modelPart.getChild(EntityModelPartNames.BONE);
        this.body = this.bone.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.rightLeg = this.bone.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = this.bone.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightTendril = this.head.getChild(EntityModelPartNames.RIGHT_TENDRIL);
        this.leftTendril = this.head.getChild(EntityModelPartNames.LEFT_TENDRIL);
        this.rightRibcage = this.body.getChild(EntityModelPartNames.RIGHT_RIBCAGE);
        this.leftRibcage = this.body.getChild(EntityModelPartNames.LEFT_RIBCAGE);
        this.tendrils = ImmutableList.of((Object)this.leftTendril, (Object)this.rightTendril);
        this.justBody = ImmutableList.of((Object)this.body);
        this.headAndLimbs = ImmutableList.of((Object)this.head, (Object)this.leftArm, (Object)this.rightArm, (Object)this.leftLeg, (Object)this.rightLeg);
        this.bodyHeadAndLimbs = ImmutableList.of((Object)this.body, (Object)this.head, (Object)this.leftArm, (Object)this.rightArm, (Object)this.leftLeg, (Object)this.rightLeg);
        this.attackingAnimation = WardenAnimations.ATTACKING.createAnimation(modelPart);
        this.chargingSonicBoomAnimation = WardenAnimations.CHARGING_SONIC_BOOM.createAnimation(modelPart);
        this.diggingAnimation = WardenAnimations.DIGGING.createAnimation(modelPart);
        this.emergingAnimation = WardenAnimations.EMERGING.createAnimation(modelPart);
        this.roaringAnimation = WardenAnimations.ROARING.createAnimation(modelPart);
        this.sniffingAnimation = WardenAnimations.SNIFFING.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -13.0f, -4.0f, 18.0f, 21.0f, 11.0f), ModelTransform.origin(0.0f, -21.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_RIBCAGE, ModelPartBuilder.create().uv(90, 11).cuboid(-2.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f), ModelTransform.origin(-7.0f, -2.0f, -4.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_RIBCAGE, ModelPartBuilder.create().uv(90, 11).mirrored().cuboid(-7.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f).mirrored(false), ModelTransform.origin(7.0f, -2.0f, -4.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 32).cuboid(-8.0f, -16.0f, -5.0f, 16.0f, 16.0f, 10.0f), ModelTransform.origin(0.0f, -13.0f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.RIGHT_TENDRIL, ModelPartBuilder.create().uv(52, 32).cuboid(-16.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), ModelTransform.origin(-8.0f, -12.0f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.LEFT_TENDRIL, ModelPartBuilder.create().uv(58, 0).cuboid(0.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), ModelTransform.origin(8.0f, -12.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(44, 50).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), ModelTransform.origin(-13.0f, -13.0f, 1.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(0, 58).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), ModelTransform.origin(13.0f, -13.0f, 1.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(76, 48).cuboid(-3.1f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), ModelTransform.origin(-5.9f, -13.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(76, 76).cuboid(-2.9f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), ModelTransform.origin(5.9f, -13.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(WardenEntityRenderState wardenEntityRenderState) {
        super.setAngles(wardenEntityRenderState);
        this.setHeadAngle(wardenEntityRenderState.relativeHeadYaw, wardenEntityRenderState.pitch);
        this.setLimbAngles(wardenEntityRenderState.limbSwingAnimationProgress, wardenEntityRenderState.limbSwingAmplitude);
        this.setHeadAndBodyAngles(wardenEntityRenderState.age);
        this.setTendrilPitches(wardenEntityRenderState, wardenEntityRenderState.age);
        this.attackingAnimation.apply(wardenEntityRenderState.attackingAnimationState, wardenEntityRenderState.age);
        this.chargingSonicBoomAnimation.apply(wardenEntityRenderState.chargingSonicBoomAnimationState, wardenEntityRenderState.age);
        this.diggingAnimation.apply(wardenEntityRenderState.diggingAnimationState, wardenEntityRenderState.age);
        this.emergingAnimation.apply(wardenEntityRenderState.emergingAnimationState, wardenEntityRenderState.age);
        this.roaringAnimation.apply(wardenEntityRenderState.roaringAnimationState, wardenEntityRenderState.age);
        this.sniffingAnimation.apply(wardenEntityRenderState.sniffingAnimationState, wardenEntityRenderState.age);
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.head.pitch = pitch * ((float)Math.PI / 180);
        this.head.yaw = yaw * ((float)Math.PI / 180);
    }

    private void setHeadAndBodyAngles(float animationProgress) {
        float f = animationProgress * 0.1f;
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        this.head.roll += 0.06f * g;
        this.head.pitch += 0.06f * h;
        this.body.roll += 0.025f * h;
        this.body.pitch += 0.025f * g;
    }

    private void setLimbAngles(float angle, float distance) {
        float f = Math.min(0.5f, 3.0f * distance);
        float g = angle * 0.8662f;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = Math.min(0.35f, f);
        this.head.roll += 0.3f * i * f;
        this.head.pitch += 1.2f * MathHelper.cos(g + 1.5707964f) * j;
        this.body.roll = 0.1f * i * f;
        this.body.pitch = 1.0f * h * j;
        this.leftLeg.pitch = 1.0f * h * f;
        this.rightLeg.pitch = 1.0f * MathHelper.cos(g + (float)Math.PI) * f;
        this.leftArm.pitch = -(0.8f * h * f);
        this.leftArm.roll = 0.0f;
        this.rightArm.pitch = -(0.8f * i * f);
        this.rightArm.roll = 0.0f;
        this.setArmPivots();
    }

    private void setArmPivots() {
        this.leftArm.yaw = 0.0f;
        this.leftArm.originZ = 1.0f;
        this.leftArm.originX = 13.0f;
        this.leftArm.originY = -13.0f;
        this.rightArm.yaw = 0.0f;
        this.rightArm.originZ = 1.0f;
        this.rightArm.originX = -13.0f;
        this.rightArm.originY = -13.0f;
    }

    private void setTendrilPitches(WardenEntityRenderState state, float animationProgress) {
        float f;
        this.leftTendril.pitch = f = state.tendrilAlpha * (float)(Math.cos((double)animationProgress * 2.25) * Math.PI * (double)0.1f);
        this.rightTendril.pitch = -f;
    }

    public List<ModelPart> getTendrils(WardenEntityRenderState state) {
        return this.tendrils;
    }

    public List<ModelPart> getBody(WardenEntityRenderState state) {
        return this.justBody;
    }

    public List<ModelPart> getHeadAndLimbs(WardenEntityRenderState state) {
        return this.headAndLimbs;
    }

    public List<ModelPart> getBodyHeadAndLimbs(WardenEntityRenderState state) {
        return this.bodyHeadAndLimbs;
    }
}

