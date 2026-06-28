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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TadpoleEntityModel
extends EntityModel<LivingEntityRenderState> {
    final private ModelPart tail;

    public TadpoleEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayer::getEntityCutoutNoCull);
        this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.0f;
        float g = 22.0f;
        float h = -3.0f;
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 3.0f), ModelTransform.origin(0.0f, 22.0f, -3.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -1.0f, 0.0f, 0.0f, 2.0f, 7.0f), ModelTransform.origin(0.0f, 22.0f, 0.0f));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        super.setAngles(livingEntityRenderState);
        float f = livingEntityRenderState.touchingWater ? 1.0f : 1.5f;
        this.tail.yaw = -f * 0.25f * MathHelper.sin(0.3f * livingEntityRenderState.age);
    }
}

