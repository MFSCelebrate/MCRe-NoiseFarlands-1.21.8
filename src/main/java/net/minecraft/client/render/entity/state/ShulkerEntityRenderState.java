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
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderState
extends LivingEntityRenderState {
    public Vec3d renderPositionOffset = Vec3d.ZERO;
    @Nullable
    public DyeColor color;
    public float openProgress;
    public float headYaw;
    public float shellYaw;
    public Direction facing = Direction.DOWN;
}

