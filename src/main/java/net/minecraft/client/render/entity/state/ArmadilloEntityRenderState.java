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
import net.minecraft.entity.AnimationState;

@Environment(value=EnvType.CLIENT)
public class ArmadilloEntityRenderState
extends LivingEntityRenderState {
    public boolean rolledUp;
    final public AnimationState unrollingAnimationState = new AnimationState();
    final public AnimationState rollingAnimationState = new AnimationState();
    final public AnimationState scaredAnimationState = new AnimationState();
}

