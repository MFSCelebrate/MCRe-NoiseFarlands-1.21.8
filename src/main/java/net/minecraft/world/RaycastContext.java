/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RaycastContext {
    final private Vec3d start;
    final private Vec3d end;
    final private ShapeType shapeType;
    final private FluidHandling fluid;
    final private ShapeContext shapeContext;

    public RaycastContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, Entity entity) {
        this(start, end, shapeType, fluidHandling, ShapeContext.of(entity));
    }

    public RaycastContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, ShapeContext shapeContext) {
        this.start = start;
        this.end = end;
        this.shapeType = shapeType;
        this.fluid = fluidHandling;
        this.shapeContext = shapeContext;
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

    public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
        return this.shapeType.get(state, world, pos, this.shapeContext);
    }

    public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
        return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
    }

    public static final class ShapeType
    extends Enum<ShapeType>
    implements ShapeProvider {
        final static public ShapeType COLLIDER = new ShapeType(AbstractBlock.AbstractBlockState::getCollisionShape);
        final static public ShapeType OUTLINE = new ShapeType(AbstractBlock.AbstractBlockState::getOutlineShape);
        final static public ShapeType VISUAL = new ShapeType(AbstractBlock.AbstractBlockState::getCameraCollisionShape);
        final static public ShapeType FALLDAMAGE_RESETTING = new ShapeType((state, world, pos, context) -> {
            if (state.isIn(BlockTags.FALL_DAMAGE_RESETTING)) {
                return VoxelShapes.fullCube();
            }
            return VoxelShapes.empty();
        });
        final private ShapeProvider provider;
        final static private ShapeType[] field_17561;

        public static ShapeType[] values() {
            return (ShapeType[])field_17561.clone();
        }

        public static ShapeType valueOf(String string) {
            return Enum.valueOf(ShapeType.class, string);
        }

        private ShapeType(ShapeProvider provider) {
            this.provider = provider;
        }

        @Override
        public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
            return this.provider.get(blockState, blockView, blockPos, shapeContext);
        }

        private static ShapeType[] method_36690() {
            return new ShapeType[]{COLLIDER, OUTLINE, VISUAL, FALLDAMAGE_RESETTING};
        }

        static {
            field_17561 = ShapeType.method_36690();
        }
    }

    public static final class FluidHandling
    extends Enum<FluidHandling> {
        final static public FluidHandling NONE = new FluidHandling(state -> false);
        final static public FluidHandling SOURCE_ONLY = new FluidHandling(FluidState::isStill);
        final static public FluidHandling ANY = new FluidHandling(state -> !state.isEmpty());
        final static public FluidHandling WATER = new FluidHandling(state -> state.isIn(FluidTags.WATER));
        final private Predicate<FluidState> predicate;
        final static private FluidHandling[] field_1349;

        public static FluidHandling[] values() {
            return (FluidHandling[])field_1349.clone();
        }

        public static FluidHandling valueOf(String string) {
            return Enum.valueOf(FluidHandling.class, string);
        }

        private FluidHandling(Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(FluidState state) {
            return this.predicate.test(state);
        }

        private static FluidHandling[] method_36691() {
            return new FluidHandling[]{NONE, SOURCE_ONLY, ANY, WATER};
        }

        static {
            field_1349 = FluidHandling.method_36691();
        }
    }

    public static interface ShapeProvider {
        public VoxelShape get(BlockState var1, BlockView var2, BlockPos var3, ShapeContext var4);
    }
}

