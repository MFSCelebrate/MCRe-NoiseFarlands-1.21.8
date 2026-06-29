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
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityModel
extends EntityModel<IronGolemEntityRenderState> {
    final private ModelPart head;
    final private ModelPart rightArm;
    final private ModelPart leftArm;
    final private ModelPart rightLeg;
    final private ModelPart leftLeg;

    public IronGolemEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f).uv(24, 0).cuboid(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f), ModelTransform.origin(0.0f, -7.0f, -2.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 40).cuboid(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f).uv(0, 70).cuboid(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, new Dilation(0.5f)), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(60, 21).cuboid(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(60, 58).cuboid(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(37, 0).cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f), ModelTransform.origin(-4.0f, 11.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(60, 0).mirrored().cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f), ModelTransform.origin(5.0f, 11.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(IronGolemEntityRenderState ironGolemEntityRenderState) {
        super.setAngles(ironGolemEntityRenderState);
        float f = ironGolemEntityRenderState.attackTicksLeft;
        float g = ironGolemEntityRenderState.limbSwingAmplitude;
        float h = ironGolemEntityRenderState.limbSwingAnimationProgress;
        if (f > 0.0f) {
            this.rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(f, 10.0f);
            this.leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(f, 10.0f);
        } else {
            int i = ironGolemEntityRenderState.lookingAtVillagerTicks;
            if (i > 0) {
                this.rightArm.pitch = -0.8f + 0.025f * MathHelper.wrap(i, 70.0f);
                this.leftArm.pitch = 0.0f;
            } else {
                this.rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(h, 13.0f)) * g;
                this.leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(h, 13.0f)) * g;
            }
        }
        this.head.yaw = ironGolemEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = ironGolemEntityRenderState.pitch * ((float)Math.PI / 180);
        this.rightLeg.pitch = -1.5f * MathHelper.wrap(h, 13.0f) * g;
        this.leftLeg.pitch = 1.5f * MathHelper.wrap(h, 13.0f) * g;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
    }

    public ModelPart getRightArm() {
        return this.rightArm;
    }
}

