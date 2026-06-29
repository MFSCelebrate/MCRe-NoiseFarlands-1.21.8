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
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.BoggedEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BoggedEntityModel
extends SkeletonEntityModel<BoggedEntityRenderState> {
    final private ModelPart mushrooms;

    public BoggedEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.mushrooms = modelPart.getChild(EntityModelPartNames.HEAD).getChild(EntityModelPartNames.MUSHROOMS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        SkeletonEntityModel.addLimbs(modelPartData);
        ModelPartData modelPartData2 = modelPartData.getChild(EntityModelPartNames.HEAD).addChild(EntityModelPartNames.MUSHROOMS, ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData2.addChild("red_mushroom_1", ModelPartBuilder.create().uv(50, 16).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of(3.0f, -8.0f, 3.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData2.addChild("red_mushroom_2", ModelPartBuilder.create().uv(50, 16).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of(3.0f, -8.0f, 3.0f, 0.0f, 2.3561945f, 0.0f));
        modelPartData2.addChild("brown_mushroom_1", ModelPartBuilder.create().uv(50, 22).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of(-3.0f, -8.0f, -3.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData2.addChild("brown_mushroom_2", ModelPartBuilder.create().uv(50, 22).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of(-3.0f, -8.0f, -3.0f, 0.0f, 2.3561945f, 0.0f));
        modelPartData2.addChild("brown_mushroom_3", ModelPartBuilder.create().uv(50, 28).cuboid(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of(-2.0f, -1.0f, 4.0f, -1.5707964f, 0.0f, 0.7853982f));
        modelPartData2.addChild("brown_mushroom_4", ModelPartBuilder.create().uv(50, 28).cuboid(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of(-2.0f, -1.0f, 4.0f, -1.5707964f, 0.0f, 2.3561945f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(BoggedEntityRenderState boggedEntityRenderState) {
        super.setAngles(boggedEntityRenderState);
        this.mushrooms.visible = !boggedEntityRenderState.sheared;
    }
}

