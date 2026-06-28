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
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel
extends EntityModel<ChickenEntityRenderState> {
    final static public String RED_THING = "red_thing";
    final static public float field_56579 = 16.0f;
    final static public ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(false, 5.0f, 2.0f, 2.0f, 1.99f, 24.0f, Set.of("head", "beak", "red_thing"));
    final private ModelPart head;
    final private ModelPart rightLeg;
    final private ModelPart leftLeg;
    final private ModelPart rightWing;
    final private ModelPart leftWing;

    public ChickenEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightWing = modelPart.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = modelPart.getChild(EntityModelPartNames.LEFT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ChickenEntityModel.getModelData();
        return TexturedModelData.of(modelData, 64, 32);
    }

    protected static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), ModelTransform.origin(0.0f, 15.0f, -4.0f));
        modelPartData2.addChild(EntityModelPartNames.BEAK, ModelPartBuilder.create().uv(14, 0).cuboid(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData2.addChild(RED_THING, ModelPartBuilder.create().uv(14, 4).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 9).cuboid(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 0).cuboid(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, modelPartBuilder, ModelTransform.origin(-2.0f, 19.0f, 1.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, modelPartBuilder, ModelTransform.origin(1.0f, 19.0f, 1.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(24, 13).cuboid(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.origin(-4.0f, 13.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(24, 13).cuboid(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.origin(4.0f, 13.0f, 0.0f));
        return modelData;
    }

    @Override
    public void setAngles(ChickenEntityRenderState chickenEntityRenderState) {
        super.setAngles(chickenEntityRenderState);
        float f = (MathHelper.sin(chickenEntityRenderState.flapProgress) + 1.0f) * chickenEntityRenderState.maxWingDeviation;
        this.head.pitch = chickenEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = chickenEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float g = chickenEntityRenderState.limbSwingAmplitude;
        float h = chickenEntityRenderState.limbSwingAnimationProgress;
        this.rightLeg.pitch = MathHelper.cos(h * 0.6662f) * 1.4f * g;
        this.leftLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightWing.roll = f;
        this.leftWing.roll = -f;
    }
}

