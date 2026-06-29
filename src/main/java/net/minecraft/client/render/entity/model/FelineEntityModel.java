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
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FelineEntityModel<T extends FelineEntityRenderState>
extends EntityModel<T> {
    final static public ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 10.0f, 4.0f, Set.of("head"));
    final static private float field_32527 = 0.0f;
    final static private float BODY_SIZE_Y = 16.0f;
    final static private float field_32529 = -9.0f;
    final static protected float HIND_LEG_PIVOT_Y = 18.0f;
    final static protected float HIND_LEG_PIVOT_Z = 5.0f;
    final static protected float FRONT_LEG_PIVOT_Y = 14.1f;
    final static private float FRONT_LEG_PIVOT_Z = -5.0f;
    final static private String TAIL1 = "tail1";
    final static private String TAIL2 = "tail2";
    final protected ModelPart leftHindLeg;
    final protected ModelPart rightHindLeg;
    final protected ModelPart leftFrontLeg;
    final protected ModelPart rightFrontLeg;
    final protected ModelPart upperTail;
    final protected ModelPart lowerTail;
    final protected ModelPart head;
    final protected ModelPart body;

    public FelineEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.upperTail = modelPart.getChild(TAIL1);
        this.lowerTail = modelPart.getChild(TAIL2);
        this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation2 = new Dilation(-0.02f);
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().cuboid("main", -2.5f, -2.0f, -3.0f, 5.0f, 4.0f, 5.0f, dilation).cuboid(EntityModelPartNames.NOSE, -1.5f, -0.001f, -4.0f, 3, 2, 2, dilation, 0, 24).cuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 0, 10).cuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 6, 10), ModelTransform.origin(0.0f, 15.0f, -9.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(20, 0).cuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, dilation), ModelTransform.of(0.0f, 12.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild(TAIL1, ModelPartBuilder.create().uv(0, 15).cuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, dilation), ModelTransform.of(0.0f, 15.0f, 8.0f, 0.9f, 0.0f, 0.0f));
        modelPartData.addChild(TAIL2, ModelPartBuilder.create().uv(4, 15).cuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, dilation2), ModelTransform.origin(0.0f, 20.0f, 14.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.origin(1.1f, 18.0f, 5.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.origin(-1.1f, 18.0f, 5.0f));
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.origin(1.2f, 14.1f, -5.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.origin(-1.2f, 14.1f, -5.0f));
        return modelData;
    }

    @Override
    public void setAngles(T felineEntityRenderState) {
        super.setAngles(felineEntityRenderState);
        float f = ((FelineEntityRenderState)felineEntityRenderState).ageScale;
        if (((FelineEntityRenderState)felineEntityRenderState).inSneakingPose) {
            this.body.originY += 1.0f * f;
            this.head.originY += 2.0f * f;
            this.upperTail.originY += 1.0f * f;
            this.lowerTail.originY += -4.0f * f;
            this.lowerTail.originZ += 2.0f * f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
        } else if (((FelineEntityRenderState)felineEntityRenderState).sprinting) {
            this.lowerTail.originY = this.upperTail.originY;
            this.lowerTail.originZ += 2.0f * f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
        }
        this.head.pitch = ((FelineEntityRenderState)felineEntityRenderState).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((FelineEntityRenderState)felineEntityRenderState).relativeHeadYaw * ((float)Math.PI / 180);
        if (!((FelineEntityRenderState)felineEntityRenderState).inSittingPose) {
            this.body.pitch = 1.5707964f;
            float g = ((FelineEntityRenderState)felineEntityRenderState).limbSwingAmplitude;
            float h = ((FelineEntityRenderState)felineEntityRenderState).limbSwingAnimationProgress;
            if (((FelineEntityRenderState)felineEntityRenderState).sprinting) {
                this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662f) * g;
                this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662f + 0.3f) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI + 0.3f) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * g;
                this.lowerTail.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(h) * g;
            } else {
                this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662f) * g;
                this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662f) * g;
                this.lowerTail.pitch = !((FelineEntityRenderState)felineEntityRenderState).inSneakingPose ? 1.7278761f + 0.7853982f * MathHelper.cos(h) * g : 1.7278761f + 0.47123894f * MathHelper.cos(h) * g;
            }
        }
        if (((FelineEntityRenderState)felineEntityRenderState).inSittingPose) {
            this.body.pitch = 0.7853982f;
            this.body.originY += -4.0f * f;
            this.body.originZ += 5.0f * f;
            this.head.originY += -3.3f * f;
            this.head.originZ += 1.0f * f;
            this.upperTail.originY += 8.0f * f;
            this.upperTail.originZ += -2.0f * f;
            this.lowerTail.originY += 2.0f * f;
            this.lowerTail.originZ += -0.8f * f;
            this.upperTail.pitch = 1.7278761f;
            this.lowerTail.pitch = 2.670354f;
            this.leftFrontLeg.pitch = -0.15707964f;
            this.leftFrontLeg.originY += 2.0f * f;
            this.leftFrontLeg.originZ -= 2.0f * f;
            this.rightFrontLeg.pitch = -0.15707964f;
            this.rightFrontLeg.originY += 2.0f * f;
            this.rightFrontLeg.originZ -= 2.0f * f;
            this.leftHindLeg.pitch = -1.5707964f;
            this.leftHindLeg.originY += 3.0f * f;
            this.leftHindLeg.originZ -= 4.0f * f;
            this.rightHindLeg.pitch = -1.5707964f;
            this.rightHindLeg.originY += 3.0f * f;
            this.rightHindLeg.originZ -= 4.0f * f;
        }
        if (((FelineEntityRenderState)felineEntityRenderState).sleepAnimationProgress > 0.0f) {
            this.head.roll = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)felineEntityRenderState).sleepAnimationProgress, this.head.roll, -1.2707963f);
            this.head.yaw = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)felineEntityRenderState).sleepAnimationProgress, this.head.yaw, 1.2707963f);
            this.leftFrontLeg.pitch = -1.2707963f;
            this.rightFrontLeg.pitch = -0.47079635f;
            this.rightFrontLeg.roll = -0.2f;
            this.rightFrontLeg.originX += f;
            this.leftHindLeg.pitch = -0.4f;
            this.rightHindLeg.pitch = 0.5f;
            this.rightHindLeg.roll = -0.5f;
            this.rightHindLeg.originX += 0.8f * f;
            this.rightHindLeg.originY += 2.0f * f;
            this.upperTail.pitch = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)felineEntityRenderState).tailCurlAnimationProgress, this.upperTail.pitch, 0.8f);
            this.lowerTail.pitch = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)felineEntityRenderState).tailCurlAnimationProgress, this.lowerTail.pitch, -0.4f);
        }
        if (((FelineEntityRenderState)felineEntityRenderState).headDownAnimationProgress > 0.0f) {
            this.head.pitch = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)felineEntityRenderState).headDownAnimationProgress, this.head.pitch, -0.58177644f);
        }
    }
}

