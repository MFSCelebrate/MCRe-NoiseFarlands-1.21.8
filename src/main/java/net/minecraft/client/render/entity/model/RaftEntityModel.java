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
import net.minecraft.client.render.entity.model.AbstractBoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@Environment(value=EnvType.CLIENT)
public class RaftEntityModel
extends AbstractBoatEntityModel {
    public RaftEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    private static void addParts(ModelPartData modelPartData) {
        modelPartData.addChild(EntityModelPartNames.BOTTOM, ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -11.0f, -4.0f, 28.0f, 20.0f, 4.0f).uv(0, 0).cuboid(-14.0f, -9.0f, -8.0f, 28.0f, 16.0f, 4.0f), ModelTransform.of(0.0f, -2.1f, 1.0f, 1.5708f, 0.0f, 0.0f));
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        modelPartData.addChild(EntityModelPartNames.LEFT_PADDLE, ModelPartBuilder.create().uv(0, 24).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -4.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_PADDLE, ModelPartBuilder.create().uv(40, 24).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -4.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        RaftEntityModel.addParts(modelPartData);
        return TexturedModelData.of(modelData, 128, 64);
    }

    public static TexturedModelData getChestTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        RaftEntityModel.addParts(modelPartData);
        modelPartData.addChild(EntityModelPartNames.CHEST_BOTTOM, ModelPartBuilder.create().uv(0, 76).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 8.0f, 12.0f), ModelTransform.of(-2.0f, -10.1f, -6.0f, 0.0f, -1.5707964f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.CHEST_LID, ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 4.0f, 12.0f), ModelTransform.of(-2.0f, -14.1f, -6.0f, 0.0f, -1.5707964f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.CHEST_LOCK, ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 4.0f, 1.0f), ModelTransform.of(-1.0f, -11.1f, -1.0f, 0.0f, -1.5707964f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }
}

