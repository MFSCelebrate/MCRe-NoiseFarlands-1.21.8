/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.model;

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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BannerFlagBlockModel
extends Model {
    final private ModelPart flag;

    public BannerFlagBlockModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
        this.flag = root.getChild("flag");
    }

    public static TexturedModelData getTexturedModelData(boolean standing) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("flag", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f), ModelTransform.origin(0.0f, standing ? -44.0f : -20.5f, standing ? 0.0f : 10.5f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public void sway(float rotation) {
        this.flag.pitch = (-0.0125f + 0.01f * MathHelper.cos((float)Math.PI * 2 * rotation)) * (float)Math.PI;
    }
}

