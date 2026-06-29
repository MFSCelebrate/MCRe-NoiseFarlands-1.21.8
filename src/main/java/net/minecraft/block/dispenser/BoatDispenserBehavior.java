/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;

public class BoatDispenserBehavior
extends ItemDispenserBehavior {
    final private ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
    final private EntityType<? extends AbstractBoatEntity> boatType;

    public BoatDispenserBehavior(EntityType<? extends AbstractBoatEntity> boatType) {
        this.boatType = boatType;
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        double h;
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        ServerWorld serverWorld = pointer.world();
        Vec3d vec3d = pointer.centerPos();
        double d = 0.5625 + (double)this.boatType.getWidth() / 2.0;
        double e = vec3d.getX() + (double)direction.getOffsetX() * d;
        double f = vec3d.getY() + (double)((float)direction.getOffsetY() * 1.125f);
        double g = vec3d.getZ() + (double)direction.getOffsetZ() * d;
        BlockPos blockPos = pointer.pos().net_minecraft_util_math_BlockPos_offset(direction);
        if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            h = 1.0;
        } else if (serverWorld.getBlockState(blockPos).isAir() && serverWorld.getFluidState(blockPos.net_minecraft_util_math_BlockPos_down()).isIn(FluidTags.WATER)) {
            h = 0.0;
        } else {
            return this.fallbackBehavior.net_minecraft_item_ItemStack_dispense(pointer, stack);
        }
        AbstractBoatEntity abstractBoatEntity = this.boatType.create(serverWorld, SpawnReason.DISPENSER);
        if (abstractBoatEntity != null) {
            abstractBoatEntity.initPosition(e, f + h, g);
            EntityType.copier(serverWorld, stack, null).accept(abstractBoatEntity);
            abstractBoatEntity.setYaw(direction.getPositiveHorizontalDegrees());
            serverWorld.spawnEntity(abstractBoatEntity);
            stack.decrement(1);
        }
        return stack;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
    }
}

