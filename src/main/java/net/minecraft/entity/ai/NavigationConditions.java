/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class NavigationConditions {
    public static boolean hasMobNavigation(MobEntity entity) {
        return entity.getNavigation().canControlOpeningDoors();
    }

    public static boolean isPositionTargetInRange(PathAwareEntity entity, int extraDistance) {
        return entity.hasPositionTarget() && entity.getPositionTarget().isWithinDistance(entity.getPos(), (double)(entity.getPositionTargetRange() + extraDistance + 1));
    }

    public static boolean isHeightInvalid(BlockPos pos, PathAwareEntity entity) {
        return entity.net_minecraft_world_World_getWorld().isOutOfHeightLimit(pos.getY());
    }

    public static boolean isPositionTargetOutOfWalkRange(boolean posTargetInRange, PathAwareEntity entity, BlockPos pos) {
        return posTargetInRange && !entity.isInPositionTargetRange(pos);
    }

    public static boolean isInvalidPosition(EntityNavigation navigation, BlockPos pos) {
        return !navigation.isValidPosition(pos);
    }

    public static boolean isWaterAt(PathAwareEntity entity, BlockPos pos) {
        return entity.net_minecraft_world_World_getWorld().getFluidState(pos).isIn(FluidTags.WATER);
    }

    public static boolean hasPathfindingPenalty(PathAwareEntity entity, BlockPos pos) {
        return entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(entity, pos)) != 0.0f;
    }

    public static boolean isSolidAt(PathAwareEntity entity, BlockPos pos) {
        return entity.net_minecraft_world_World_getWorld().getBlockState(pos).isSolid();
    }
}

