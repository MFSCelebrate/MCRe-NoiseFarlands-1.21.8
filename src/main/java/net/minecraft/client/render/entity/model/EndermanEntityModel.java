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
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityModel<T extends EndermanEntityRenderState>
extends BipedEntityModel<T> {
    public EndermanEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        float f = -14.0f;
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, -14.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.origin(0.0f, -13.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(0, 16).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(-0.5f)), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(32, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f), ModelTransform.origin(0.0f, -14.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(56, 0).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.origin(-5.0f, -12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.origin(5.0f, -12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(56, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.origin(-2.0f, -5.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.origin(2.0f, -5.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(T endermanEntityRenderState) {
        super.setAngles(endermanEntityRenderState);
        this.head.visible = true;
        this.rightArm.pitch *= 0.5f;
        this.leftArm.pitch *= 0.5f;
        this.rightLeg.pitch *= 0.5f;
        this.leftLeg.pitch *= 0.5f;
        float f = 0.4f;
        this.rightArm.pitch = MathHelper.clamp(this.rightArm.pitch, -0.4f, 0.4f);
        this.leftArm.pitch = MathHelper.clamp(this.leftArm.pitch, -0.4f, 0.4f);
        this.rightLeg.pitch = MathHelper.clamp(this.rightLeg.pitch, -0.4f, 0.4f);
        this.leftLeg.pitch = MathHelper.clamp(this.leftLeg.pitch, -0.4f, 0.4f);
        if (((EndermanEntityRenderState)endermanEntityRenderState).carriedBlock != null) {
            this.rightArm.pitch = -0.5f;
            this.leftArm.pitch = -0.5f;
            this.rightArm.roll = 0.05f;
            this.leftArm.roll = -0.05f;
        }
        if (((EndermanEntityRenderState)endermanEntityRenderState).angry) {
            float g = 5.0f;
            this.head.originY -= 5.0f;
            this.hat.originY += 5.0f;
        }
    }
}

