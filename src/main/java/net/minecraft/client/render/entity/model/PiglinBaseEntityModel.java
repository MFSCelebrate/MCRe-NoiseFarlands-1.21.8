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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinBaseEntityModel<S extends BipedEntityRenderState>
extends BipedEntityModel<S> {
    final static private String LEFT_SLEEVE = "left_sleeve";
    final static private String RIGHT_SLEEVE = "right_sleeve";
    final static private String LEFT_PANTS = "left_pants";
    final static private String RIGHT_PANTS = "right_pants";
    final public ModelPart leftSleeve;
    final public ModelPart rightSleeve;
    final public ModelPart leftPants;
    final public ModelPart rightPants;
    final public ModelPart jacket;
    final public ModelPart rightEar;
    final public ModelPart leftEar;

    public PiglinBaseEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayer::getEntityTranslucent);
        this.leftSleeve = this.leftArm.getChild(LEFT_SLEEVE);
        this.rightSleeve = this.rightArm.getChild(RIGHT_SLEEVE);
        this.leftPants = this.leftLeg.getChild(LEFT_PANTS);
        this.rightPants = this.rightLeg.getChild(RIGHT_PANTS);
        this.jacket = this.body.getChild(EntityModelPartNames.JACKET);
        this.rightEar = this.head.getChild(EntityModelPartNames.RIGHT_EAR);
        this.leftEar = this.head.getChild(EntityModelPartNames.LEFT_EAR);
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = PlayerEntityModel.getTexturedModelData(dilation, false);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.NONE);
        ModelPartData modelPartData2 = PiglinBaseEntityModel.getModelPartData(dilation, modelData);
        modelPartData2.addChild(EntityModelPartNames.HAT);
        return modelData;
    }

    public static ModelPartData getModelPartData(Dilation dilation, ModelData playerModelData) {
        ModelPartData modelPartData = playerModelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, dilation).uv(31, 1).cuboid(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, dilation).uv(2, 4).cuboid(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, dilation).uv(2, 0).cuboid(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, dilation), ModelTransform.NONE);
        modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(51, 6).cuboid(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, dilation), ModelTransform.of(4.5f, -6.0f, 0.0f, 0.0f, 0.0f, -0.5235988f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(39, 6).cuboid(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, dilation), ModelTransform.of(-4.5f, -6.0f, 0.0f, 0.0f, 0.0f, 0.5235988f));
        return modelPartData2;
    }

    @Override
    public void setAngles(S bipedEntityRenderState) {
        super.setAngles(bipedEntityRenderState);
        float f = ((BipedEntityRenderState)bipedEntityRenderState).limbSwingAnimationProgress;
        float g = ((BipedEntityRenderState)bipedEntityRenderState).limbSwingAmplitude;
        float h = 0.5235988f;
        float i = ((BipedEntityRenderState)bipedEntityRenderState).age * 0.1f + f * 0.5f;
        float j = 0.08f + g * 0.4f;
        this.leftEar.roll = -0.5235988f - MathHelper.cos(i * 1.2f) * j;
        this.rightEar.roll = 0.5235988f + MathHelper.cos(i) * j;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }
}

