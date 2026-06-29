/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class FrogEatEntityTask
extends MultiTickTask<FrogEntity> {
    final static public int RUN_TIME = 100;
    final static public int CATCH_DURATION = 6;
    final static public int EAT_DURATION = 10;
    final static private float MAX_DISTANCE = 1.75f;
    final static private float VELOCITY_MULTIPLIER = 0.75f;
    final static public int UNREACHABLE_TONGUE_TARGETS_START_TIME = 100;
    final static public int MAX_UNREACHABLE_TONGUE_TARGETS = 5;
    private int eatTick;
    private int moveToTargetTick;
    final private SoundEvent tongueSound;
    final private SoundEvent eatSound;
    private Vec3d targetPos;
    private Phase phase = Phase.DONE;

    public FrogEatEntityTask(SoundEvent tongueSound, SoundEvent eatSound) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.IS_PANICKING, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), 100);
        this.tongueSound = tongueSound;
        this.eatSound = eatSound;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get();
        boolean bl = this.isTargetReachable(frogEntity, livingEntity);
        if (!bl) {
            frogEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            this.markTargetAsUnreachable(frogEntity, livingEntity);
        }
        return bl && frogEntity.getPose() != EntityPose.CROAKING && FrogEntity.isValidFrogFood(livingEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        return frogEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.phase != Phase.DONE && !frogEntity.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get();
        TargetUtil.lookAt(frogEntity, livingEntity);
        frogEntity.setFrogTarget(livingEntity);
        frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0f, 0));
        this.moveToTargetTick = 10;
        this.phase = Phase.MOVE_TO_TARGET;
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        frogEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        frogEntity.clearFrogTarget();
        frogEntity.setPose(EntityPose.STANDING);
    }

    private void eat(ServerWorld world, FrogEntity frog) {
        Entity entity;
        world.playSoundFromEntity(null, frog, this.eatSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
        Optional<Entity> optional = frog.getFrogTarget();
        if (optional.isPresent() && (entity = optional.get()).isAlive()) {
            frog.tryAttack(world, entity);
            if (!entity.isAlive()) {
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get();
        frogEntity.setFrogTarget(livingEntity);
        switch (this.phase.ordinal()) {
            case 0: {
                if (livingEntity.distanceTo(frogEntity) < 1.75f) {
                    serverWorld.playSoundFromEntity(null, frogEntity, this.tongueSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
                    frogEntity.setPose(EntityPose.USING_TONGUE);
                    livingEntity.setVelocity(livingEntity.getPos().relativize(frogEntity.getPos()).normalize().multiply(0.75));
                    this.targetPos = livingEntity.getPos();
                    this.eatTick = 0;
                    this.phase = Phase.CATCH_ANIMATION;
                    break;
                }
                if (this.moveToTargetTick <= 0) {
                    frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0f, 0));
                    this.moveToTargetTick = 10;
                    break;
                }
                --this.moveToTargetTick;
                break;
            }
            case 1: {
                if (this.eatTick++ < 6) break;
                this.phase = Phase.EAT_ANIMATION;
                this.eat(serverWorld, frogEntity);
                break;
            }
            case 2: {
                if (this.eatTick >= 10) {
                    this.phase = Phase.DONE;
                    break;
                }
                ++this.eatTick;
                break;
            }
        }
    }

    private boolean isTargetReachable(FrogEntity entity, LivingEntity target) {
        Path path = entity.getNavigation().findPathTo(target, 0);
        return path != null && path.getManhattanDistanceFromTarget() < 1.75f;
    }

    private void markTargetAsUnreachable(FrogEntity entity, LivingEntity target) {
        boolean bl;
        List list = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
        boolean bl2 = bl = !list.contains(target.getUuid());
        if (list.size() == 5 && bl) {
            list.remove(0);
        }
        if (bl) {
            list.add(target.getUuid());
        }
        entity.getBrain().remember(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS, list, 100L);
    }

    @Override
    protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (FrogEntity)entity, time);
    }

    @Override
    protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (FrogEntity)entity, time);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (FrogEntity)entity, time);
    }

    static final class Phase
    extends Enum<Phase> {
        final static public Phase MOVE_TO_TARGET = new Phase();
        final static public Phase CATCH_ANIMATION = new Phase();
        final static public Phase EAT_ANIMATION = new Phase();
        final static public Phase DONE = new Phase();
        final static private Phase[] field_37495;

        public static Phase[] values() {
            return (Phase[])field_37495.clone();
        }

        public static Phase valueOf(String string) {
            return Enum.valueOf(Phase.class, string);
        }

        private static Phase[] method_41390() {
            return new Phase[]{MOVE_TO_TARGET, CATCH_ANIMATION, EAT_ANIMATION, DONE};
        }

        static {
            field_37495 = Phase.method_41390();
        }
    }
}

