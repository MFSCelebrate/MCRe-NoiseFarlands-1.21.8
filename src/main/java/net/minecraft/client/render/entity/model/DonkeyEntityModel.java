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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel
extends AbstractHorseEntityModel<DonkeyEntityRenderState> {
    final static public float field_55113 = 0.87f;
    final static public float field_55114 = 0.92f;
    final static private ModelTransformer DONKEY_PARTS_ADDER = data -> {
        DonkeyEntityModel.addDonkeyParts(data.getRoot());
        return data;
    };
    final private ModelPart leftChest;
    final private ModelPart rightChest;

    public DonkeyEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftChest = this.body.getChild(EntityModelPartNames.LEFT_CHEST);
        this.rightChest = this.body.getChild(EntityModelPartNames.RIGHT_CHEST);
    }

    public static TexturedModelData getTexturedModelData(float scale) {
        return TexturedModelData.of(AbstractHorseEntityModel.getModelData(Dilation.NONE), 64, 64).transform(DONKEY_PARTS_ADDER).transform(ModelTransformer.scaling(scale));
    }

    public static TexturedModelData getBabyTexturedModelData(float scale) {
        return TexturedModelData.of(AbstractHorseEntityModel.getBabyModelData(Dilation.NONE), 64, 64).transform(DONKEY_PARTS_ADDER).transform(BABY_TRANSFORMER).transform(ModelTransformer.scaling(scale));
    }

    public static TexturedModelData getSaddleTexturedModelData(float scale, boolean baby) {
        return HorseSaddleEntityModel.getUntransformedTexturedModelData(baby).transform(DONKEY_PARTS_ADDER).transform(baby ? AbstractHorseEntityModel.BABY_TRANSFORMER : ModelTransformer.NO_OP).transform(ModelTransformer.scaling(scale));
    }

    private static void addDonkeyParts(ModelPartData root) {
        ModelPartData modelPartData = root.getChild(EntityModelPartNames.BODY);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        modelPartData.addChild(EntityModelPartNames.LEFT_CHEST, modelPartBuilder, ModelTransform.of(6.0f, -8.0f, 0.0f, 0.0f, -1.5707964f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_CHEST, modelPartBuilder, ModelTransform.of(-6.0f, -8.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        ModelPartData modelPartData2 = root.getChild("head_parts").getChild(EntityModelPartNames.HEAD);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 12).cuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, modelPartBuilder2, ModelTransform.of(1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, 0.2617994f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, modelPartBuilder2, ModelTransform.of(-1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, -0.2617994f));
    }

    @Override
    public void setAngles(DonkeyEntityRenderState donkeyEntityRenderState) {
        super.setAngles(donkeyEntityRenderState);
        this.leftChest.visible = donkeyEntityRenderState.hasChest;
        this.rightChest.visible = donkeyEntityRenderState.hasChest;
    }
}

