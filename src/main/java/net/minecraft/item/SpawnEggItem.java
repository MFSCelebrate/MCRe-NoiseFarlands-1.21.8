/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SpawnEggItem
extends Item {
    final static private Map<EntityType<? extends MobEntity>, SpawnEggItem> SPAWN_EGGS = Maps.newIdentityHashMap();
    final private EntityType<?> type;

    public SpawnEggItem(EntityType<? extends MobEntity> type, Item.Settings settings) {
        super(settings);
        this.type = type;
        SPAWN_EGGS.put(type, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        EntityType<?> entityType;
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof Spawner) {
            Spawner spawner = (Spawner)((Object)blockEntity);
            entityType = this.getEntityType(world.getRegistryManager(), itemStack);
            spawner.setEntityType(entityType, world.getRandom());
            world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
            world.emitGameEvent((Entity)context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }
        BlockPos blockPos2 = blockState.getCollisionShape(world, blockPos).isEmpty() ? blockPos : blockPos.net_minecraft_util_math_BlockPos_offset(direction);
        entityType = this.getEntityType(world.getRegistryManager(), itemStack);
        if (entityType.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
            itemStack.decrement(1);
            world.emitGameEvent((Entity)context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = SpawnEggItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        BlockHitResult blockHitResult2 = blockHitResult;
        BlockPos blockPos = blockHitResult2.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return ActionResult.PASS;
        }
        if (!world.canEntityModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, blockHitResult2.getSide(), itemStack)) {
            return ActionResult.FAIL;
        }
        EntityType<?> entityType = this.getEntityType(serverWorld.getRegistryManager(), itemStack);
        Object entity = entityType.spawnFromItemStack(serverWorld, itemStack, user, blockPos, SpawnReason.SPAWN_ITEM_USE, false, false);
        if (entity == null) {
            return ActionResult.PASS;
        }
        itemStack.decrementUnlessCreative(1, user);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        world.emitGameEvent((Entity)user, GameEvent.ENTITY_PLACE, ((Entity)entity).getPos());
        return ActionResult.SUCCESS;
    }

    public boolean isOfSameEntityType(RegistryWrapper.WrapperLookup registries, ItemStack stack, EntityType<?> type) {
        return Objects.equals(this.getEntityType(registries, stack), type);
    }

    @Nullable
    public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
        return SPAWN_EGGS.get(type);
    }

    public static Iterable<SpawnEggItem> getAll() {
        return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
    }

    public EntityType<?> getEntityType(RegistryWrapper.WrapperLookup registries, ItemStack stack) {
        EntityType<?> entityType;
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        if (!nbtComponent.isEmpty() && (entityType = nbtComponent.getRegistryValueOfId(registries, RegistryKeys.ENTITY_TYPE)) != null) {
            return entityType;
        }
        return this.type;
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.type.getRequiredFeatures();
    }

    public Optional<MobEntity> spawnBaby(PlayerEntity user, MobEntity entity, EntityType<? extends MobEntity> entityType, ServerWorld world, Vec3d pos, ItemStack stack) {
        if (!this.isOfSameEntityType(world.getRegistryManager(), stack, entityType)) {
            return Optional.empty();
        }
        MobEntity mobEntity = entity instanceof PassiveEntity ? ((PassiveEntity)entity).net_minecraft_entity_passive_LlamaEntity_createChild(world, (PassiveEntity)entity) : entityType.create(world, SpawnReason.SPAWN_ITEM_USE);
        if (mobEntity == null) {
            return Optional.empty();
        }
        mobEntity.setBaby(true);
        if (!mobEntity.isBaby()) {
            return Optional.empty();
        }
        mobEntity.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f);
        mobEntity.copyComponentsFrom(stack);
        world.spawnEntityAndPassengers(mobEntity);
        stack.decrementUnlessCreative(1, user);
        return Optional.of(mobEntity);
    }

    @Override
    public boolean shouldShowOperatorBlockWarnings(ItemStack stack, @Nullable PlayerEntity player) {
        NbtComponent nbtComponent;
        if (player != null && player.getPermissionLevel() >= 2 && (nbtComponent = stack.get(DataComponentTypes.ENTITY_DATA)) != null) {
            EntityType<?> entityType = nbtComponent.getRegistryValueOfId(player.net_minecraft_world_World_getWorld().getRegistryManager(), RegistryKeys.ENTITY_TYPE);
            return entityType != null && entityType.canPotentiallyExecuteCommands();
        }
        return false;
    }
}

