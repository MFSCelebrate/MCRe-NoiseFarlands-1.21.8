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
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.FrogAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.FrogEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class FrogEntityModel
extends EntityModel<FrogEntityRenderState> {
    final static private float WALKING_LIMB_ANGLE_SCALE = 1.5f;
    final static private float SWIMMING_LIMB_ANGLE_SCALE = 1.0f;
    final static private float LIMB_DISTANCE_SCALE = 2.5f;
    final private ModelPart body;
    final private ModelPart head;
    final private ModelPart eyes;
    final private ModelPart tongue;
    final private ModelPart leftArm;
    final private ModelPart rightArm;
    final private ModelPart leftLeg;
    final private ModelPart rightLeg;
    final private ModelPart croakingBody;
    final private Animation longJumpingAnimation;
    final private Animation croakingAnimation;
    final private Animation usingTongueAnimation;
    final private Animation swimmingAnimation;
    final private Animation walkingAnimation;
    final private Animation idlingInWaterAnimation;

    public FrogEntityModel(ModelPart modelPart) {
        super(modelPart.getChild(EntityModelPartNames.ROOT));
        this.body = this.root.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.eyes = this.head.getChild(EntityModelPartNames.EYES);
        this.tongue = this.body.getChild(EntityModelPartNames.TONGUE);
        this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftLeg = this.root.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = this.root.getChild(EntityModelPartNames.RIGHT_LEG);
        this.croakingBody = this.body.getChild(EntityModelPartNames.CROAKING_BODY);
        this.longJumpingAnimation = FrogAnimations.LONG_JUMPING.createAnimation(modelPart);
        this.croakingAnimation = FrogAnimations.CROAKING.createAnimation(modelPart);
        this.usingTongueAnimation = FrogAnimations.USING_TONGUE.createAnimation(modelPart);
        this.swimmingAnimation = FrogAnimations.SWIMMING.createAnimation(modelPart);
        this.walkingAnimation = FrogAnimations.WALKING.createAnimation(modelPart);
        this.idlingInWaterAnimation = FrogAnimations.IDLING_IN_WATER.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(3, 1).cuboid(-3.5f, -2.0f, -8.0f, 7.0f, 3.0f, 9.0f).uv(23, 22).cuboid(-3.5f, -1.0f, -8.0f, 7.0f, 0.0f, 9.0f), ModelTransform.origin(0.0f, -2.0f, 4.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(23, 13).cuboid(-3.5f, -1.0f, -7.0f, 7.0f, 0.0f, 9.0f).uv(0, 13).cuboid(-3.5f, -2.0f, -7.0f, 7.0f, 3.0f, 9.0f), ModelTransform.origin(0.0f, -2.0f, -1.0f));
        ModelPartData modelPartData5 = modelPartData4.addChild(EntityModelPartNames.EYES, ModelPartBuilder.create(), ModelTransform.origin(-0.5f, 0.0f, 2.0f));
        modelPartData5.addChild(EntityModelPartNames.RIGHT_EYE, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.origin(-1.5f, -3.0f, -6.5f));
        modelPartData5.addChild(EntityModelPartNames.LEFT_EYE, ModelPartBuilder.create().uv(0, 5).cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.origin(2.5f, -3.0f, -6.5f));
        modelPartData3.addChild(EntityModelPartNames.CROAKING_BODY, ModelPartBuilder.create().uv(26, 5).cuboid(-3.5f, -0.1f, -2.9f, 7.0f, 2.0f, 3.0f, new Dilation(-0.1f)), ModelTransform.origin(0.0f, -1.0f, -5.0f));
        ModelPartData modelPartData6 = modelPartData3.addChild(EntityModelPartNames.TONGUE, ModelPartBuilder.create().uv(17, 13).cuboid(-2.0f, 0.0f, -7.1f, 4.0f, 0.0f, 7.0f), ModelTransform.origin(0.0f, -1.01f, 1.0f));
        ModelPartData modelPartData7 = modelPartData3.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(0, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), ModelTransform.origin(4.0f, -1.0f, -6.5f));
        modelPartData7.addChild(EntityModelPartNames.LEFT_HAND, ModelPartBuilder.create().uv(18, 40).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(0.0f, 3.0f, -1.0f));
        ModelPartData modelPartData8 = modelPartData3.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(0, 38).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), ModelTransform.origin(-4.0f, -1.0f, -6.5f));
        modelPartData8.addChild(EntityModelPartNames.RIGHT_HAND, ModelPartBuilder.create().uv(2, 40).cuboid(-4.0f, 0.01f, -5.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(0.0f, 3.0f, 0.0f));
        ModelPartData modelPartData9 = modelPartData2.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(14, 25).cuboid(-1.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), ModelTransform.origin(3.5f, -3.0f, 4.0f));
        modelPartData9.addChild(EntityModelPartNames.LEFT_FOOT, ModelPartBuilder.create().uv(2, 32).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(2.0f, 3.0f, 0.0f));
        ModelPartData modelPartData10 = modelPartData2.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 25).cuboid(-2.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), ModelTransform.origin(-3.5f, -3.0f, 4.0f));
        modelPartData10.addChild(EntityModelPartNames.RIGHT_FOOT, ModelPartBuilder.create().uv(18, 32).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(-2.0f, 3.0f, 0.0f));
        return TexturedModelData.of(modelData, 48, 48);
    }

    @Override
    public void setAngles(FrogEntityRenderState frogEntityRenderState) {
        super.setAngles(frogEntityRenderState);
        this.longJumpingAnimation.apply(frogEntityRenderState.longJumpingAnimationState, frogEntityRenderState.age);
        this.croakingAnimation.apply(frogEntityRenderState.croakingAnimationState, frogEntityRenderState.age);
        this.usingTongueAnimation.apply(frogEntityRenderState.usingTongueAnimationState, frogEntityRenderState.age);
        if (frogEntityRenderState.insideWaterOrBubbleColumn) {
            this.swimmingAnimation.applyWalking(frogEntityRenderState.limbSwingAnimationProgress, frogEntityRenderState.limbSwingAmplitude, 1.0f, 2.5f);
        } else {
            this.walkingAnimation.applyWalking(frogEntityRenderState.limbSwingAnimationProgress, frogEntityRenderState.limbSwingAmplitude, 1.5f, 2.5f);
        }
        this.idlingInWaterAnimation.apply(frogEntityRenderState.idlingInWaterAnimationState, frogEntityRenderState.age);
        this.croakingBody.visible = frogEntityRenderState.croakingAnimationState.isRunning();
    }
}

