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
import net.minecraft.client.render.entity.state.EvokerFangsEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityModel
extends EntityModel<EvokerFangsEntityRenderState> {
    final static private String BASE = "base";
    final static private String UPPER_JAW = "upper_jaw";
    final static private String LOWER_JAW = "lower_jaw";
    final private ModelPart base;
    final private ModelPart upperJaw;
    final private ModelPart lowerJaw;

    public EvokerFangsEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.base = modelPart.getChild(BASE);
        this.upperJaw = this.base.getChild(UPPER_JAW);
        this.lowerJaw = this.base.getChild(LOWER_JAW);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BASE, ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f), ModelTransform.origin(-5.0f, 24.0f, -5.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(40, 0).cuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        modelPartData2.addChild(UPPER_JAW, modelPartBuilder, ModelTransform.of(6.5f, 0.0f, 1.0f, 0.0f, 0.0f, 2.042035f));
        modelPartData2.addChild(LOWER_JAW, modelPartBuilder, ModelTransform.of(3.5f, 0.0f, 9.0f, 0.0f, (float)Math.PI, 4.2411504f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(EvokerFangsEntityRenderState evokerFangsEntityRenderState) {
        super.setAngles(evokerFangsEntityRenderState);
        float f = evokerFangsEntityRenderState.animationProgress;
        float g = Math.min(f * 2.0f, 1.0f);
        g = 1.0f - g * g * g;
        this.upperJaw.roll = (float)Math.PI - g * 0.35f * (float)Math.PI;
        this.lowerJaw.roll = (float)Math.PI + g * 0.35f * (float)Math.PI;
        this.base.originY -= (f + MathHelper.sin(f * 2.7f)) * 7.2f;
        float h = 1.0f;
        if (f > 0.9f) {
            h *= (1.0f - f) / 0.1f;
        }
        this.root.originY = 24.0f - 20.0f * h;
        this.root.xScale = h;
        this.root.yScale = h;
        this.root.zScale = h;
    }
}

