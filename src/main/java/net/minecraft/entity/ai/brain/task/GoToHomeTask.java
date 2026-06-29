/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.apache.commons.lang3.mutable.MutableLong
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoToHomeTask {
    final static private int POI_EXPIRY = 40;
    final static private int MAX_TRIES = 5;
    final static private int RUN_TIME = 20;
    final static private int MAX_DISTANCE = 4;

    public static Task<PathAwareEntity> create(float speed) {
        Long2LongOpenHashMap long2LongMap = new Long2LongOpenHashMap();
        MutableLong mutableLong = new MutableLong(0L);
        return TaskTriggerer.task(arg_0 -> GoToHomeTask.method_47052(mutableLong, (Long2LongMap)long2LongMap, speed, arg_0));
    }

    private static App method_47052(MutableLong context, Long2LongMap long2LongMap, float f, TaskTriggerer.TaskContext taskContext) {
        return taskContext.group(taskContext.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), taskContext.queryMemoryAbsent(MemoryModuleType.HOME)).apply((Applicative)taskContext, (walkTarget, home) -> (world, entity, time) -> {
            if (world.getTime() - context.getValue() < 20L) {
                return false;
            }
            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
            if (optional.isEmpty() || optional.get().getSquaredDistance(entity.getBlockPos()) <= 4.0) {
                return false;
            }
            MutableInt mutableInt = new MutableInt(0);
            context.setValue(world.getTime() + (long)world.getRandom().nextInt(20));
            Predicate<BlockPos> predicate = pos -> {
                long l = pos.asLong();
                if (long2LongMap.containsKey(l)) {
                    return false;
                }
                if (mutableInt.incrementAndGet() >= 5) {
                    return false;
                }
                long2LongMap.put(l, context.getValue() + 40L);
                return true;
            };
            Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = pointOfInterestStorage.getTypesAndPositions(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), predicate, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toSet());
            Path path = FindPointOfInterestTask.findPathToPoi(entity, set);
            if (path != null && path.reachesTarget()) {
                BlockPos blockPos = path.getTarget();
                Optional<RegistryEntry<PointOfInterestType>> optional2 = pointOfInterestStorage.getType(blockPos);
                if (optional2.isPresent()) {
                    walkTarget.remember(new WalkTarget(blockPos, f, 1));
                    DebugInfoSender.sendPointOfInterest(world, blockPos);
                }
            } else if (mutableInt.getValue() < 5) {
                long2LongMap.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < context.getValue());
            }
            return true;
        });
    }
}

