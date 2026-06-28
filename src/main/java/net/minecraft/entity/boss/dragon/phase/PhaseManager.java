/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.entity.boss.dragon.phase;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PhaseManager {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private EnderDragonEntity dragon;
    final private Phase[] phases = new Phase[PhaseType.count()];
    @Nullable
    private Phase current;

    public PhaseManager(EnderDragonEntity dragon) {
        this.dragon = dragon;
        this.setPhase(PhaseType.HOVER);
    }

    public void setPhase(PhaseType<?> type) {
        if (this.current != null && type == this.current.getType()) {
            return;
        }
        if (this.current != null) {
            this.current.endPhase();
        }
        this.current = this.create(type);
        if (!this.dragon.net_minecraft_world_World_getWorld().isClient) {
            this.dragon.getDataTracker().set(EnderDragonEntity.PHASE_TYPE, type.getTypeId());
        }
        LOGGER.debug("Dragon is now in phase {} on the {}", type, (Object)(this.dragon.net_minecraft_world_World_getWorld().isClient ? "client" : "server"));
        this.current.beginPhase();
    }

    public Phase getCurrent() {
        return this.current;
    }

    public <T extends Phase> T create(PhaseType<T> type) {
        int i = type.getTypeId();
        if (this.phases[i] == null) {
            this.phases[i] = type.create(this.dragon);
        }
        return (T)this.phases[i];
    }
}

