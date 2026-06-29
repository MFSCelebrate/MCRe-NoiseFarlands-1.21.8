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
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CreeperEntityModel
extends EntityModel<CreeperEntityRenderState> {
    final private ModelPart head;
    final private ModelPart leftHindLeg;
    final private ModelPart rightHindLeg;
    final private ModelPart leftFrontLeg;
    final private ModelPart rightFrontLeg;
    final static private int HEAD_AND_BODY_Y_PIVOT = 6;

    public CreeperEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin(0.0f, 6.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(0.0f, 6.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.origin(-2.0f, 18.0f, 4.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.origin(2.0f, 18.0f, 4.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(-2.0f, 18.0f, -4.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(2.0f, 18.0f, -4.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(CreeperEntityRenderState creeperEntityRenderState) {
        super.setAngles(creeperEntityRenderState);
        this.head.yaw = creeperEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = creeperEntityRenderState.pitch * ((float)Math.PI / 180);
        float f = creeperEntityRenderState.limbSwingAmplitude;
        float g = creeperEntityRenderState.limbSwingAnimationProgress;
        this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
    }
}

