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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.PiglinBaseEntityModel;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel
extends PiglinBaseEntityModel<PiglinEntityRenderState> {
    public PiglinEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(PiglinEntityRenderState piglinEntityRenderState) {
        super.setAngles(piglinEntityRenderState);
        float f = 0.5235988f;
        float g = piglinEntityRenderState.handSwingProgress;
        PiglinActivity piglinActivity = piglinEntityRenderState.activity;
        if (piglinActivity == PiglinActivity.DANCING) {
            float h = piglinEntityRenderState.age / 60.0f;
            this.rightEar.roll = 0.5235988f + (float)Math.PI / 180 * MathHelper.sin(h * 30.0f) * 10.0f;
            this.leftEar.roll = -0.5235988f - (float)Math.PI / 180 * MathHelper.cos(h * 30.0f) * 10.0f;
            this.head.originX += MathHelper.sin(h * 10.0f);
            this.head.originY += MathHelper.sin(h * 40.0f) + 0.4f;
            this.rightArm.roll = (float)Math.PI / 180 * (70.0f + MathHelper.cos(h * 40.0f) * 10.0f);
            this.leftArm.roll = this.rightArm.roll * -1.0f;
            this.rightArm.originY += MathHelper.sin(h * 40.0f) * 0.5f - 0.5f;
            this.leftArm.originY += MathHelper.sin(h * 40.0f) * 0.5f + 0.5f;
            this.body.originY += MathHelper.sin(h * 40.0f) * 0.35f;
        } else if (piglinActivity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON && g == 0.0f) {
            this.rotateMainArm(piglinEntityRenderState);
        } else if (piglinActivity == PiglinActivity.CROSSBOW_HOLD) {
            ArmPosing.hold(this.rightArm, this.leftArm, this.head, piglinEntityRenderState.mainArm == Arm.RIGHT);
        } else if (piglinActivity == PiglinActivity.CROSSBOW_CHARGE) {
            ArmPosing.charge(this.rightArm, this.leftArm, piglinEntityRenderState.piglinCrossbowPullTime, piglinEntityRenderState.itemUseTime, piglinEntityRenderState.mainArm == Arm.RIGHT);
        } else if (piglinActivity == PiglinActivity.ADMIRING_ITEM) {
            this.head.pitch = 0.5f;
            this.head.yaw = 0.0f;
            if (piglinEntityRenderState.mainArm == Arm.LEFT) {
                this.rightArm.yaw = -0.5f;
                this.rightArm.pitch = -0.9f;
            } else {
                this.leftArm.yaw = 0.5f;
                this.leftArm.pitch = -0.9f;
            }
        }
    }

    @Override
    protected void animateArms(PiglinEntityRenderState piglinEntityRenderState, float f) {
        float g = piglinEntityRenderState.handSwingProgress;
        if (g > 0.0f && piglinEntityRenderState.activity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON) {
            ArmPosing.meleeAttack(this.rightArm, this.leftArm, piglinEntityRenderState.mainArm, g, piglinEntityRenderState.age);
            return;
        }
        super.animateArms(piglinEntityRenderState, f);
    }

    private void rotateMainArm(PiglinEntityRenderState state) {
        if (state.mainArm == Arm.LEFT) {
            this.leftArm.pitch = -1.8f;
        } else {
            this.rightArm.pitch = -1.8f;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }
}

