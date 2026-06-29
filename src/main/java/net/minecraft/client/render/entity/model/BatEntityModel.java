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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.BatAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.BatEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BatEntityModel
extends EntityModel<BatEntityRenderState> {
    final private ModelPart head;
    final private ModelPart body;
    final private ModelPart rightWing;
    final private ModelPart leftWing;
    final private ModelPart rightWingTip;
    final private ModelPart leftWingTip;
    final private ModelPart feet;
    final private Animation flyingAnimation;
    final private Animation roostingAnimation;

    public BatEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayer::getEntityCutout);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
        this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
        this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
        this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
        this.feet = this.body.getChild(EntityModelPartNames.FEET);
        this.flyingAnimation = BatAnimations.FLYING.createAnimation(modelPart);
        this.roostingAnimation = BatAnimations.ROOSTING.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, 0.0f, -1.0f, 3.0f, 5.0f, 2.0f), ModelTransform.origin(0.0f, 17.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 7).cuboid(-2.0f, -3.0f, -1.0f, 4.0f, 3.0f, 2.0f), ModelTransform.origin(0.0f, 17.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(1, 15).cuboid(-2.5f, -4.0f, 0.0f, 3.0f, 5.0f, 0.0f), ModelTransform.origin(-1.5f, -2.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(8, 15).cuboid(-0.1f, -3.0f, 0.0f, 3.0f, 5.0f, 0.0f), ModelTransform.origin(1.1f, -3.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(12, 0).cuboid(-2.0f, -2.0f, 0.0f, 2.0f, 7.0f, 0.0f), ModelTransform.origin(-1.5f, 0.0f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.RIGHT_WING_TIP, ModelPartBuilder.create().uv(16, 0).cuboid(-6.0f, -2.0f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.origin(-2.0f, 0.0f, 0.0f));
        ModelPartData modelPartData5 = modelPartData2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(12, 7).cuboid(0.0f, -2.0f, 0.0f, 2.0f, 7.0f, 0.0f), ModelTransform.origin(1.5f, 0.0f, 0.0f));
        modelPartData5.addChild(EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().uv(16, 8).cuboid(0.0f, -2.0f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.origin(2.0f, 0.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.FEET, ModelPartBuilder.create().uv(16, 16).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 2.0f, 0.0f), ModelTransform.origin(0.0f, 5.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(BatEntityRenderState batEntityRenderState) {
        super.setAngles(batEntityRenderState);
        if (batEntityRenderState.roosting) {
            this.setRoostingHeadAngles(batEntityRenderState.relativeHeadYaw);
        }
        this.flyingAnimation.apply(batEntityRenderState.flyingAnimationState, batEntityRenderState.age);
        this.roostingAnimation.apply(batEntityRenderState.roostingAnimationState, batEntityRenderState.age);
    }

    private void setRoostingHeadAngles(float yaw) {
        this.head.yaw = yaw * ((float)Math.PI / 180);
    }
}

