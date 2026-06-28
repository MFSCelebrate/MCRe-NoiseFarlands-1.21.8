/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity
extends AbstractFireballEntity {
    public SmallFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
        super((EntityType<? extends AbstractFireballEntity>)entityType, world);
    }

    public SmallFireballEntity(World world, LivingEntity owner, Vec3d velocity) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, owner, velocity, world);
    }

    public SmallFireballEntity(World world, double x, double y, double z, Vec3d velocity) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, x, y, z, velocity, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        World world = this.net_minecraft_world_World_getWorld();
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.net_minecraft_entity_Entity_getOwner();
        int i = entity.getFireTicks();
        entity.setOnFireFor(5.0f);
        DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
        if (!entity.damage(serverWorld, damageSource, 5.0f)) {
            entity.setFireTicks(i);
        } else {
            EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        World world = this.net_minecraft_world_World_getWorld();
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        Entity entity = this.net_minecraft_entity_Entity_getOwner();
        if (!(entity instanceof MobEntity) || serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            BlockPos blockPos = blockHitResult.getBlockPos().net_minecraft_util_math_BlockPos_offset(blockHitResult.getSide());
            if (this.net_minecraft_world_World_getWorld().isAir(blockPos)) {
                this.net_minecraft_world_World_getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.net_minecraft_world_World_getWorld(), blockPos));
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.net_minecraft_world_World_getWorld().isClient) {
            this.discard();
        }
    }
}

