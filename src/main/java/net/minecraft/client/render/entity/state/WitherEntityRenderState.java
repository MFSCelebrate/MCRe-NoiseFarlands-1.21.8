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
public class WitherEntityRenderState
extends LivingEntityRenderState {
    public float[] sideHeadPitches = new float[2];
    public float[] sideHeadYaws = new float[2];
    public float invulnerableTimer;
    public boolean renderOverlay;
}

