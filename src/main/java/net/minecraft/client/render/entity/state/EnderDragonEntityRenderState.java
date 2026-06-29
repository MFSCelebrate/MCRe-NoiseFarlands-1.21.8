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
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.boss.dragon.EnderDragonFrameTracker;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderState
extends EntityRenderState {
    public float wingPosition;
    public float ticksSinceDeath;
    public boolean hurt;
    @Nullable
    public Vec3d crystalBeamPos;
    public boolean inLandingOrTakeoffPhase;
    public boolean sittingOrHovering;
    public double squaredDistanceFromOrigin;
    public float tickProgress;
    final public EnderDragonFrameTracker frameTracker = new EnderDragonFrameTracker();

    public EnderDragonFrameTracker.Frame getLerpedFrame(int age) {
        return this.frameTracker.getLerpedFrame(age, this.tickProgress);
    }

    public float getNeckPartPitchOffset(int id, EnderDragonFrameTracker.Frame bodyFrame, EnderDragonFrameTracker.Frame neckFrame) {
        double d = this.inLandingOrTakeoffPhase ? (double)id / Math.max(this.squaredDistanceFromOrigin / 4.0, 1.0) : (this.sittingOrHovering ? (double)id : (id == 6 ? 0.0 : neckFrame.y() - bodyFrame.y()));
        return (float)d;
    }
}

