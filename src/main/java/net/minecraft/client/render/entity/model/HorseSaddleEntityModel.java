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
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class HorseSaddleEntityModel
extends AbstractHorseEntityModel<LivingHorseEntityRenderState> {
    final static private String SADDLE = "saddle";
    final static private String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
    final static private String LEFT_SADDLE_LINE = "left_saddle_line";
    final static private String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
    final static private String RIGHT_SADDLE_LINE = "right_saddle_line";
    final static private String HEAD_SADDLE = "head_saddle";
    final static private String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
    final private ModelPart[] saddleLines;

    public HorseSaddleEntityModel(ModelPart modelPart) {
        super(modelPart);
        ModelPart modelPart2 = this.head.getChild(LEFT_SADDLE_LINE);
        ModelPart modelPart3 = this.head.getChild(RIGHT_SADDLE_LINE);
        this.saddleLines = new ModelPart[]{modelPart2, modelPart3};
    }

    public static TexturedModelData getTexturedModelData(boolean baby) {
        return HorseSaddleEntityModel.getUntransformedTexturedModelData(baby).transform(baby ? BABY_TRANSFORMER : ModelTransformer.NO_OP);
    }

    public static TexturedModelData getUntransformedTexturedModelData(boolean baby) {
        ModelData modelData = baby ? HorseSaddleEntityModel.getBabyModelData(Dilation.NONE) : HorseSaddleEntityModel.getModelData(Dilation.NONE);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.getChild(EntityModelPartNames.BODY);
        ModelPartData modelPartData3 = modelPartData.getChild("head_parts");
        modelPartData2.addChild(SADDLE, ModelPartBuilder.create().uv(26, 0).cuboid(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData3.addChild(LEFT_SADDLE_MOUTH, ModelPartBuilder.create().uv(29, 5).cuboid(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild(RIGHT_SADDLE_MOUTH, ModelPartBuilder.create().uv(29, 5).cuboid(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild(LEFT_SADDLE_LINE, ModelPartBuilder.create().uv(32, 2).cuboid(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f), ModelTransform.rotation(-0.5235988f, 0.0f, 0.0f));
        modelPartData3.addChild(RIGHT_SADDLE_LINE, ModelPartBuilder.create().uv(32, 2).cuboid(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f), ModelTransform.rotation(-0.5235988f, 0.0f, 0.0f));
        modelPartData3.addChild(HEAD_SADDLE, ModelPartBuilder.create().uv(1, 1).cuboid(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, new Dilation(0.22f)), ModelTransform.NONE);
        modelPartData3.addChild(MOUTH_SADDLE_WRAP, ModelPartBuilder.create().uv(19, 0).cuboid(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, new Dilation(0.2f)), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(LivingHorseEntityRenderState livingHorseEntityRenderState) {
        super.setAngles(livingHorseEntityRenderState);
        for (ModelPart modelPart : this.saddleLines) {
            modelPart.visible = livingHorseEntityRenderState.hasPassengers;
        }
    }
}

