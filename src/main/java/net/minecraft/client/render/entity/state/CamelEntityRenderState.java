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
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class CamelEntityRenderState
extends LivingEntityRenderState {
    public ItemStack saddleStack = ItemStack.EMPTY;
    public boolean hasPassengers;
    public float jumpCooldown;
    final public AnimationState sittingTransitionAnimationState = new AnimationState();
    final public AnimationState sittingAnimationState = new AnimationState();
    final public AnimationState standingTransitionAnimationState = new AnimationState();
    final public AnimationState idlingAnimationState = new AnimationState();
    final public AnimationState dashingAnimationState = new AnimationState();
}

