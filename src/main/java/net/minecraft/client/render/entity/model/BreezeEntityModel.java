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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.BreezeAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BreezeEntityModel
extends EntityModel<BreezeEntityRenderState> {
    final static private float field_47431 = 0.6f;
    final static private float field_47432 = 0.8f;
    final static private float field_47433 = 1.0f;
    final private ModelPart head;
    final private ModelPart eyes;
    final private ModelPart windBody;
    final private ModelPart windTop;
    final private ModelPart windMid;
    final private ModelPart windBottom;
    final private ModelPart rods;
    final private Animation idlingAnimation;
    final private Animation shootingAnimation;
    final private Animation slidingAnimation;
    final private Animation slidingBackAnimation;
    final private Animation inhalingAnimation;
    final private Animation longJumpingAnimation;

    public BreezeEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayer::getEntityTranslucent);
        this.windBody = modelPart.getChild(EntityModelPartNames.WIND_BODY);
        this.windBottom = this.windBody.getChild(EntityModelPartNames.WIND_BOTTOM);
        this.windMid = this.windBottom.getChild(EntityModelPartNames.WIND_MID);
        this.windTop = this.windMid.getChild(EntityModelPartNames.WIND_TOP);
        this.head = modelPart.getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.HEAD);
        this.eyes = this.head.getChild(EntityModelPartNames.EYES);
        this.rods = modelPart.getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.RODS);
        this.idlingAnimation = BreezeAnimations.IDLING.createAnimation(modelPart);
        this.shootingAnimation = BreezeAnimations.SHOOTING.createAnimation(modelPart);
        this.slidingAnimation = BreezeAnimations.SLIDING.createAnimation(modelPart);
        this.slidingBackAnimation = BreezeAnimations.SLIDING_BACK.createAnimation(modelPart);
        this.inhalingAnimation = BreezeAnimations.INHALING.createAnimation(modelPart);
        this.longJumpingAnimation = BreezeAnimations.LONG_JUMPING.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData(int textureWidth, int textureHeight) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.RODS, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 8.0f, 0.0f));
        modelPartData3.addChild("rod_1", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of(2.5981f, -3.0f, 1.5f, -2.7489f, -1.0472f, 3.1416f));
        modelPartData3.addChild("rod_2", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of(-2.5981f, -3.0f, 1.5f, -2.7489f, 1.0472f, 3.1416f));
        modelPartData3.addChild("rod_3", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of(0.0f, -3.0f, -3.0f, 0.3927f, 0.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(4, 24).cuboid(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new Dilation(0.0f)).uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, 4.0f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.EYES, ModelPartBuilder.create().uv(4, 24).cuboid(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new Dilation(0.0f)).uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        ModelPartData modelPartData5 = modelPartData.addChild(EntityModelPartNames.WIND_BODY, ModelPartBuilder.create(), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        ModelPartData modelPartData6 = modelPartData5.addChild(EntityModelPartNames.WIND_BOTTOM, ModelPartBuilder.create().uv(1, 83).cuboid(-2.5f, -7.0f, -2.5f, 5.0f, 7.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData7 = modelPartData6.addChild(EntityModelPartNames.WIND_MID, ModelPartBuilder.create().uv(74, 28).cuboid(-6.0f, -6.0f, -6.0f, 12.0f, 6.0f, 12.0f, new Dilation(0.0f)).uv(78, 32).cuboid(-4.0f, -6.0f, -4.0f, 8.0f, 6.0f, 8.0f, new Dilation(0.0f)).uv(49, 71).cuboid(-2.5f, -6.0f, -2.5f, 5.0f, 6.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData7.addChild(EntityModelPartNames.WIND_TOP, ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -8.0f, -9.0f, 18.0f, 8.0f, 18.0f, new Dilation(0.0f)).uv(6, 6).cuboid(-6.0f, -8.0f, -6.0f, 12.0f, 8.0f, 12.0f, new Dilation(0.0f)).uv(105, 57).cuboid(-2.5f, -8.0f, -2.5f, 5.0f, 8.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -6.0f, 0.0f));
        return TexturedModelData.of(modelData, textureWidth, textureHeight);
    }

    @Override
    public void setAngles(BreezeEntityRenderState breezeEntityRenderState) {
        super.setAngles(breezeEntityRenderState);
        this.idlingAnimation.apply(breezeEntityRenderState.idleAnimationState, breezeEntityRenderState.age);
        this.shootingAnimation.apply(breezeEntityRenderState.shootingAnimationState, breezeEntityRenderState.age);
        this.slidingAnimation.apply(breezeEntityRenderState.slidingAnimationState, breezeEntityRenderState.age);
        this.slidingBackAnimation.apply(breezeEntityRenderState.slidingBackAnimationState, breezeEntityRenderState.age);
        this.inhalingAnimation.apply(breezeEntityRenderState.inhalingAnimationState, breezeEntityRenderState.age);
        this.longJumpingAnimation.apply(breezeEntityRenderState.longJumpingAnimationState, breezeEntityRenderState.age);
    }

    public ModelPart getHead() {
        return this.head;
    }

    public ModelPart getEyes() {
        return this.eyes;
    }

    public ModelPart getRods() {
        return this.rods;
    }

    public ModelPart getWindBody() {
        return this.windBody;
    }
}

