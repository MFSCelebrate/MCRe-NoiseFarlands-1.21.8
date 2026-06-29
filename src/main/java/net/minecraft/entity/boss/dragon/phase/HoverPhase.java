/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class HoverPhase
extends AbstractPhase {
    @Nullable
    private Vec3d target;

    public HoverPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void serverTick(ServerWorld world) {
        if (this.target == null) {
            this.target = this.dragon.getPos();
        }
    }

    @Override
    public boolean isSittingOrHovering() {
        return true;
    }

    @Override
    public void beginPhase() {
        this.target = null;
    }

    @Override
    public float getMaxYAcceleration() {
        return 1.0f;
    }

    @Override
    @Nullable
    public Vec3d getPathTarget() {
        return this.target;
    }

    public PhaseType<HoverPhase> getType() {
        return PhaseType.HOVER;
    }
}

