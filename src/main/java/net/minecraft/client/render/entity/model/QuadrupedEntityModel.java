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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends LivingEntityRenderState>
extends EntityModel<T> {
    final protected ModelPart head;
    final protected ModelPart body;
    final protected ModelPart rightHindLeg;
    final protected ModelPart leftHindLeg;
    final protected ModelPart rightFrontLeg;
    final protected ModelPart leftFrontLeg;

    protected QuadrupedEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
    }

    public static ModelData getModelData(int stanceWidth, boolean leftMirrored, boolean rightMirrored, Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin(0.0f, 18 - stanceWidth, -6.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(28, 8).cuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, dilation), ModelTransform.of(0.0f, 17 - stanceWidth, 2.0f, 1.5707964f, 0.0f, 0.0f));
        QuadrupedEntityModel.addLegs(modelPartData, leftMirrored, rightMirrored, stanceWidth, dilation);
        return modelData;
    }

    static void addLegs(ModelPartData root, boolean leftMirrored, boolean rightMirrored, int stanceWidth, Dilation dilation) {
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().mirrored(rightMirrored).uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)stanceWidth, 4.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().mirrored(leftMirrored).uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)stanceWidth, 4.0f, dilation);
        root.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.origin(-3.0f, 24 - stanceWidth, 7.0f));
        root.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder2, ModelTransform.origin(3.0f, 24 - stanceWidth, 7.0f));
        root.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(-3.0f, 24 - stanceWidth, -5.0f));
        root.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.origin(3.0f, 24 - stanceWidth, -5.0f));
    }

    @Override
    public void setAngles(T livingEntityRenderState) {
        super.setAngles(livingEntityRenderState);
        this.head.pitch = ((LivingEntityRenderState)livingEntityRenderState).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw * ((float)Math.PI / 180);
        float f = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress;
        float g = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude;
        this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

