/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile.thrown;

import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class EggEntity
extends ThrownItemEntity {
    final static private EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0.0f, 0.0f);

    public EggEntity(EntityType<? extends EggEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>)entityType, world);
    }

    public EggEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityType.EGG, owner, world, stack);
    }

    public EggEntity(World world, double x, double y, double z, ItemStack stack) {
        super(EntityType.EGG, x, y, z, world, stack);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            double d = 0.08;
            for (int i = 0; i < 8; ++i) {
                this.net_minecraft_world_World_getWorld().addParticleClient(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.net_minecraft_entity_Entity_getOwner()), 0.0f);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.net_minecraft_world_World_getWorld().isClient) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }
                for (int j = 0; j < i; ++j) {
                    ChickenEntity chickenEntity = EntityType.CHICKEN.create(this.net_minecraft_world_World_getWorld(), SpawnReason.TRIGGERED);
                    if (chickenEntity == null) continue;
                    chickenEntity.setBreedingAge(-24000);
                    chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0f);
                    Optional.ofNullable(this.getStack().get(DataComponentTypes.CHICKEN_VARIANT)).flatMap(variant -> variant.resolveEntry(this.getRegistryManager())).ifPresent(chickenEntity::setVariant);
                    if (!chickenEntity.recalculateDimensions(EMPTY_DIMENSIONS)) break;
                    this.net_minecraft_world_World_getWorld().spawnEntity(chickenEntity);
                }
            }
            this.net_minecraft_world_World_getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
    }
}

