/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public interface SpawnLocationTypes {
    final static public SpawnLocation UNRESTRICTED = (world, pos, entityType) -> true;
    final static public SpawnLocation IN_WATER = (world, pos, entityType) -> {
        if (entityType == null || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_up();
        return world.getFluidState(pos).isIn(FluidTags.WATER) && !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
    };
    final static public SpawnLocation IN_LAVA = (world, pos, entityType) -> {
        if (entityType == null || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        return world.getFluidState(pos).isIn(FluidTags.LAVA);
    };
    final static public SpawnLocation ON_GROUND = new SpawnLocation(){

        @Override
        public boolean isSpawnPositionOk(WorldView worldView, BlockPos blockPos, @Nullable EntityType<?> entityType) {
            if (entityType == null || !worldView.getWorldBorder().contains(blockPos)) {
                return false;
            }
            BlockPos blockPos2 = blockPos.net_minecraft_util_math_BlockPos_up();
            BlockPos blockPos3 = blockPos.net_minecraft_util_math_BlockPos_down();
            BlockState blockState = worldView.getBlockState(blockPos3);
            if (!blockState.allowsSpawning(worldView, blockPos3, entityType)) {
                return false;
            }
            return this.isClearForSpawn(worldView, blockPos, entityType) && this.isClearForSpawn(worldView, blockPos2, entityType);
        }

        private boolean isClearForSpawn(WorldView world, BlockPos pos, EntityType<?> entityType) {
            BlockState blockState = world.getBlockState(pos);
            return SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), entityType);
        }

        @Override
        public BlockPos adjustPosition(WorldView world, BlockPos pos) {
            BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_down();
            if (world.getBlockState(blockPos).canPathfindThrough(NavigationType.LAND)) {
                return blockPos;
            }
            return pos;
        }
    };
}

