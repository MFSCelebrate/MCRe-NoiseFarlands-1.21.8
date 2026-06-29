/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public abstract class VehicleEntity extends Entity {
    protected static final TrackedData<
            Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<
            Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<
            Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public VehicleEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean clientDamage(DamageSource source) {
        return true;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.isRemoved()) {
            return true;
        }
        if (this.isAlwaysInvulnerableTo(source)) {
            return false;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.scheduleVelocityUpdate();
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        Entity attacker = source.getAttacker();
        boolean isCreativePlayer = false;
        if (attacker instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) attacker;
            if (playerEntity.getAbilities().creativeMode) {
                isCreativePlayer = true;
            }
        }
        if (!isCreativePlayer && (this.getDamageWobbleStrength() > 40.0f || this.shouldAlwaysKill(source))) {
            this.killAndDropSelf(world, source);
        } else if (isCreativePlayer) {
            this.discard();
        }
        return true;
    }

    boolean shouldAlwaysKill(DamageSource source) {
        return false;
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return explosion.getCausingEntity()
                        instanceof
                        MobEntity && !explosion.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    public void killAndDropItem(ServerWorld world, Item item) {
        this.kill(world);
        if (!world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
        this.dropStack(world, itemStack);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DAMAGE_WOBBLE_TICKS, 0);
        builder.add(DAMAGE_WOBBLE_SIDE, 1);
        builder.add(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(0.0f));
    }

    public void setDamageWobbleTicks(int damageWobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, damageWobbleTicks);
    }

    public void setDamageWobbleSide(int damageWobbleSide) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, damageWobbleSide);
    }

    public void setDamageWobbleStrength(float damageWobbleStrength) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(damageWobbleStrength));
    }

    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH).floatValue();
    }

    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }

    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    protected void killAndDropSelf(ServerWorld world, DamageSource damageSource) {
        this.killAndDropItem(world, this.asItem());
    }

    @Override
    public int getDefaultPortalCooldown() {
        return 10;
    }

    protected abstract Item asItem();
}
