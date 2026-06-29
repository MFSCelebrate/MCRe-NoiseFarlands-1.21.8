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
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityRenderState
extends LivingEntityRenderState {
    public float spikesExtension;
    public float tailAngle;
    public Vec3d cameraPosVec = Vec3d.ZERO;
    @Nullable
    public Vec3d rotationVec;
    @Nullable
    public Vec3d lookAtPos;
    @Nullable
    public Vec3d beamTargetPos;
    public float beamTicks;
    public float beamProgress;
}

