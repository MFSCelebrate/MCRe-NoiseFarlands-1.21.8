/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class TrackIronGolemTargetGoal
extends TrackTargetGoal {
    final private IronGolemEntity golem;
    @Nullable
    private LivingEntity target;
    final private TargetPredicate targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);

    public TrackIronGolemTargetGoal(IronGolemEntity golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        PlayerEntity playerEntity2;
        Box box = this.golem.getBoundingBox().expand(10.0, 8.0, 10.0);
        ServerWorld serverWorld = TrackIronGolemTargetGoal.getServerWorld(this.golem);
        List list = serverWorld.getTargets(VillagerEntity.class, this.targetPredicate, this.golem, box);
        List list2 = serverWorld.getPlayers(this.targetPredicate, this.golem, box);
        for (LivingEntity livingEntity : list) {
            VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
            for (PlayerEntity playerEntity : list2) {
                int i = villagerEntity.getReputation(playerEntity);
                if (i > -100) continue;
                this.target = playerEntity;
            }
        }
        if (this.target == null) {
            return false;
        }
        LivingEntity livingEntity = this.target;
        return !(livingEntity instanceof PlayerEntity) || !(playerEntity2 = (PlayerEntity)livingEntity).isSpectator() && !playerEntity2.isCreative();
    }

    @Override
    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}

