/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract sealed class SideShapeType
extends Enum<SideShapeType> {
    final static public SideShapeType FULL = new SideShapeType(){

        @Override
        public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
            return Block.isFaceFullSquare(state.getSidesShape(world, pos), direction);
        }
    };
    final static public SideShapeType CENTER = new SideShapeType(){
        final private VoxelShape squareCuboid = Block.createColumnShape(2.0, 0.0, 10.0);

        @Override
        public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
            return !VoxelShapes.matchesAnywhere(state.getSidesShape(world, pos).getFace(direction), this.squareCuboid, BooleanBiFunction.ONLY_SECOND);
        }
    };
    final static public SideShapeType RIGID = new SideShapeType(){
        final private VoxelShape hollowSquareCuboid = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.createColumnShape(12.0, 0.0, 16.0), BooleanBiFunction.ONLY_FIRST);

        @Override
        public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
            return !VoxelShapes.matchesAnywhere(state.getSidesShape(world, pos).getFace(direction), this.hollowSquareCuboid, BooleanBiFunction.ONLY_SECOND);
        }
    };
    final static private SideShapeType[] field_25825;

    public static SideShapeType[] values() {
        return (SideShapeType[])field_25825.clone();
    }

    public static SideShapeType valueOf(String string) {
        return Enum.valueOf(SideShapeType.class, string);
    }

    public abstract boolean matches(BlockState var1, BlockView var2, BlockPos var3, Direction var4);

    private static SideShapeType[] method_36711() {
        return new SideShapeType[]{FULL, CENTER, RIGID};
    }

    static {
        field_25825 = SideShapeType.method_36711();
    }
}

