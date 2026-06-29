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
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Environment(value=EnvType.CLIENT)
public class BipedEntityRenderState
extends ArmedEntityRenderState {
    public float leaningPitch;
    public float handSwingProgress;
    public float limbAmplitudeInverse = 1.0f;
    public float crossbowPullTime;
    public int itemUseTime;
    public Arm preferredArm = Arm.RIGHT;
    public Hand activeHand = Hand.MAIN_HAND;
    public boolean isInSneakingPose;
    public boolean isGliding;
    public boolean isSwimming;
    public boolean hasVehicle;
    public boolean isUsingItem;
    public float leftWingPitch;
    public float leftWingYaw;
    public float leftWingRoll;
    public ItemStack equippedHeadStack = ItemStack.EMPTY;
    public ItemStack equippedChestStack = ItemStack.EMPTY;
    public ItemStack equippedLegsStack = ItemStack.EMPTY;
    public ItemStack equippedFeetStack = ItemStack.EMPTY;
}

