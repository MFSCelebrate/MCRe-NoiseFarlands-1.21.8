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
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@Environment(value=EnvType.CLIENT)
public class ColdCowEntityModel
extends CowEntityModel {
    public ColdCowEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ColdCowEntityModel.getModelData();
        modelData.getRoot().addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(20, 32).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, new Dilation(0.5f)).uv(18, 4).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).uv(52, 0).cuboid(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartData modelPartData = modelData.getRoot().addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).uv(9, 33).cuboid(-3.0f, 1.0f, -7.0f, 6.0f, 3.0f, 1.0f), ModelTransform.origin(0.0f, 4.0f, -8.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_HORN, ModelPartBuilder.create().uv(0, 40).cuboid(-1.5f, -4.5f, -0.5f, 2.0f, 6.0f, 2.0f), ModelTransform.of(-4.5f, -2.5f, -3.5f, 1.5708f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HORN, ModelPartBuilder.create().uv(0, 32).cuboid(-1.5f, -3.0f, -0.5f, 2.0f, 6.0f, 2.0f), ModelTransform.of(5.5f, -2.5f, -5.0f, 1.5708f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }
}

