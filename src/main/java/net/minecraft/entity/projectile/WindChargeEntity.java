/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class WindChargeEntity
extends AbstractWindChargeEntity {
    final static private ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(true, false, Optional.of(Float.valueOf(1.22f)), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()));
    final static private float EXPLOSION_POWER = 1.2f;
    final static private float MAX_RENDER_DISTANCE_WHEN_NEWLY_SPAWNED = MathHelper.square(3.5f);
    private int deflectCooldown = 5;

    public WindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindChargeEntity(PlayerEntity player, World world, double x, double y, double z) {
        super(EntityType.WIND_CHARGE, world, player, x, y, z);
    }

    public WindChargeEntity(World world, double x, double y, double z, Vec3d velocity) {
        super((EntityType<? extends AbstractWindChargeEntity>)EntityType.WIND_CHARGE, x, y, z, velocity, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.deflectCooldown > 0) {
            --this.deflectCooldown;
        }
    }

    @Override
    public boolean deflect(ProjectileDeflection deflection, @Nullable Entity deflector, @Nullable Entity owner, boolean fromAttack) {
        if (this.deflectCooldown > 0) {
            return false;
        }
        return super.deflect(deflection, deflector, owner, fromAttack);
    }

    @Override
    protected void createExplosion(Vec3d pos) {
        this.net_minecraft_world_World_getWorld().createExplosion(this, null, EXPLOSION_BEHAVIOR, pos.getX(), pos.getY(), pos.getZ(), 1.2f, false, World.ExplosionSourceType.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST);
    }

    @Override
    public boolean shouldRender(double distance) {
        if (this.age < 2 && distance < (double)MAX_RENDER_DISTANCE_WHEN_NEWLY_SPAWNED) {
            return false;
        }
        return super.shouldRender(distance);
    }
}

