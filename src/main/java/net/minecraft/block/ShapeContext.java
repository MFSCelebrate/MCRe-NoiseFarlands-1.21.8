/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ExperimentalMinecartShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public interface ShapeContext {
    public static ShapeContext absent() {
        return EntityShapeContext.ABSENT;
    }

    public static ShapeContext of(Entity entity) {
        Entity entity2 = entity;
        Objects.requireNonNull(entity2);
        Entity entity3 = entity2;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractMinecartEntity.class}, (Object)entity3, n)) {
            case 0 -> {
                AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity3;
                if (AbstractMinecartEntity.areMinecartImprovementsEnabled(abstractMinecartEntity.net_minecraft_world_World_getWorld())) {
                    yield new ExperimentalMinecartShapeContext(abstractMinecartEntity, false);
                }
                yield new EntityShapeContext(entity, false, false);
            }
            default -> new EntityShapeContext(entity, false, false);
        };
    }

    public static ShapeContext of(Entity entity, boolean collidesWithFluid) {
        return new EntityShapeContext(entity, collidesWithFluid, false);
    }

    public static ShapeContext ofPlacement(@Nullable PlayerEntity player) {
        Predicate<FluidState> predicate;
        ItemStack itemStack;
        PlayerEntity livingEntity;
        boolean bl = player != null ? player.isDescending() : false;
        double d = player != null ? player.getY() : -1.7976931348623157E308;
        if (player instanceof LivingEntity) {
            livingEntity = player;
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if (player instanceof LivingEntity) {
            livingEntity = player;
            predicate = state -> livingEntity.canWalkOnFluid((FluidState)state);
        } else {
            predicate = state -> false;
        }
        return new EntityShapeContext(bl, true, d, itemStack, predicate, player);
    }

    public static ShapeContext ofCollision(@Nullable Entity entity, double y) {
        Predicate<FluidState> predicate;
        ItemStack itemStack;
        LivingEntity livingEntity;
        boolean bl = entity != null ? entity.isDescending() : false;
        double d = entity != null ? y : -1.7976931348623157E308;
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity;
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity;
            predicate = state -> livingEntity.canWalkOnFluid((FluidState)state);
        } else {
            predicate = state -> false;
        }
        return new EntityShapeContext(bl, true, d, itemStack, predicate, entity);
    }

    public boolean isDescending();

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

    public boolean isHolding(Item var1);

    public boolean canWalkOnFluid(FluidState var1, FluidState var2);

    public VoxelShape getCollisionShape(BlockState var1, CollisionView var2, BlockPos var3);

    default public boolean isPlacement() {
        return false;
    }
}

