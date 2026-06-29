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
public class BreezeEntityRenderState
extends LivingEntityRenderState {
    final public AnimationState idleAnimationState = new AnimationState();
    final public AnimationState shootingAnimationState = new AnimationState();
    final public AnimationState slidingAnimationState = new AnimationState();
    final public AnimationState slidingBackAnimationState = new AnimationState();
    final public AnimationState inhalingAnimationState = new AnimationState();
    final public AnimationState longJumpingAnimationState = new AnimationState();
}

