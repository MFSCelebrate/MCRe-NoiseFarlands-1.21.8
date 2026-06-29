/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class EntityShapeContext
implements ShapeContext {
    final static protected ShapeContext ABSENT = new EntityShapeContext(false, false, -1.7976931348623157E308, ItemStack.EMPTY, fluidState -> false, null){

        @Override
        public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
            return defaultValue;
        }
    };
    final private boolean descending;
    final private double minY;
    final private boolean placement;
    final private ItemStack heldItem;
    final private Predicate<FluidState> walkOnFluidPredicate;
    @Nullable
    final private Entity entity;

    protected EntityShapeContext(boolean descending, boolean placement, double minY, ItemStack heldItem, Predicate<FluidState> walkOnFluidPredicate, @Nullable Entity entity) {
        this.descending = descending;
        this.placement = placement;
        this.minY = minY;
        this.heldItem = heldItem;
        this.walkOnFluidPredicate = walkOnFluidPredicate;
        this.entity = entity;
    }

    @Deprecated
    protected EntityShapeContext(Entity entity, boolean collidesWithFluid, boolean placement) {
        Predicate<FluidState> predicate;
        ItemStack itemStack;
        LivingEntity livingEntity;
        boolean bl = entity.isDescending();
        double d = entity.getY();
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity;
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if (collidesWithFluid) {
            predicate = state -> true;
        } else if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity;
            predicate = state -> livingEntity.canWalkOnFluid((FluidState)state);
        } else {
            predicate = state -> false;
        }
        this(bl, placement, d, itemStack, predicate, entity);
    }

    @Override
    public boolean isHolding(Item item) {
        return this.heldItem.isOf(item);
    }

    @Override
    public boolean canWalkOnFluid(FluidState stateAbove, FluidState state) {
        return this.walkOnFluidPredicate.test(state) && !stateAbove.getFluid().matchesType(state.getFluid());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
        return state.getCollisionShape(world, pos, this);
    }

    @Override
    public boolean isDescending() {
        return this.descending;
    }

    @Override
    public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
        return this.minY > (double)pos.getY() + shape.getMax(Direction.Axis.Y) - (double)1.0E-5f;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public boolean isPlacement() {
        return this.placement;
    }
}

