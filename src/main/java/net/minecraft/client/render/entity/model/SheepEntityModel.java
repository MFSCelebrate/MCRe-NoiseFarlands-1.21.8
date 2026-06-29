/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class SheepEntityModel
extends QuadrupedEntityModel<SheepEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(false, 8.0f, 4.0f, 2.0f, 2.0f, 24.0f, Set.of("head"));

    public SheepEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = QuadrupedEntityModel.getModelData(12, false, true, Dilation.NONE);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -4.0f, -6.0f, 6.0f, 6.0f, 8.0f), ModelTransform.origin(0.0f, 6.0f, -8.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(28, 8).cuboid(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(SheepEntityRenderState sheepEntityRenderState) {
        super.setAngles(sheepEntityRenderState);
        this.head.originY += sheepEntityRenderState.neckAngle * 9.0f * sheepEntityRenderState.ageScale;
        this.head.pitch = sheepEntityRenderState.headAngle;
    }
}

