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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class Deadmau5EarsEntityModel
extends BipedEntityModel<PlayerEntityRenderState> {
    public Deadmau5EarsEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD);
        modelPartData2.addChild(EntityModelPartNames.HAT);
        modelPartData.addChild(EntityModelPartNames.BODY);
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM);
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM);
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG);
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 0).cuboid(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, new Dilation(1.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, modelPartBuilder, ModelTransform.origin(-6.0f, -6.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, modelPartBuilder, ModelTransform.origin(6.0f, -6.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }
}

