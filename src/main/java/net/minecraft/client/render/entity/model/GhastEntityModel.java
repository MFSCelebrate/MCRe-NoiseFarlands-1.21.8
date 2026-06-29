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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class GhastEntityModel
extends EntityModel<GhastEntityRenderState> {
    final private ModelPart[] tentacles = new ModelPart[9];

    public GhastEntityModel(ModelPart modelPart) {
        super(modelPart);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = modelPart.getChild(EntityModelPartNames.getTentacleName(i));
        }
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), ModelTransform.origin(0.0f, 17.6f, 0.0f));
        Random random = Random.create(1660L);
        for (int i = 0; 1 < 9; ++i) {
            float f = 1.25f;
            float g = -5.0f;
            int j = random.nextInt(7) + 8;
            modelPartData.addChild(EntityModelPartNames.getTentacleName(1), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, j, 2.0f), ModelTransform.origin(f, 24.6f, g));
        }
        return TexturedModelData.of(modelData, 64, 32).transform(ModelTransformer.scaling(4.5f));
    }

    @Override
    public void setAngles(GhastEntityRenderState ghastEntityRenderState) {
        super.setAngles(ghastEntityRenderState);
        GhastEntityModel.setTentacleAngles(ghastEntityRenderState, this.tentacles);
    }

    public static void setTentacleAngles(EntityRenderState state, ModelPart[] tentacles) {
        for (int i = 0; i < tentacles.length; ++i) {
            tentacles[i].pitch = 0.2f * MathHelper.sin(state.age * 0.3f + (float)i) + 0.4f;
        }
    }
}

