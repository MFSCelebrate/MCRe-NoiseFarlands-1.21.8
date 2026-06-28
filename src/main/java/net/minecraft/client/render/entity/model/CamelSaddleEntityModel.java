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
import net.minecraft.client.render.entity.model.CamelEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class CamelSaddleEntityModel
extends CamelEntityModel {
    final static private String SADDLE = "saddle";
    final static private String BRIDLE = "bridle";
    final static private String REINS = "reins";
    final private ModelPart reins;

    public CamelSaddleEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.reins = this.head.getChild(REINS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = CamelSaddleEntityModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.getChild(EntityModelPartNames.BODY);
        ModelPartData modelPartData3 = modelPartData2.getChild(EntityModelPartNames.HEAD);
        Dilation dilation = new Dilation(0.05f);
        modelPartData2.addChild(SADDLE, ModelPartBuilder.create().uv(74, 64).cuboid(-4.5f, -17.0f, -15.5f, 9.0f, 5.0f, 11.0f, dilation).uv(92, 114).cuboid(-3.5f, -20.0f, -15.5f, 7.0f, 3.0f, 11.0f, dilation).uv(0, 89).cuboid(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f, dilation), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        modelPartData3.addChild(REINS, ModelPartBuilder.create().uv(98, 42).cuboid(3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f).uv(84, 57).cuboid(-3.5f, -18.0f, -2.0f, 7.0f, 7.0f, 0.0f).uv(98, 42).cuboid(-3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        modelPartData3.addChild(BRIDLE, ModelPartBuilder.create().uv(60, 87).cuboid(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f, dilation).uv(21, 64).cuboid(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f, dilation).uv(50, 64).cuboid(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f, dilation).uv(74, 70).cuboid(2.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f).uv(74, 70).mirrored().cuboid(-3.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(CamelEntityRenderState camelEntityRenderState) {
        super.setAngles(camelEntityRenderState);
        this.reins.visible = camelEntityRenderState.hasPassengers;
    }
}

