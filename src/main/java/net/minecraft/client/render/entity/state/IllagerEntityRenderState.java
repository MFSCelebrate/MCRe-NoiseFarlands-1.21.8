/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class IllagerEntityRenderState
extends ArmedEntityRenderState {
    public boolean hasVehicle;
    public boolean attacking;
    public Arm illagerMainArm = Arm.RIGHT;
    public IllagerEntity.State illagerState = IllagerEntity.State.NEUTRAL;
    public int crossbowPullTime;
    public int itemUseTime;
    public float handSwingProgress;
}

