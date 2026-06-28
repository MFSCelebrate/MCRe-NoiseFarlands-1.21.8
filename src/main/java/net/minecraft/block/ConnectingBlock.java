/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class ConnectingBlock
extends Block {
    final static public BooleanProperty NORTH = Properties.NORTH;
    final static public BooleanProperty EAST = Properties.EAST;
    final static public BooleanProperty SOUTH = Properties.SOUTH;
    final static public BooleanProperty WEST = Properties.WEST;
    final static public BooleanProperty UP = Properties.UP;
    final static public BooleanProperty DOWN = Properties.DOWN;
    final static public Map<Direction, BooleanProperty> FACING_PROPERTIES = ImmutableMap.copyOf((Map)Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN)));
    final private Function<BlockState, VoxelShape> shapeFunction;

    protected ConnectingBlock(float radius, AbstractBlock.Settings settings) {
        super(settings);
        this.shapeFunction = this.createShapeFunction(radius);
    }

    protected abstract MapCodec<? extends ConnectingBlock> getCodec();

    private Function<BlockState, VoxelShape> createShapeFunction(float radius) {
        VoxelShape voxelShape = Block.createCubeShape(radius);
        Map<Direction, VoxelShape> map = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(radius, 0.0, 8.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape2 = voxelShape;
            for (Map.Entry<Direction, BooleanProperty> entry : FACING_PROPERTIES.entrySet()) {
                if (!((Boolean)state.get(entry.getValue())).booleanValue()) continue;
                voxelShape2 = VoxelShapes.union((VoxelShape)map.get(entry.getKey()), voxelShape2);
            }
            return voxelShape2;
        });
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return false;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapeFunction.apply(state);
    }
}

