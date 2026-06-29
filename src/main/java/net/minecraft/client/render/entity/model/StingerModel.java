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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;

@Environment(value=EnvType.CLIENT)
public class StingerModel
extends Model {
    public StingerModel(ModelPart root) {
        super(root, RenderLayer::getEntityCutout);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f);
        modelPartData.addChild("cross_1", modelPartBuilder, ModelTransform.rotation(0.7853982f, 0.0f, 0.0f));
        modelPartData.addChild("cross_2", modelPartBuilder, ModelTransform.rotation(2.3561945f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 16, 16);
    }
}

