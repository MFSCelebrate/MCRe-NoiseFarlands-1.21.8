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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel
extends EntityModel<BipedEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    final private ModelPart rightWing;
    final private ModelPart leftWing;

    public ElytraEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftWing = modelPart.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = modelPart.getChild(EntityModelPartNames.RIGHT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(1.0f);
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(22, 0).cuboid(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, dilation), ModelTransform.of(5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, -0.2617994f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(22, 0).mirrored().cuboid(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, dilation), ModelTransform.of(-5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, 0.2617994f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(BipedEntityRenderState bipedEntityRenderState) {
        super.setAngles(bipedEntityRenderState);
        this.leftWing.originY = bipedEntityRenderState.isInSneakingPose ? 3.0f : 0.0f;
        this.leftWing.pitch = bipedEntityRenderState.leftWingPitch;
        this.leftWing.roll = bipedEntityRenderState.leftWingRoll;
        this.leftWing.yaw = bipedEntityRenderState.leftWingYaw;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.originY = this.leftWing.originY;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.roll = -this.leftWing.roll;
    }
}

