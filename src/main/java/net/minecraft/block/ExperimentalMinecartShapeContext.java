/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class ExperimentalMinecartShapeContext
extends EntityShapeContext {
    @Nullable
    private BlockPos belowPos;
    @Nullable
    private BlockPos ascendingPos;

    protected ExperimentalMinecartShapeContext(AbstractMinecartEntity minecart, boolean collidesWithFluid) {
        super(minecart, collidesWithFluid, false);
        this.setIgnoredPositions(minecart);
    }

    private void setIgnoredPositions(AbstractMinecartEntity minecart) {
        BlockPos blockPos = minecart.getRailOrMinecartPos();
        BlockState blockState = minecart.net_minecraft_world_World_getWorld().getBlockState(blockPos);
        boolean bl = AbstractRailBlock.isRail(blockState);
        if (bl) {
            this.belowPos = blockPos.net_minecraft_util_math_BlockPos_down();
            RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
            if (railShape.isAscending()) {
                this.ascendingPos = switch (railShape) {
                    case RailShape.ASCENDING_EAST -> blockPos.net_minecraft_util_math_BlockPos_east();
                    case RailShape.ASCENDING_WEST -> blockPos.net_minecraft_util_math_BlockPos_west();
                    case RailShape.ASCENDING_NORTH -> blockPos.net_minecraft_util_math_BlockPos_north();
                    case RailShape.ASCENDING_SOUTH -> blockPos.net_minecraft_util_math_BlockPos_south();
                    default -> null;
                };
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
        if (pos.equals(this.belowPos) || pos.equals(this.ascendingPos)) {
            return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, world, pos);
    }
}

