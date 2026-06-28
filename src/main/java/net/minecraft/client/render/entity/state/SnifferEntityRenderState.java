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
public class SnifferEntityRenderState
extends LivingEntityRenderState {
    public boolean searching;
    final public AnimationState diggingAnimationState = new AnimationState();
    final public AnimationState sniffingAnimationState = new AnimationState();
    final public AnimationState risingAnimationState = new AnimationState();
    final public AnimationState feelingHappyAnimationState = new AnimationState();
    final public AnimationState scentingAnimationState = new AnimationState();
}

