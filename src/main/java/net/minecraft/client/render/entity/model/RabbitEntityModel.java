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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityModel
extends EntityModel<RabbitEntityRenderState> {
    final static private float HAUNCH_JUMP_PITCH_MULTIPLIER = 50.0f;
    final static private float FRONT_LEGS_JUMP_PITCH_MULTIPLIER = -40.0f;
    final static private float SCALE = 0.6f;
    final static private ModelTransformer ADULT_TRANSFORMER = ModelTransformer.scaling(0.6f);
    final static private ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 22.0f, 2.0f, 2.65f, 2.5f, 36.0f, Set.of("head", "left_ear", "right_ear", "nose"));
    final static private String LEFT_HAUNCH = "left_haunch";
    final static private String RIGHT_HAUNCH = "right_haunch";
    final private ModelPart leftHaunch;
    final private ModelPart rightHaunch;
    final private ModelPart leftFrontLeg;
    final private ModelPart rightFrontLeg;
    final private ModelPart head;

    public RabbitEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftHaunch = modelPart.getChild(LEFT_HAUNCH);
        this.rightHaunch = modelPart.getChild(RIGHT_HAUNCH);
        this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
    }

    public static TexturedModelData getTexturedModelData(boolean baby) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(LEFT_HAUNCH, ModelPartBuilder.create().uv(30, 15).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), ModelTransform.of(3.0f, 17.5f, 3.7f, -0.36651915f, 0.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(RIGHT_HAUNCH, ModelPartBuilder.create().uv(16, 15).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), ModelTransform.of(-3.0f, 17.5f, 3.7f, -0.36651915f, 0.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_HIND_FOOT, ModelPartBuilder.create().uv(26, 24).cuboid(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), ModelTransform.rotation(0.36651915f, 0.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_HIND_FOOT, ModelPartBuilder.create().uv(8, 24).cuboid(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), ModelTransform.rotation(0.36651915f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f), ModelTransform.of(0.0f, 19.0f, 8.0f, -0.34906584f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().uv(8, 15).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.of(3.0f, 17.0f, -1.0f, -0.19198622f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().uv(0, 15).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.of(-3.0f, 17.0f, -1.0f, -0.19198622f, 0.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(32, 0).cuboid(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f), ModelTransform.origin(0.0f, 16.0f, -1.0f));
        modelPartData4.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(52, 0).cuboid(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, -0.2617994f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(58, 0).cuboid(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.2617994f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(52, 6).cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f), ModelTransform.of(0.0f, 20.0f, 7.0f, -0.3490659f, 0.0f, 0.0f));
        modelPartData4.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(32, 9).cuboid(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32).transform(baby ? BABY_TRANSFORMER : ADULT_TRANSFORMER);
    }

    @Override
    public void setAngles(RabbitEntityRenderState rabbitEntityRenderState) {
        super.setAngles(rabbitEntityRenderState);
        this.head.pitch = rabbitEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = rabbitEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float f = MathHelper.sin(rabbitEntityRenderState.jumpProgress * (float)Math.PI);
        this.leftHaunch.pitch += f * 50.0f * ((float)Math.PI / 180);
        this.rightHaunch.pitch += f * 50.0f * ((float)Math.PI / 180);
        this.leftFrontLeg.pitch += f * -40.0f * ((float)Math.PI / 180);
        this.rightFrontLeg.pitch += f * -40.0f * ((float)Math.PI / 180);
    }
}

