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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel
extends EntityModel<WitherEntityRenderState> {
    final static private String RIBCAGE = "ribcage";
    final static private String CENTER_HEAD = "center_head";
    final static private String RIGHT_HEAD = "right_head";
    final static private String LEFT_HEAD = "left_head";
    final static private float RIBCAGE_PITCH_OFFSET = 0.065f;
    final static private float TAIL_PITCH_OFFSET = 0.265f;
    final private ModelPart centerHead;
    final private ModelPart rightHead;
    final private ModelPart leftHead;
    final private ModelPart ribcage;
    final private ModelPart tail;

    public WitherEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.ribcage = modelPart.getChild(RIBCAGE);
        this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
        this.centerHead = modelPart.getChild(CENTER_HEAD);
        this.rightHead = modelPart.getChild(RIGHT_HEAD);
        this.leftHead = modelPart.getChild(LEFT_HEAD);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shoulders", ModelPartBuilder.create().uv(0, 16).cuboid(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, dilation), ModelTransform.NONE);
        float f = 0.20420352f;
        modelPartData.addChild(RIBCAGE, ModelPartBuilder.create().uv(0, 22).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, dilation).uv(24, 22).cuboid(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, dilation).uv(24, 22).cuboid(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, dilation).uv(24, 22).cuboid(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, dilation), ModelTransform.of(-2.0f, 6.9f, -0.5f, 0.20420352f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(12, 22).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, dilation), ModelTransform.of(-2.0f, 6.9f + MathHelper.cos(0.20420352f) * 10.0f, -0.5f + MathHelper.sin(0.20420352f) * 10.0f, 0.83252203f, 0.0f, 0.0f));
        modelPartData.addChild(CENTER_HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, dilation);
        modelPartData.addChild(RIGHT_HEAD, modelPartBuilder, ModelTransform.origin(-8.0f, 4.0f, 0.0f));
        modelPartData.addChild(LEFT_HEAD, modelPartBuilder, ModelTransform.origin(10.0f, 4.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(WitherEntityRenderState witherEntityRenderState) {
        super.setAngles(witherEntityRenderState);
        WitherEntityModel.rotateHead(witherEntityRenderState, this.rightHead, 0);
        WitherEntityModel.rotateHead(witherEntityRenderState, this.leftHead, 1);
        float f = MathHelper.cos(witherEntityRenderState.age * 0.1f);
        this.ribcage.pitch = (0.065f + 0.05f * f) * (float)Math.PI;
        this.tail.setOrigin(-2.0f, 6.9f + MathHelper.cos(this.ribcage.pitch) * 10.0f, -0.5f + MathHelper.sin(this.ribcage.pitch) * 10.0f);
        this.tail.pitch = (0.265f + 0.1f * f) * (float)Math.PI;
        this.centerHead.yaw = witherEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.centerHead.pitch = witherEntityRenderState.pitch * ((float)Math.PI / 180);
    }

    private static void rotateHead(WitherEntityRenderState witherEntityRenderState, ModelPart head, int sigma) {
        head.yaw = (witherEntityRenderState.sideHeadYaws[sigma] - witherEntityRenderState.bodyYaw) * ((float)Math.PI / 180);
        head.pitch = witherEntityRenderState.sideHeadPitches[sigma] * ((float)Math.PI / 180);
    }
}

