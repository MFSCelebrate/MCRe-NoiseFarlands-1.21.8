/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.CreakingAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class CreakingEntityModel
extends EntityModel<CreakingEntityRenderState> {
    final static public List<ModelPart> INACTIVE_EMISSIVE_PARTS = List.of();
    final private ModelPart head;
    final private List<ModelPart> activeEmissiveParts;
    final private Animation walkingAnimation;
    final private Animation attackingAnimation;
    final private Animation invulnerableAnimation;
    final private Animation crumblingAnimation;

    public CreakingEntityModel(ModelPart modelPart) {
        super(modelPart);
        ModelPart modelPart2 = modelPart.getChild(EntityModelPartNames.ROOT);
        ModelPart modelPart3 = modelPart2.getChild(EntityModelPartNames.UPPER_BODY);
        this.head = modelPart3.getChild(EntityModelPartNames.HEAD);
        this.activeEmissiveParts = List.of(this.head);
        this.walkingAnimation = CreakingAnimations.WALKING.createAnimation(modelPart2);
        this.attackingAnimation = CreakingAnimations.ATTACKING.createAnimation(modelPart2);
        this.invulnerableAnimation = CreakingAnimations.INVULNERABLE.createAnimation(modelPart2);
        this.crumblingAnimation = CreakingAnimations.CRUMBLING.createAnimation(modelPart2);
    }

    private static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.UPPER_BODY, ModelPartBuilder.create(), ModelTransform.origin(-1.0f, -19.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -10.0f, -3.0f, 6.0f, 10.0f, 6.0f).uv(28, 31).cuboid(-3.0f, -13.0f, -3.0f, 6.0f, 3.0f, 6.0f).uv(12, 40).cuboid(3.0f, -13.0f, 0.0f, 9.0f, 14.0f, 0.0f).uv(34, 12).cuboid(-12.0f, -14.0f, 0.0f, 9.0f, 14.0f, 0.0f), ModelTransform.origin(-3.0f, -11.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 16).cuboid(0.0f, -3.0f, -3.0f, 6.0f, 13.0f, 5.0f).uv(24, 0).cuboid(-6.0f, -4.0f, -3.0f, 6.0f, 7.0f, 5.0f), ModelTransform.origin(0.0f, -7.0f, 1.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(22, 13).cuboid(-2.0f, -1.5f, -1.5f, 3.0f, 21.0f, 3.0f).uv(46, 0).cuboid(-2.0f, 19.5f, -1.5f, 3.0f, 4.0f, 3.0f), ModelTransform.origin(-7.0f, -9.5f, 1.5f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(30, 40).cuboid(0.0f, -1.0f, -1.5f, 3.0f, 16.0f, 3.0f).uv(52, 12).cuboid(0.0f, -5.0f, -1.5f, 3.0f, 4.0f, 3.0f).uv(52, 19).cuboid(0.0f, 15.0f, -1.5f, 3.0f, 4.0f, 3.0f), ModelTransform.origin(6.0f, -9.0f, 0.5f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(42, 40).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 16.0f, 3.0f).uv(45, 55).cuboid(-1.5f, 15.7f, -4.5f, 5.0f, 0.0f, 9.0f), ModelTransform.origin(1.5f, -16.0f, 0.5f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 34).cuboid(-3.0f, -1.5f, -1.5f, 3.0f, 19.0f, 3.0f).uv(45, 46).cuboid(-5.0f, 17.2f, -4.5f, 5.0f, 0.0f, 9.0f).uv(12, 34).cuboid(-3.0f, -4.5f, -1.5f, 3.0f, 3.0f, 3.0f), ModelTransform.origin(-1.0f, -17.5f, 0.5f));
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = CreakingEntityModel.getModelData();
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(CreakingEntityRenderState creakingEntityRenderState) {
        super.setAngles(creakingEntityRenderState);
        this.head.pitch = creakingEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = creakingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        if (creakingEntityRenderState.unrooted) {
            this.walkingAnimation.applyWalking(creakingEntityRenderState.limbSwingAnimationProgress, creakingEntityRenderState.limbSwingAmplitude, 1.0f, 1.0f);
        }
        this.attackingAnimation.apply(creakingEntityRenderState.attackAnimationState, creakingEntityRenderState.age);
        this.invulnerableAnimation.apply(creakingEntityRenderState.invulnerableAnimationState, creakingEntityRenderState.age);
        this.crumblingAnimation.apply(creakingEntityRenderState.crumblingAnimationState, creakingEntityRenderState.age);
    }

    public List<ModelPart> getEmissiveParts(CreakingEntityRenderState state) {
        if (!state.glowingEyes) {
            return INACTIVE_EMISSIVE_PARTS;
        }
        return this.activeEmissiveParts;
    }
}

