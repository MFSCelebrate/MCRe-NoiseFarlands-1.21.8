/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.projectile.thrown;

import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderPearlEntity extends ThrownItemEntity {
    private long chunkTicketExpiryTicks = 0L;

    public EnderPearlEntity(EntityType<? extends EnderPearlEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>) entityType, world);
    }

    public EnderPearlEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityType.ENDER_PEARL, owner, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void setOwner(@Nullable LazyEntityReference<Entity> owner) {
        this.removeFromOwner();
        super.setOwner(owner);
        this.addToOwner();
    }

    private void removeFromOwner() {
        Entity entity = this.net_minecraft_entity_Entity_getOwner();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            serverPlayerEntity.removeEnderPearl(this);
        }
    }

    private void addToOwner() {
        Entity entity = this.net_minecraft_entity_Entity_getOwner();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            serverPlayerEntity.addEnderPearl(this);
        }
    }

    @Override
    @Nullable
    public Entity net_minecraft_entity_Entity_getOwner() {
        World world;
        if (this.owner == null || !((world = this.net_minecraft_world_World_getWorld())
                        instanceof ServerWorld)) {
            return super.net_minecraft_entity_Entity_getOwner();
        }
        ServerWorld serverWorld = (ServerWorld) world;
        return this.owner.resolve(uuid -> EnderPearlEntity.resolveOwner(serverWorld, uuid), Entity.class);
    }

    @Nullable
    private static Entity resolveOwner(ServerWorld world, UUID uuid) {
        Entity entity = world.net_minecraft_entity_Entity_getEntity(uuid);
        if (entity != null) {
            return entity;
        }
        for (ServerWorld serverWorld : world.getServer().getWorlds()) {
            if (serverWorld == world || (entity = serverWorld.net_minecraft_entity_Entity_getEntity(uuid)) == null)
                continue;
            return entity;
        }
        return null;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.net_minecraft_entity_Entity_getOwner()), 0.0f);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        ServerWorld serverWorld;
        block14:
        {
            block13:
            {
                super.onCollision(hitResult);
                for (int i = 0; i < 32; ++i) {
                    this.net_minecraft_world_World_getWorld().addParticleClient(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
                }
                World world = this.net_minecraft_world_World_getWorld();
                if (!(world instanceof ServerWorld)) break block13;
                serverWorld = (ServerWorld) world;
                if (!this.isRemoved()) break block14;
            }
            return;
        }
        Entity entity = this.net_minecraft_entity_Entity_getOwner();
        if (entity == null || !EnderPearlEntity.canTeleportEntityTo(entity, serverWorld)) {
            this.discard();
            return;
        }
        Vec3d vec3d = this.getLastRenderPos();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            if (serverPlayerEntity.networkHandler.isConnectionOpen()) {
                ServerPlayerEntity serverPlayerEntity2;
                EndermiteEntity endermiteEntity;
                if (this.random.nextFloat() < 0.05f && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && (endermiteEntity = EntityType.ENDERMITE.create(serverWorld, SpawnReason.TRIGGERED)) != null) {
                    endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                    serverWorld.spawnEntity(endermiteEntity);
                }
                if (this.hasPortalCooldown()) {
                    entity.resetPortalCooldown();
                }
                if ((serverPlayerEntity2 = serverPlayerEntity.net_minecraft_server_network_ServerPlayerEntity_teleportTo(new TeleportTarget(serverWorld, vec3d, Vec3d.ZERO, 0.0f, 0.0f, PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA), TeleportTarget.NO_OP))) != null) {
                    serverPlayerEntity2.onLanding();
                    serverPlayerEntity2.clearCurrentExplosion();
                    serverPlayerEntity2.damage(serverPlayerEntity.net_minecraft_server_world_ServerWorld_getWorld(), this.getDamageSources().enderPearl(), 5.0f);
                }
                this.playTeleportSound(serverWorld, vec3d);
            }
        } else {
            Entity entity2 = entity.net_minecraft_entity_Entity_teleportTo(new TeleportTarget(serverWorld, vec3d, entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
            if (entity2 != null) {
                entity2.onLanding();
            }
            this.playTeleportSound(serverWorld, vec3d);
        }
        this.discard();
    }

    private static boolean canTeleportEntityTo(Entity entity, World world) {
        if (entity.net_minecraft_world_World_getWorld().getRegistryKey() == world.getRegistryKey()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                return livingEntity.isAlive() && !livingEntity.isSleeping();
            }
            return entity.isAlive();
        }
        return entity.canUsePortals(true);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void tick() {
        int i = ChunkSectionPos.getSectionCoordFloored(this.getPos().getX());
        int j = ChunkSectionPos.getSectionCoordFloored(this.getPos().getZ());
        Entity entity = this.getOwner();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            if (!entity.isAlive() && serverPlayerEntity.getWorld().getGameRules().getBoolean(GameRules.ENDER_PEARLS_VANISH_ON_DEATH)) {
                this.discard();
            }
        }
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        BlockPos blockPos = BlockPos.ofFloored(this.getPos());
        if ((--this.chunkTicketExpiryTicks <= 0L || i != ChunkSectionPos.getSectionCoord(blockPos.getX()) || j != ChunkSectionPos.getSectionCoord(blockPos.getZ())) && entity
                        instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity) entity;
            this.chunkTicketExpiryTicks = serverPlayerEntity2.handleThrownEnderPearl(this);
        }
    }

    private void playTeleportSound(World world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
    }

    @Override
    @Nullable
    public Entity net_minecraft_entity_Entity_teleportTo(TeleportTarget teleportTarget) {
        Entity entity = super.net_minecraft_entity_Entity_teleportTo(teleportTarget);
        if (entity != null) {
            entity.addPortalChunkTicketAt(BlockPos.ofFloored(entity.getPos()));
        }
        return entity;
    }

    @Override
    public boolean canTeleportBetween(World from, World to) {
        Entity entity;
        if (from.getRegistryKey() == World.END && to.getRegistryKey() == World.OVERWORLD && (entity = this.net_minecraft_entity_Entity_getOwner())
                        instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            return super.canTeleportBetween(from, to) && serverPlayerEntity.seenCredits;
        }
        return super.canTeleportBetween(from, to);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        Entity entity;
        super.onBlockCollision(state);
        if (state.isOf(Blocks.END_GATEWAY) && (entity = this.net_minecraft_entity_Entity_getOwner())
                        instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            serverPlayerEntity.onBlockCollision(state);
        }
    }

    @Override
    public void onRemove(Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            this.removeFromOwner();
        }
        super.onRemove(reason);
    }

    @Override
    public void onBubbleColumnSurfaceCollision(boolean drag, BlockPos pos) {
        Entity.applyBubbleColumnSurfaceEffects(this, drag, pos);
    }

    @Override
    public void onBubbleColumnCollision(boolean drag) {
        Entity.applyBubbleColumnEffects(this, drag);
    }
}
