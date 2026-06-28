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
public class WardenEntityRenderState
extends LivingEntityRenderState {
    public float tendrilAlpha;
    public float heartAlpha;
    final public AnimationState roaringAnimationState = new AnimationState();
    final public AnimationState sniffingAnimationState = new AnimationState();
    final public AnimationState emergingAnimationState = new AnimationState();
    final public AnimationState diggingAnimationState = new AnimationState();
    final public AnimationState attackingAnimationState = new AnimationState();
    final public AnimationState chargingSonicBoomAnimationState = new AnimationState();
}

