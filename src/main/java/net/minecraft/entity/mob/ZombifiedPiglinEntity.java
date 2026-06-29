/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ZombifiedPiglinEntity
extends ZombieEntity
implements Angerable {
    final static private EntityDimensions BABY_BASE_DIMENSIONS = EntityType.ZOMBIFIED_PIGLIN.getDimensions().scaled(0.5f).withEyeHeight(0.97f);
    final static private Identifier ATTACKING_SPEED_MODIFIER_ID = Identifier.ofVanilla("attacking");
    final static private EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_MODIFIER_ID, 0.05, EntityAttributeModifier.Operation.ADD_VALUE);
    final static private UniformIntProvider ANGRY_SOUND_DELAY_RANGE = TimeHelper.betweenSeconds(0, 1);
    private int angrySoundDelay;
    final static private UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    private int angerTime;
    @Nullable
    private UUID angryAt;
    final static private int field_30524 = 10;
    final static private UniformIntProvider ANGER_PASSING_COOLDOWN_RANGE = TimeHelper.betweenSeconds(4, 6);
    private int angerPassingCooldown;

    public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> entityType, World world) {
        super((EntityType<? extends ZombieEntity>)entityType, world);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new UniversalAngerGoal<ZombifiedPiglinEntity>(this, true));
    }

    public static DefaultAttributeContainer.Builder createZombifiedPiglinAttributes() {
        return ZombieEntity.createZombieAttributes().add(EntityAttributes.SPAWN_REINFORCEMENTS, 0.0).add(EntityAttributes.MOVEMENT_SPEED, 0.23f).add(EntityAttributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
    }

    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    @Override
    protected void mobTick(ServerWorld world) {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (this.hasAngerTime()) {
            if (!this.isBaby() && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_MODIFIER_ID)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
            this.tickAngrySound();
        } else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_MODIFIER_ID)) {
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_MODIFIER_ID);
        }
        this.tickAngerLogic(world, true);
        if (this.getTarget() != null) {
            this.tickAngerPassing();
        }
        super.mobTick(world);
    }

    private void tickAngrySound() {
        if (this.angrySoundDelay > 0) {
            --this.angrySoundDelay;
            if (this.angrySoundDelay == 0) {
                this.playAngrySound();
            }
        }
    }

    private void tickAngerPassing() {
        if (this.angerPassingCooldown > 0) {
            --this.angerPassingCooldown;
            return;
        }
        if (this.getVisibilityCache().canSee(this.getTarget())) {
            this.angerNearbyZombifiedPiglins();
        }
        this.angerPassingCooldown = ANGER_PASSING_COOLDOWN_RANGE.get(this.random);
    }

    private void angerNearbyZombifiedPiglins() {
        double d = this.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
        Box box = Box.from(this.getPos()).expand(d, 10.0, d);
        this.net_minecraft_world_World_getWorld().getEntitiesByClass(ZombifiedPiglinEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR).stream().filter(zombifiedPiglin -> zombifiedPiglin != this).filter(zombifiedPiglin -> zombifiedPiglin.getTarget() == null).filter(zombifiedPiglin -> !zombifiedPiglin.isTeammate(this.getTarget())).forEach(zombifiedPiglin -> zombifiedPiglin.setTarget(this.getTarget()));
    }

    private void playAngrySound() {
        this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0f, this.getSoundPitch() * 1.8f);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.getTarget() == null && target != null) {
            this.angrySoundDelay = ANGRY_SOUND_DELAY_RANGE.get(this.random);
            this.angerPassingCooldown = ANGER_PASSING_COOLDOWN_RANGE.get(this.random);
        }
        super.setTarget(target);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    public static boolean canSpawn(EntityType<ZombifiedPiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && !world.getBlockState(pos.net_minecraft_util_math_BlockPos_down()).isOf(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return world.doesNotIntersectEntities(this) && !world.containsFluid(this.getBoundingBox());
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        this.writeAngerToData(view);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.readAngerFromData(this.net_minecraft_world_World_getWorld(), view);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasAngerTime() ? SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).setBaseValue(0.0);
    }

    @Override
    @Nullable
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public boolean isAngryAt(ServerWorld world, PlayerEntity player) {
        return this.shouldAngerAt(player, world);
    }

    @Override
    public boolean canGather(ServerWorld world, ItemStack stack) {
        return this.canPickupItem(stack);
    }
}

