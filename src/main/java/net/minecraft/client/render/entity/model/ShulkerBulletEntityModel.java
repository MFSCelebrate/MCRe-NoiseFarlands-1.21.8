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
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityModel
extends EntityModel<ShulkerBulletEntityRenderState> {
    final static private String MAIN = "main";
    final private ModelPart bullet;

    public ShulkerBulletEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.bullet = modelPart.getChild(MAIN);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(MAIN, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f).uv(0, 10).cuboid(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f).uv(20, 0).cuboid(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(ShulkerBulletEntityRenderState shulkerBulletEntityRenderState) {
        super.setAngles(shulkerBulletEntityRenderState);
        this.bullet.yaw = shulkerBulletEntityRenderState.yaw * ((float)Math.PI / 180);
        this.bullet.pitch = shulkerBulletEntityRenderState.pitch * ((float)Math.PI / 180);
    }
}

