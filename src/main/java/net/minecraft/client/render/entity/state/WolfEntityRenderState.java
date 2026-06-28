/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderState
extends LivingEntityRenderState {
    final static private Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/wolf/wolf.png");
    public boolean angerTime;
    public boolean inSittingPose;
    public float tailAngle = 0.62831855f;
    public float begAnimationProgress;
    public float shakeProgress;
    public float furWetBrightnessMultiplier = 1.0f;
    public Identifier texture = DEFAULT_TEXTURE;
    @Nullable
    public DyeColor collarColor;
    public ItemStack bodyArmor = ItemStack.EMPTY;

    public float getRoll(float shakeOffset) {
        float f = (this.shakeProgress + shakeOffset) / 1.8f;
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        return MathHelper.sin(f * (float)Math.PI) * MathHelper.sin(f * (float)Math.PI * 11.0f) * 0.15f * (float)Math.PI;
    }
}

