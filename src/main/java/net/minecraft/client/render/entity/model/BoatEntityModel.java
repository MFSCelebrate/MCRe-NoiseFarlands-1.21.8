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
public class BoatEntityModel
extends AbstractBoatEntityModel {
    final static private int field_52877 = 28;
    final static private int field_52878 = 32;
    final static private int field_52879 = 6;
    final static private int field_52880 = 20;
    final static private int field_52881 = 4;
    final static private String WATER_PATCH = "water_patch";
    final static private String BACK = "back";
    final static private String FRONT = "front";
    final static private String RIGHT = "right";
    final static private String LEFT = "left";

    public BoatEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    private static void addParts(ModelPartData modelPartData) {
        int i = 16;
        int j = 14;
        int k = 10;
        modelPartData.addChild(EntityModelPartNames.BOTTOM, ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of(0.0f, 3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild(BACK, ModelPartBuilder.create().uv(0, 19).cuboid(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f), ModelTransform.of(-15.0f, 4.0f, 4.0f, 0.0f, 4.712389f, 0.0f));
        modelPartData.addChild(FRONT, ModelPartBuilder.create().uv(0, 27).cuboid(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f), ModelTransform.of(15.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild(RIGHT, ModelPartBuilder.create().uv(0, 35).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, -9.0f, 0.0f, (float)Math.PI, 0.0f));
        modelPartData.addChild(LEFT, ModelPartBuilder.create().uv(0, 43).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.origin(0.0f, 4.0f, 9.0f));
        int l = 20;
        int m = 7;
        int n = 6;
        float f = -5.0f;
        modelPartData.addChild(EntityModelPartNames.LEFT_PADDLE, ModelPartBuilder.create().uv(62, 0).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_PADDLE, ModelPartBuilder.create().uv(62, 20).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        BoatEntityModel.addParts(modelPartData);
        return TexturedModelData.of(modelData, 128, 64);
    }

    public static TexturedModelData getChestTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        BoatEntityModel.addParts(modelPartData);
        modelPartData.addChild(EntityModelPartNames.CHEST_BOTTOM, ModelPartBuilder.create().uv(0, 76).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 8.0f, 12.0f), ModelTransform.of(-2.0f, -5.0f, -6.0f, 0.0f, -1.5707964f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.CHEST_LID, ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 4.0f, 12.0f), ModelTransform.of(-2.0f, -9.0f, -6.0f, 0.0f, -1.5707964f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.CHEST_LOCK, ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 4.0f, 1.0f), ModelTransform.of(-1.0f, -6.0f, -1.0f, 0.0f, -1.5707964f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    public static TexturedModelData getBaseTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(WATER_PATCH, ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of(0.0f, -3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 0, 0);
    }
}

