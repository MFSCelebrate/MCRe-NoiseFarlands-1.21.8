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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.StriderEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StriderEntityModel
extends EntityModel<StriderEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    final static private String RIGHT_BOTTOM_BRISTLE = "right_bottom_bristle";
    final static private String RIGHT_MIDDLE_BRISTLE = "right_middle_bristle";
    final static private String RIGHT_TOP_BRISTLE = "right_top_bristle";
    final static private String LEFT_TOP_BRISTLE = "left_top_bristle";
    final static private String LEFT_MIDDLE_BRISTLE = "left_middle_bristle";
    final static private String LEFT_BOTTOM_BRISTLE = "left_bottom_bristle";
    final private ModelPart rightLeg;
    final private ModelPart leftLeg;
    final private ModelPart body;
    final private ModelPart rightBottomBristle;
    final private ModelPart rightMiddleBristle;
    final private ModelPart rightTopBristle;
    final private ModelPart leftTopBristle;
    final private ModelPart leftMiddleBristle;
    final private ModelPart leftBottomBristle;

    public StriderEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.rightBottomBristle = this.body.getChild(RIGHT_BOTTOM_BRISTLE);
        this.rightMiddleBristle = this.body.getChild(RIGHT_MIDDLE_BRISTLE);
        this.rightTopBristle = this.body.getChild(RIGHT_TOP_BRISTLE);
        this.leftTopBristle = this.body.getChild(LEFT_TOP_BRISTLE);
        this.leftMiddleBristle = this.body.getChild(LEFT_MIDDLE_BRISTLE);
        this.leftBottomBristle = this.body.getChild(LEFT_BOTTOM_BRISTLE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), ModelTransform.origin(-4.0f, 8.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 55).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), ModelTransform.origin(4.0f, 8.0f, 0.0f));
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f), ModelTransform.origin(0.0f, 1.0f, 0.0f));
        modelPartData2.addChild(RIGHT_BOTTOM_BRISTLE, ModelPartBuilder.create().uv(16, 65).cuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), ModelTransform.of(-8.0f, 4.0f, -8.0f, 0.0f, 0.0f, -1.2217305f));
        modelPartData2.addChild(RIGHT_MIDDLE_BRISTLE, ModelPartBuilder.create().uv(16, 49).cuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), ModelTransform.of(-8.0f, -1.0f, -8.0f, 0.0f, 0.0f, -1.134464f));
        modelPartData2.addChild(RIGHT_TOP_BRISTLE, ModelPartBuilder.create().uv(16, 33).cuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), ModelTransform.of(-8.0f, -5.0f, -8.0f, 0.0f, 0.0f, -0.87266463f));
        modelPartData2.addChild(LEFT_TOP_BRISTLE, ModelPartBuilder.create().uv(16, 33).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), ModelTransform.of(8.0f, -6.0f, -8.0f, 0.0f, 0.0f, 0.87266463f));
        modelPartData2.addChild(LEFT_MIDDLE_BRISTLE, ModelPartBuilder.create().uv(16, 49).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), ModelTransform.of(8.0f, -2.0f, -8.0f, 0.0f, 0.0f, 1.134464f));
        modelPartData2.addChild(LEFT_BOTTOM_BRISTLE, ModelPartBuilder.create().uv(16, 65).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), ModelTransform.of(8.0f, 3.0f, -8.0f, 0.0f, 0.0f, 1.2217305f));
        return TexturedModelData.of(modelData, 64, 128);
    }

    @Override
    public void setAngles(StriderEntityRenderState striderEntityRenderState) {
        super.setAngles(striderEntityRenderState);
        float f = striderEntityRenderState.limbSwingAnimationProgress;
        float g = Math.min(striderEntityRenderState.limbSwingAmplitude, 0.25f);
        if (!striderEntityRenderState.hasPassengers) {
            this.body.pitch = striderEntityRenderState.pitch * ((float)Math.PI / 180);
            this.body.yaw = striderEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        } else {
            this.body.pitch = 0.0f;
            this.body.yaw = 0.0f;
        }
        float h = 1.5f;
        this.body.roll = 0.1f * MathHelper.sin(f * 1.5f) * 4.0f * g;
        this.body.originY = 2.0f;
        this.body.originY -= 2.0f * MathHelper.cos(f * 1.5f) * 2.0f * g;
        this.leftLeg.pitch = MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.rightLeg.pitch = MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.leftLeg.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f) * g;
        this.rightLeg.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f + (float)Math.PI) * g;
        this.leftLeg.originY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.rightLeg.originY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.rightBottomBristle.roll = -1.2217305f;
        this.rightMiddleBristle.roll = -1.134464f;
        this.rightTopBristle.roll = -0.87266463f;
        this.leftTopBristle.roll = 0.87266463f;
        this.leftMiddleBristle.roll = 1.134464f;
        this.leftBottomBristle.roll = 1.2217305f;
        float i = MathHelper.cos(f * 1.5f + (float)Math.PI) * g;
        this.rightBottomBristle.roll += i * 1.3f;
        this.rightMiddleBristle.roll += i * 1.2f;
        this.rightTopBristle.roll += i * 0.6f;
        this.leftTopBristle.roll += i * 0.6f;
        this.leftMiddleBristle.roll += i * 1.2f;
        this.leftBottomBristle.roll += i * 1.3f;
        float j = 1.0f;
        float k = 1.0f;
        this.rightBottomBristle.roll += 0.05f * MathHelper.sin(striderEntityRenderState.age * 1.0f * -0.4f);
        this.rightMiddleBristle.roll += 0.1f * MathHelper.sin(striderEntityRenderState.age * 1.0f * 0.2f);
        this.rightTopBristle.roll += 0.1f * MathHelper.sin(striderEntityRenderState.age * 1.0f * 0.4f);
        this.leftTopBristle.roll += 0.1f * MathHelper.sin(striderEntityRenderState.age * 1.0f * 0.4f);
        this.leftMiddleBristle.roll += 0.1f * MathHelper.sin(striderEntityRenderState.age * 1.0f * 0.2f);
        this.leftBottomBristle.roll += 0.05f * MathHelper.sin(striderEntityRenderState.age * 1.0f * -0.4f);
    }
}

