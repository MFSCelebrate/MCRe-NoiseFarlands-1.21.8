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
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityModel
extends EntityModel<DolphinEntityRenderState> {
    final static public ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    final private ModelPart body;
    final private ModelPart tail;
    final private ModelPart tailFin;

    public DolphinEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
        this.tailFin = this.tail.getChild(EntityModelPartNames.TAIL_FIN);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 18.0f;
        float g = -8.0f;
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(22, 0).cuboid(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f), ModelTransform.origin(0.0f, 22.0f, -5.0f));
        modelPartData2.addChild(EntityModelPartNames.BACK_FIN, ModelPartBuilder.create().uv(51, 0).cuboid(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f), ModelTransform.rotation(1.0471976f, 0.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_FIN, ModelPartBuilder.create().uv(48, 20).mirrored().cuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), ModelTransform.of(2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, 2.0943952f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_FIN, ModelPartBuilder.create().uv(48, 20).cuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), ModelTransform.of(-2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, -2.0943952f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(0, 19).cuboid(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f), ModelTransform.of(0.0f, -2.5f, 11.0f, -0.10471976f, 0.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.TAIL_FIN, ModelPartBuilder.create().uv(19, 20).cuboid(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f), ModelTransform.origin(0.0f, 0.0f, 9.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f), ModelTransform.origin(0.0f, -4.0f, -3.0f));
        modelPartData4.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(0, 13).cuboid(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(DolphinEntityRenderState dolphinEntityRenderState) {
        super.setAngles(dolphinEntityRenderState);
        this.body.pitch = dolphinEntityRenderState.pitch * ((float)Math.PI / 180);
        this.body.yaw = dolphinEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        if (dolphinEntityRenderState.moving) {
            this.body.pitch += -0.05f - 0.05f * MathHelper.cos(dolphinEntityRenderState.age * 0.3f);
            this.tail.pitch = -0.1f * MathHelper.cos(dolphinEntityRenderState.age * 0.3f);
            this.tailFin.pitch = -0.2f * MathHelper.cos(dolphinEntityRenderState.age * 0.3f);
        }
    }
}

