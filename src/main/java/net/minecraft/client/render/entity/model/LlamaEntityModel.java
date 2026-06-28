/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Map;
import java.util.function.UnaryOperator;
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
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel
extends EntityModel<LlamaEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = LlamaEntityModel::transformBaby;
    final private ModelPart head;
    final private ModelPart rightHindLeg;
    final private ModelPart leftHindLeg;
    final private ModelPart rightFrontLeg;
    final private ModelPart leftFrontLeg;
    final private ModelPart rightChest;
    final private ModelPart leftChest;

    public LlamaEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.rightChest = modelPart.getChild(EntityModelPartNames.RIGHT_CHEST);
        this.leftChest = modelPart.getChild(EntityModelPartNames.LEFT_CHEST);
        this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, dilation).uv(0, 14).cuboid(EntityModelPartNames.NECK, -4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, dilation).uv(17, 0).cuboid("ear", -4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, dilation).uv(17, 0).cuboid("ear", 1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, dilation), ModelTransform.origin(0.0f, 7.0f, -6.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(29, 0).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, dilation), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_CHEST, ModelPartBuilder.create().uv(45, 28).cuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation), ModelTransform.of(-8.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_CHEST, ModelPartBuilder.create().uv(45, 41).cuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation), ModelTransform.of(5.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        int i = 4;
        int j = 14;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(29, 29).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.origin(-3.5f, 10.0f, 6.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.origin(3.5f, 10.0f, 6.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(-3.5f, 10.0f, -5.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.origin(3.5f, 10.0f, -5.0f));
        return TexturedModelData.of(modelData, 128, 64);
    }

    private static ModelData transformBaby(ModelData modelData) {
        float f = 2.0f;
        float g = 0.7f;
        float h = 1.1f;
        UnaryOperator unaryOperator = modelTransform -> modelTransform.moveOrigin(0.0f, 21.0f, 3.52f).scaled(0.71428573f, 0.64935064f, 0.7936508f);
        UnaryOperator unaryOperator2 = modelTransform -> modelTransform.moveOrigin(0.0f, 33.0f, 0.0f).scaled(0.625f, 0.45454544f, 0.45454544f);
        UnaryOperator unaryOperator3 = modelTransform -> modelTransform.moveOrigin(0.0f, 33.0f, 0.0f).scaled(0.45454544f, 0.41322312f, 0.45454544f);
        ModelData modelData2 = new ModelData();
        for (Map.Entry<String, ModelPartData> entry : modelData.getRoot().getChildren()) {
            String string = entry.getKey();
            ModelPartData modelPartData = entry.getValue();
            UnaryOperator unaryOperator4 = switch (string) {
                case "head" -> unaryOperator;
                case "body" -> unaryOperator2;
                default -> unaryOperator3;
            };
            modelData2.getRoot().addChild(string, modelPartData.applyTransformer(unaryOperator4));
        }
        return modelData2;
    }

    @Override
    public void setAngles(LlamaEntityRenderState llamaEntityRenderState) {
        super.setAngles(llamaEntityRenderState);
        this.head.pitch = llamaEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = llamaEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float f = llamaEntityRenderState.limbSwingAmplitude;
        float g = llamaEntityRenderState.limbSwingAnimationProgress;
        this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.rightChest.visible = llamaEntityRenderState.hasChest;
        this.leftChest.visible = llamaEntityRenderState.hasChest;
    }
}

