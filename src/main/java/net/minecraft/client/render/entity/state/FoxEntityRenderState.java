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
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.entity.passive.FoxEntity;

@Environment(value=EnvType.CLIENT)
public class FoxEntityRenderState
extends ItemHolderEntityRenderState {
    public float headRoll;
    public float bodyRotationHeightOffset;
    public boolean inSneakingPose;
    public boolean sleeping;
    public boolean sitting;
    public boolean walking;
    public boolean chasing;
    public FoxEntity.Variant type = FoxEntity.Variant.DEFAULT;
}

