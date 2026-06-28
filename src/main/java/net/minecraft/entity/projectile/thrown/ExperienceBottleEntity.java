/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class ExperienceBottleEntity
extends ThrownItemEntity {
    public ExperienceBottleEntity(EntityType<? extends ExperienceBottleEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>)entityType, world);
    }

    public ExperienceBottleEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityType.EXPERIENCE_BOTTLE, owner, world, stack);
    }

    public ExperienceBottleEntity(World world, double x, double y, double z, ItemStack stack) {
        super(EntityType.EXPERIENCE_BOTTLE, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected double getGravity() {
        return 0.07;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        World world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            serverWorld.syncWorldEvent(WorldEvents.SPLASH_POTION_SPLASHED, this.getBlockPos(), -13083194);
            int i = 3 + serverWorld.random.nextInt(5) + serverWorld.random.nextInt(5);
            if (hitResult instanceof BlockHitResult) {
                BlockHitResult blockHitResult = (BlockHitResult)hitResult;
                Vec3d vec3d = blockHitResult.getSide().getDoubleVector();
                ExperienceOrbEntity.spawn(serverWorld, hitResult.getPos(), vec3d, 1);
            } else {
                ExperienceOrbEntity.spawn(serverWorld, hitResult.getPos(), this.getVelocity().multiply(-1.0), 1);
            }
            this.discard();
        }
    }
}

