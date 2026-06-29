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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BeeEntityRenderState
extends LivingEntityRenderState {
    public float bodyPitch;
    public boolean hasStinger = true;
    public boolean stoppedOnGround;
    public boolean angry;
    public boolean hasNectar;
}

