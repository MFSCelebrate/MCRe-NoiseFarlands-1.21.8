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
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractHorseEntityModel<T extends LivingHorseEntityRenderState>
extends EntityModel<T> {
    final static private float EATING_GRASS_ANIMATION_HEAD_BASE_PITCH = 2.1816616f;
    final static private float ANGRY_ANIMATION_FRONT_LEG_PITCH_MULTIPLIER = 1.0471976f;
    final static private float ANGRY_ANIMATION_BODY_PITCH_MULTIPLIER = 0.7853982f;
    final static private float HEAD_TAIL_BASE_PITCH = 0.5235988f;
    final static private float ANGRY_ANIMATION_HIND_LEG_PITCH_MULTIPLIER = 0.2617994f;
    final static protected String HEAD_PARTS = "head_parts";
    final static protected ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f, Set.of("head_parts"));
    final protected ModelPart body;
    final protected ModelPart head;
    final private ModelPart rightHindLeg;
    final private ModelPart leftHindLeg;
    final private ModelPart rightFrontLeg;
    final private ModelPart leftFrontLeg;
    final private ModelPart tail;

    public AbstractHorseEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.head = modelPart.getChild(HEAD_PARTS);
        this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 32).cuboid(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, new Dilation(0.05f)), ModelTransform.origin(0.0f, 11.0f, 5.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(HEAD_PARTS, ModelPartBuilder.create().uv(0, 35).cuboid(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f), ModelTransform.of(0.0f, 4.0f, -12.0f, 0.5235988f, 0.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 13).cuboid(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, dilation), ModelTransform.NONE);
        modelPartData3.addChild(EntityModelPartNames.MANE, ModelPartBuilder.create().uv(56, 36).cuboid(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, dilation), ModelTransform.NONE);
        modelPartData3.addChild(EntityModelPartNames.UPPER_MOUTH, ModelPartBuilder.create().uv(0, 25).cuboid(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, dilation), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.origin(4.0f, 14.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.origin(-4.0f, 14.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.origin(4.0f, 14.0f, -10.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.origin(-4.0f, 14.0f, -10.0f));
        modelPartData2.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(42, 36).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, dilation), ModelTransform.of(0.0f, -5.0f, 2.0f, 0.5235988f, 0.0f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(19, 16).cuboid(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new Dilation(-0.001f)), ModelTransform.NONE);
        modelPartData4.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(19, 16).cuboid(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new Dilation(-0.001f)), ModelTransform.NONE);
        return modelData;
    }

    public static ModelData getBabyHorseModelData(Dilation dilation) {
        return BABY_TRANSFORMER.apply(AbstractHorseEntityModel.getBabyModelData(dilation));
    }

    protected static ModelData getBabyModelData(Dilation dilation) {
        ModelData modelData = AbstractHorseEntityModel.getModelData(dilation);
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation2 = dilation.add(0.0f, 5.5f, 0.0f);
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.origin(4.0f, 14.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.origin(-4.0f, 14.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.origin(4.0f, 14.0f, -10.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.origin(-4.0f, 14.0f, -10.0f));
        return modelData;
    }

    @Override
    public void setAngles(T livingHorseEntityRenderState) {
        super.setAngles(livingHorseEntityRenderState);
        float f = MathHelper.clamp(((LivingHorseEntityRenderState)livingHorseEntityRenderState).relativeHeadYaw, -20.0f, 20.0f);
        float g = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).pitch * ((float)Math.PI / 180);
        float h = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).limbSwingAmplitude;
        float i = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).limbSwingAnimationProgress;
        if (h > 0.2f) {
            g += MathHelper.cos(i * 0.8f) * 0.15f * h;
        }
        float j = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).eatingGrassAnimationProgress;
        float k = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).angryAnimationProgress;
        float l = 1.0f - k;
        float m = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).eatingAnimationProgress;
        boolean bl = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).waggingTail;
        this.head.pitch = 0.5235988f + g;
        this.head.yaw = f * ((float)Math.PI / 180);
        float n = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).touchingWater ? 0.2f : 1.0f;
        float o = MathHelper.cos(n * i * 0.6662f + (float)Math.PI);
        float p = o * 0.8f * h;
        float q = (1.0f - Math.max(k, j)) * (0.5235988f + g + m * MathHelper.sin(((LivingHorseEntityRenderState)livingHorseEntityRenderState).age) * 0.05f);
        this.head.pitch = k * (0.2617994f + g) + j * (2.1816616f + MathHelper.sin(((LivingHorseEntityRenderState)livingHorseEntityRenderState).age) * 0.05f) + q;
        this.head.yaw = k * f * ((float)Math.PI / 180) + (1.0f - Math.max(k, j)) * this.head.yaw;
        float r = ((LivingHorseEntityRenderState)livingHorseEntityRenderState).ageScale;
        this.head.originY += MathHelper.lerp(j, MathHelper.lerp(k, 0.0f, -8.0f * r), 7.0f * r);
        this.head.originZ = MathHelper.lerp(k, this.head.originZ, -4.0f * r);
        this.body.pitch = k * -0.7853982f + l * this.body.pitch;
        float s = 0.2617994f * k;
        float t = MathHelper.cos(((LivingHorseEntityRenderState)livingHorseEntityRenderState).age * 0.6f + (float)Math.PI);
        this.leftFrontLeg.originY -= 12.0f * r * k;
        this.leftFrontLeg.originZ += 4.0f * r * k;
        this.rightFrontLeg.originY = this.leftFrontLeg.originY;
        this.rightFrontLeg.originZ = this.leftFrontLeg.originZ;
        float u = (-1.0471976f + t) * k + p * l;
        float v = (-1.0471976f - t) * k - p * l;
        this.leftHindLeg.pitch = s - o * 0.5f * h * l;
        this.rightHindLeg.pitch = s + o * 0.5f * h * l;
        this.leftFrontLeg.pitch = u;
        this.rightFrontLeg.pitch = v;
        this.tail.pitch = 0.5235988f + h * 0.75f;
        this.tail.originY += h * r;
        this.tail.originZ += h * 2.0f * r;
        this.tail.yaw = bl ? MathHelper.cos(((LivingHorseEntityRenderState)livingHorseEntityRenderState).age * 0.7f) : 0.0f;
    }
}

