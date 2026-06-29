/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FireballEntity
extends AbstractFireballEntity {
    final static private byte DEFAULT_EXPLOSION_POWER = 1;
    private int explosionPower = 1;

    public FireballEntity(EntityType<? extends FireballEntity> entityType, World world) {
        super((EntityType<? extends AbstractFireballEntity>)entityType, world);
    }

    public FireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.FIREBALL, owner, velocity, world);
        this.explosionPower = explosionPower;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        World world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            boolean bl = serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            this.net_minecraft_world_World_getWorld().createExplosion((Entity)this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, bl, World.ExplosionSourceType.MOB);
            this.discard();
        }
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
        DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
        entity.damage(serverWorld, damageSource, 6.0f);
        EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.explosionPower = view.getByte("ExplosionPower", 1);
    }
}

