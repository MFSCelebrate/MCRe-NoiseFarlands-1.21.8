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
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitchEntityModel
extends EntityModel<WitchEntityRenderState>
implements ModelWithHead,
ModelWithHat {
    final protected ModelPart nose;
    final private ModelPart head;
    final private ModelPart hat;
    final private ModelPart hatRim;
    final private ModelPart rightLeg;
    final private ModelPart leftLeg;
    final private ModelPart arms;

    public WitchEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.hat = this.head.getChild(EntityModelPartNames.HAT);
        this.hatRim = this.hat.getChild(EntityModelPartNames.HAT_RIM);
        this.nose = this.head.getChild(EntityModelPartNames.NOSE);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.arms = modelPart.getChild(EntityModelPartNames.ARMS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = VillagerResemblingModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(0, 64).cuboid(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f), ModelTransform.origin(-5.0f, -10.03125f, -5.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("hat2", ModelPartBuilder.create().uv(0, 76).cuboid(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f), ModelTransform.of(1.75f, -4.0f, 2.0f, -0.05235988f, 0.0f, 0.02617994f));
        ModelPartData modelPartData5 = modelPartData4.addChild("hat3", ModelPartBuilder.create().uv(0, 87).cuboid(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f), ModelTransform.of(1.75f, -4.0f, 2.0f, -0.10471976f, 0.0f, 0.05235988f));
        modelPartData5.addChild("hat4", ModelPartBuilder.create().uv(0, 95).cuboid(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, new Dilation(0.25f)), ModelTransform.of(1.75f, -2.0f, 2.0f, -0.20943952f, 0.0f, 0.10471976f));
        ModelPartData modelPartData6 = modelPartData2.getChild(EntityModelPartNames.NOSE);
        modelPartData6.addChild("mole", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 3.0f, -6.75f, 1.0f, 1.0f, 1.0f, new Dilation(-0.25f)), ModelTransform.origin(0.0f, -2.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 128);
    }

    @Override
    public void setAngles(WitchEntityRenderState witchEntityRenderState) {
        super.setAngles(witchEntityRenderState);
        this.head.yaw = witchEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = witchEntityRenderState.pitch * ((float)Math.PI / 180);
        this.rightLeg.pitch = MathHelper.cos(witchEntityRenderState.limbSwingAnimationProgress * 0.6662f) * 1.4f * witchEntityRenderState.limbSwingAmplitude * 0.5f;
        this.leftLeg.pitch = MathHelper.cos(witchEntityRenderState.limbSwingAnimationProgress * 0.6662f + (float)Math.PI) * 1.4f * witchEntityRenderState.limbSwingAmplitude * 0.5f;
        float f = 0.01f * (float)(witchEntityRenderState.id % 10);
        this.nose.pitch = MathHelper.sin(witchEntityRenderState.age * f) * 4.5f * ((float)Math.PI / 180);
        this.nose.roll = MathHelper.cos(witchEntityRenderState.age * f) * 2.5f * ((float)Math.PI / 180);
        if (witchEntityRenderState.holdingItem) {
            this.nose.setOrigin(0.0f, 1.0f, -1.5f);
            this.nose.pitch = -0.9f;
        }
    }

    public ModelPart getNose() {
        return this.nose;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setHatVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.hatRim.visible = visible;
    }

    @Override
    public void rotateArms(MatrixStack stack) {
        this.root.applyTransform(stack);
        this.arms.applyTransform(stack);
    }
}

