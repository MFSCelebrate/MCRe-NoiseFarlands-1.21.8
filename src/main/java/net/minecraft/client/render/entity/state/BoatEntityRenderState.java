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
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderState
extends EntityRenderState {
    public float yaw;
    public int damageWobbleSide;
    public float damageWobbleTicks;
    public float damageWobbleStrength;
    public float bubbleWobble;
    public boolean submergedInWater;
    public float leftPaddleAngle;
    public float rightPaddleAngle;
}

