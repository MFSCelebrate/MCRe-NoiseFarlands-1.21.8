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
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FrogEntityRenderState
extends LivingEntityRenderState {
    final static private Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/frog/temperate_frog.png");
    public boolean insideWaterOrBubbleColumn;
    final public AnimationState longJumpingAnimationState = new AnimationState();
    final public AnimationState croakingAnimationState = new AnimationState();
    final public AnimationState usingTongueAnimationState = new AnimationState();
    final public AnimationState idlingInWaterAnimationState = new AnimationState();
    public Identifier texture = DEFAULT_TEXTURE;
}

