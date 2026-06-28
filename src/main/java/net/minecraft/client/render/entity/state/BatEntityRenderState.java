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
public class BatEntityRenderState
extends LivingEntityRenderState {
    public boolean roosting;
    final public AnimationState flyingAnimationState = new AnimationState();
    final public AnimationState roostingAnimationState = new AnimationState();
}

