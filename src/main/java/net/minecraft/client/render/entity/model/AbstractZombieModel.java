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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractZombieModel<S extends ZombieEntityRenderState>
extends BipedEntityModel<S> {
    protected AbstractZombieModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(S zombieEntityRenderState) {
        super.setAngles(zombieEntityRenderState);
        float f = ((ZombieEntityRenderState)zombieEntityRenderState).handSwingProgress;
        ArmPosing.zombieArms(this.leftArm, this.rightArm, ((ZombieEntityRenderState)zombieEntityRenderState).attacking, f, ((ZombieEntityRenderState)zombieEntityRenderState).age);
    }
}

