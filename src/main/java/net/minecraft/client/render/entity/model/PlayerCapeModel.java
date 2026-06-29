/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
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
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class PlayerCapeModel<T extends PlayerEntityRenderState>
extends BipedEntityModel<T> {
    final static private String CAPE = "cape";
    final private ModelPart cape;

    public PlayerCapeModel(ModelPart modelPart) {
        super(modelPart);
        this.cape = this.body.getChild(CAPE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD);
        modelPartData2.addChild(EntityModelPartNames.HAT);
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.BODY);
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM);
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM);
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG);
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG);
        modelPartData3.addChild(CAPE, ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, Dilation.NONE, 1.0f, 0.5f), ModelTransform.of(0.0f, 0.0f, 2.0f, 0.0f, (float)Math.PI, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T playerEntityRenderState) {
        super.setAngles(playerEntityRenderState);
        this.cape.rotate(new Quaternionf().rotateY((float)(-Math.PI)).rotateX((6.0f + ((PlayerEntityRenderState)playerEntityRenderState).field_53537 / 2.0f + ((PlayerEntityRenderState)playerEntityRenderState).field_53536) * ((float)Math.PI / 180)).rotateZ(((PlayerEntityRenderState)playerEntityRenderState).field_53538 / 2.0f * ((float)Math.PI / 180)).rotateY((180.0f - ((PlayerEntityRenderState)playerEntityRenderState).field_53538 / 2.0f) * ((float)Math.PI / 180)));
    }
}

