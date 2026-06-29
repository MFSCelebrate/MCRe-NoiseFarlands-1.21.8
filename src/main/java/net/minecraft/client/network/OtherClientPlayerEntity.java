/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;

@Environment(value=EnvType.CLIENT)
public class OtherClientPlayerEntity
extends AbstractClientPlayerEntity {
    private Vec3d clientVelocity = Vec3d.ZERO;
    private int velocityLerpDivisor;

    public OtherClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
        this.noClip = true;
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * OtherClientPlayerEntity.getRenderDistanceMultiplier()) * d;
    }

    @Override
    public boolean clientDamage(DamageSource source) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateLimbs(false);
    }

    @Override
    public void tickMovement() {
        if (this.isInterpolating()) {
            this.getInterpolator().tick();
        }
        if (this.headTrackingIncrements > 0) {
            this.lerpHeadYaw(this.headTrackingIncrements, this.serverHeadYaw);
            --this.headTrackingIncrements;
        }
        if (this.velocityLerpDivisor > 0) {
            this.addVelocityInternal(new Vec3d((this.clientVelocity.x - this.getVelocity().x) / (double)this.velocityLerpDivisor, (this.clientVelocity.y - this.getVelocity().y) / (double)this.velocityLerpDivisor, (this.clientVelocity.z - this.getVelocity().z) / (double)this.velocityLerpDivisor));
            --this.velocityLerpDivisor;
        }
        this.lastStrideDistance = this.strideDistance;
        this.tickHandSwing();
        float f = !this.isOnGround() || this.isDead() ? 0.0f : (float)Math.min(0.1, this.getVelocity().horizontalLength());
        this.strideDistance += (f - this.strideDistance) * 0.4f;
        ScopedProfiler scopedProfiler = Profilers.get().scoped("push");
        try {
            this.tickCramming();
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        this.clientVelocity = new Vec3d(x, y, z);
        this.velocityLerpDivisor = this.getType().getTrackTickInterval() + 1;
    }

    @Override
    protected void updatePose() {
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.resetPosition();
    }
}

