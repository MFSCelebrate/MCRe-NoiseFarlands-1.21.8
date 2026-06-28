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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PigEntityModel;

@Environment(value=EnvType.CLIENT)
public class ColdPigEntityModel
extends PigEntityModel {
    public ColdPigEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = ColdPigEntityModel.getModelData(dilation);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(28, 8).cuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f).uv(28, 32).cuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, new Dilation(0.5f)), ModelTransform.of(0.0f, 11.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }
}

