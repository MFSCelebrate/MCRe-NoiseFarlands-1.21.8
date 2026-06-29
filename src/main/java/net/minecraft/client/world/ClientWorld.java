/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.DataCache;
import net.minecraft.client.world.WorldEventHandler;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EntityList;
import net.minecraft.world.GameMode;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.EmptyTickSchedulers;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientWorld
extends World
implements DataCache.CacheContext<ClientWorld> {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public Text QUITTING_MULTIPLAYER_TEXT = Text.translatable("multiplayer.status.quitting");
    final static private double PARTICLE_Y_OFFSET = 0.05;
    final static private int field_34805 = 10;
    final static private int field_34806 = 1000;
    final EntityList entityList = new EntityList();
    final private ClientEntityManager<Entity> entityManager = new ClientEntityManager<Entity>(Entity.class, new ClientEntityHandler());
    final private ClientPlayNetworkHandler networkHandler;
    final private WorldRenderer worldRenderer;
    final private WorldEventHandler worldEventHandler;
    final private Properties clientWorldProperties;
    final private DimensionEffects dimensionEffects;
    final private TickManager tickManager;
    final private MinecraftClient client = MinecraftClient.getInstance();
    final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
    final List<EnderDragonPart> enderDragonParts = Lists.newArrayList();
    final private Map<MapIdComponent, MapState> mapStates = Maps.newHashMap();
    final static private int field_32640 = -1;
    private int lightningTicksLeft;
    final private Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = Util.make(new Object2ObjectArrayMap(3), map -> {
        map.put((Object)BiomeColors.GRASS_COLOR, (Object)new BiomeColorCache(pos -> this.calculateColor((BlockPos)pos, BiomeColors.GRASS_COLOR)));
        map.put((Object)BiomeColors.FOLIAGE_COLOR, (Object)new BiomeColorCache(pos -> this.calculateColor((BlockPos)pos, BiomeColors.FOLIAGE_COLOR)));
        map.put((Object)BiomeColors.DRY_FOLIAGE_COLOR, (Object)new BiomeColorCache(pos -> this.calculateColor((BlockPos)pos, BiomeColors.DRY_FOLIAGE_COLOR)));
        map.put((Object)BiomeColors.WATER_COLOR, (Object)new BiomeColorCache(pos -> this.calculateColor((BlockPos)pos, BiomeColors.WATER_COLOR)));
    });
    final private ClientChunkManager chunkManager;
    final private Deque<Runnable> chunkUpdaters = Queues.newArrayDeque();
    private int simulationDistance;
    final private PendingUpdateManager pendingUpdateManager = new PendingUpdateManager();
    final private Set<BlockEntity> blockEntities = new ReferenceOpenHashSet();
    final private int seaLevel;
    private boolean shouldTickTimeOfDay;
    final static private Set<Item> BLOCK_MARKER_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);

    public void handlePlayerActionResponse(int sequence) {
        this.pendingUpdateManager.processPendingUpdates(sequence, this);
    }

    @Override
    public void loadBlockEntity(BlockEntity blockEntity) {
        BlockEntityRenderer<BlockEntity> blockEntityRenderer = this.client.getBlockEntityRenderDispatcher().get(blockEntity);
        if (blockEntityRenderer != null && blockEntityRenderer.rendersOutsideBoundingBox()) {
            this.blockEntities.add(blockEntity);
        }
    }

    public Set<BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void handleBlockUpdate(BlockPos pos, BlockState state, int flags) {
        if (!this.pendingUpdateManager.hasPendingUpdate(pos, state)) {
            super.setBlockState(pos, state, flags, 512);
        }
    }

    public void processPendingUpdate(BlockPos pos, BlockState state, Vec3d playerPos) {
        BlockState blockState = this.getBlockState(pos);
        if (blockState != state) {
            this.setBlockState(pos, state, Block.NOTIFY_ALL | Block.FORCE_STATE);
            ClientPlayerEntity playerEntity = this.client.player;
            if (this == playerEntity.net_minecraft_world_World_getWorld() && playerEntity.collidesWithStateAtPos(pos, state)) {
                playerEntity.updatePosition(playerPos.x, playerPos.y, playerPos.z);
            }
        }
    }

    PendingUpdateManager getPendingUpdateManager() {
        return this.pendingUpdateManager;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (this.pendingUpdateManager.hasPendingSequence()) {
            BlockState blockState = this.getBlockState(pos);
            boolean bl = super.setBlockState(pos, state, flags, maxUpdateDepth);
            if (bl) {
                this.pendingUpdateManager.addPendingUpdate(pos, blockState, this.client.player);
            }
            return bl;
        }
        return super.setBlockState(pos, state, flags, maxUpdateDepth);
    }

    public ClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionType, int loadDistance, int simulationDistance, WorldRenderer worldRenderer, boolean debugWorld, long seed, int seaLevel) {
        super(properties, registryRef, networkHandler.getRegistryManager(), dimensionType, true, debugWorld, seed, 1000000);
        this.networkHandler = networkHandler;
        this.chunkManager = new ClientChunkManager(this, loadDistance);
        this.tickManager = new TickManager();
        this.clientWorldProperties = properties;
        this.worldRenderer = worldRenderer;
        this.seaLevel = seaLevel;
        this.worldEventHandler = new WorldEventHandler(this.client, this, worldRenderer);
        this.dimensionEffects = DimensionEffects.byDimensionType(dimensionType.value());
        this.setSpawnPos(new BlockPos(8, 64, 8), 0.0f);
        this.simulationDistance = simulationDistance;
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
    }

    public void enqueueChunkUpdate(Runnable updater) {
        this.chunkUpdaters.add(updater);
    }

    public void runQueuedChunkUpdates() {
        Runnable runnable;
        int i = this.chunkUpdaters.size();
        int j = i < 1000 ? Math.max(10, i / 10) : i;
        for (int k = 0; k < j && (runnable = this.chunkUpdaters.poll()) != null; ++k) {
            runnable.run();
        }
    }

    public DimensionEffects getDimensionEffects() {
        return this.dimensionEffects;
    }

    public void tick(BooleanSupplier shouldKeepTicking) {
        this.getWorldBorder().tick();
        this.calculateAmbientDarkness();
        if (this.getTickManager().shouldTick()) {
            this.tickTime();
        }
        if (this.lightningTicksLeft > 0) {
            this.setLightningTicksLeft(this.lightningTicksLeft - 1);
        }
        ScopedProfiler scopedProfiler = Profilers.get().scoped("blocks");
        try {
            this.chunkManager.tick(shouldKeepTicking, true);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    private void tickTime() {
        this.clientWorldProperties.setTime(this.clientWorldProperties.getTime() + 1L);
        if (this.shouldTickTimeOfDay) {
            this.clientWorldProperties.setTimeOfDay(this.clientWorldProperties.getTimeOfDay() + 1L);
        }
    }

    public void setTime(long time, long timeOfDay, boolean shouldTickTimeOfDay) {
        this.clientWorldProperties.setTime(time);
        this.clientWorldProperties.setTimeOfDay(timeOfDay);
        this.shouldTickTimeOfDay = shouldTickTimeOfDay;
    }

    public Iterable<Entity> getEntities() {
        return this.getEntityLookup().iterate();
    }

    public void tickEntities() {
        Profiler profiler = Profilers.get();
        profiler.push("entities");
        this.entityList.forEach(entity -> {
            if (entity.isRemoved() || entity.hasVehicle() || this.tickManager.shouldSkipTick((Entity)entity)) {
                return;
            }
            this.tickEntity(this::tickEntity, entity);
        });
        profiler.pop();
        this.tickBlockEntities();
    }

    public boolean hasEntity(Entity entity) {
        return this.entityList.has(entity);
    }

    @Override
    public boolean shouldUpdatePostDeath(Entity entity) {
        return entity.getChunkPos().getChebyshevDistance(this.client.player.getChunkPos()) <= this.simulationDistance;
    }

    public void tickEntity(Entity entity) {
        entity.resetPosition();
        ++entity.age;
        Profilers.get().push(() -> Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        entity.tick();
        Profilers.get().pop();
        for (Entity entity2 : entity.getPassengerList()) {
            this.tickPassenger(entity, entity2);
        }
    }

    private void tickPassenger(Entity entity, Entity passenger) {
        if (passenger.isRemoved() || passenger.getVehicle() != entity) {
            passenger.stopRiding();
            return;
        }
        if (!(passenger instanceof PlayerEntity) && !this.entityList.has(passenger)) {
            return;
        }
        passenger.resetPosition();
        ++passenger.age;
        passenger.tickRiding();
        for (Entity entity2 : passenger.getPassengerList()) {
            this.tickPassenger(passenger, entity2);
        }
    }

    public void unloadBlockEntities(WorldChunk chunk) {
        chunk.clear();
        this.chunkManager.net_minecraft_world_chunk_light_LightingProvider_getLightingProvider().setColumnEnabled(chunk.getPos(), false);
        this.entityManager.stopTicking(chunk.getPos());
    }

    public void resetChunkColor(ChunkPos chunkPos) {
        this.colorCache.forEach((resolver, cache) -> cache.reset(chunkPos.x, chunkPos.z));
        this.entityManager.startTicking(chunkPos);
    }

    public void onChunkUnload(long sectionPos) {
        this.worldRenderer.onChunkUnload(sectionPos);
    }

    public void reloadColor() {
        this.colorCache.forEach((resolver, cache) -> cache.reset());
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return true;
    }

    public int getRegularEntityCount() {
        return this.entityManager.getEntityCount();
    }

    public void addEntity(Entity entity) {
        this.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        this.entityManager.addEntity(entity);
    }

    public void removeEntity(int entityId, Entity.RemovalReason removalReason) {
        Entity entity = this.getEntityLookup().get(entityId);
        if (entity != null) {
            entity.setRemoved(removalReason);
            entity.onRemoved();
        }
    }

    @Override
    public List<Entity> getCrammedEntities(Entity entity, Box box) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        if (clientPlayerEntity != null && clientPlayerEntity != entity && clientPlayerEntity.getBoundingBox().intersects(box) && EntityPredicates.canBePushedBy(entity).test(clientPlayerEntity)) {
            return List.of(clientPlayerEntity);
        }
        return List.of();
    }

    @Override
    @Nullable
    public Entity getEntityById(int id) {
        return this.getEntityLookup().get(id);
    }

    public void disconnect(Text reasonText) {
        this.networkHandler.getConnection().disconnect(reasonText);
    }

    public void doRandomBlockDisplayTicks(int centerX, int centerY, int centerZ) {
        int i = 32;
        Random random = Random.create();
        Block block = this.getBlockParticle();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = 0; j < 667; ++j) {
            this.randomBlockDisplayTick(centerX, centerY, centerZ, 16, random, block, mutable);
            this.randomBlockDisplayTick(centerX, centerY, centerZ, 32, random, block, mutable);
        }
    }

    @Nullable
    private Block getBlockParticle() {
        ItemStack itemStack;
        Item item;
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE && BLOCK_MARKER_ITEMS.contains(item = (itemStack = this.client.player.getMainHandStack()).getItem()) && item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            return blockItem.getBlock();
        }
        return null;
    }

    public void randomBlockDisplayTick(int centerX, int centerY, int centerZ, int radius, Random random, @Nullable Block block, BlockPos.Mutable pos) {
        int i = centerX + this.random.nextInt(radius) - this.random.nextInt(radius);
        int j = centerY + this.random.nextInt(radius) - this.random.nextInt(radius);
        int k = centerZ + this.random.nextInt(radius) - this.random.nextInt(radius);
        pos.set(i, j, k);
        BlockState blockState = this.getBlockState(pos);
        blockState.getBlock().randomDisplayTick(blockState, this, pos, random);
        FluidState fluidState = this.getFluidState(pos);
        if (!fluidState.isEmpty()) {
            fluidState.randomDisplayTick(this, pos, random);
            ParticleEffect particleEffect = fluidState.getParticle();
            if (particleEffect != null && this.random.nextInt(10) == 0) {
                boolean bl = blockState.isSideSolidFullSquare(this, pos, Direction.DOWN);
                Vec3i blockPos = pos.net_minecraft_util_math_Vec3i_down();
                this.addParticle((BlockPos)blockPos, this.getBlockState((BlockPos)blockPos), particleEffect, bl);
            }
        }
        if (block == blockState.getBlock()) {
            this.addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, blockState), (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, 0.0, 0.0, 0.0);
        }
        if (!blockState.isFullCube(this, pos)) {
            this.getBiome(pos).value().getParticleConfig().ifPresent(config -> {
                if (config.shouldAddParticle(this.random)) {
                    this.addParticleClient(config.getParticle(), (double)pos.getX() + this.random.nextDouble(), (double)pos.getY() + this.random.nextDouble(), (double)pos.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
                }
            });
        }
    }

    private void addParticle(BlockPos pos, BlockState state, ParticleEffect parameters, boolean solidBelow) {
        if (!state.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape voxelShape = state.getCollisionShape(this, pos);
        double d = voxelShape.getMax(Direction.Axis.Y);
        if (d < 1.0) {
            if (solidBelow) {
                this.addParticle(pos.getX(), pos.getX() + 1, pos.getZ(), pos.getZ() + 1, (double)(pos.getY() + 1) - 0.05, parameters);
            }
        } else if (!state.isIn(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMin(Direction.Axis.Y);
            if (e > 0.0) {
                this.addParticle(pos, parameters, voxelShape, (double)pos.getY() + e - 0.05);
            } else {
                BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_down();
                BlockState blockState = this.getBlockState(blockPos);
                VoxelShape voxelShape2 = blockState.getCollisionShape(this, blockPos);
                double f = voxelShape2.getMax(Direction.Axis.Y);
                if (f < 1.0 && blockState.getFluidState().isEmpty()) {
                    this.addParticle(pos, parameters, voxelShape, (double)pos.getY() - 0.05);
                }
            }
        }
    }

    private void addParticle(BlockPos pos, ParticleEffect parameters, VoxelShape shape, double y) {
        this.addParticle((double)pos.getX() + shape.getMin(Direction.Axis.X), (double)pos.getX() + shape.getMax(Direction.Axis.X), (double)pos.getZ() + shape.getMin(Direction.Axis.Z), (double)pos.getZ() + shape.getMax(Direction.Axis.Z), y, parameters);
    }

    private void addParticle(double minX, double maxX, double minZ, double maxZ, double y, ParticleEffect parameters) {
        this.addParticleClient(parameters, MathHelper.lerp(this.random.nextDouble(), minX, maxX), y, MathHelper.lerp(this.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
    }

    @Override
    public CrashReportSection addDetailsToCrashReport(CrashReport report) {
        CrashReportSection crashReportSection = super.addDetailsToCrashReport(report);
        crashReportSection.add("Server brand", () -> this.client.player.networkHandler.getBrand());
        crashReportSection.add("Server type", () -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        crashReportSection.add("Tracked entity count", () -> String.valueOf(this.getRegularEntityCount()));
        return crashReportSection;
    }

    @Override
    public void playSound(@Nullable Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
        if (source == this.client.player) {
            this.playSound(x, y, z, sound.value(), category, volume, pitch, false, seed);
        }
    }

    @Override
    public void playSoundFromEntity(@Nullable Entity source, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
        if (source == this.client.player) {
            this.client.getSoundManager().play(new EntityTrackingSoundInstance(sound.value(), category, volume, pitch, entity, seed));
        }
    }

    @Override
    public void playSoundFromEntityClient(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.client.getSoundManager().play(new EntityTrackingSoundInstance(sound, category, volume, pitch, entity, this.random.nextLong()));
    }

    @Override
    public void playSoundClient(SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (this.client.player != null) {
            this.client.getSoundManager().play(new EntityTrackingSoundInstance(sound, category, volume, pitch, this.client.player, this.random.nextLong()));
        }
    }

    @Override
    public void playSoundClient(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        this.playSound(x, y, z, sound, category, volume, pitch, useDistance, this.random.nextLong());
    }

    private void playSound(double x, double y, double z, SoundEvent event, SoundCategory category, float volume, float pitch, boolean useDistance, long seed) {
        double d = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
        PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(event, category, volume, pitch, Random.create(seed), x, y, z);
        if (useDistance && d > 100.0) {
            double e = Math.sqrt(d) / 40.0;
            this.client.getSoundManager().play(positionedSoundInstance, (int)(e * 20.0));
        } else {
            this.client.getSoundManager().play(positionedSoundInstance);
        }
    }

    @Override
    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, List<FireworkExplosionComponent> explosions) {
        if (explosions.isEmpty()) {
            for (int i = 0; i < this.random.nextInt(3) + 2; ++i) {
                this.addParticleClient(ParticleTypes.POOF, x, y, z, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
            }
        } else {
            this.client.particleManager.addParticle(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, explosions));
        }
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        this.networkHandler.sendPacket(packet);
    }

    @Override
    public RecipeManager net_minecraft_recipe_RecipeManager_getRecipeManager() {
        return this.networkHandler.getRecipeManager();
    }

    @Override
    public TickManager getTickManager() {
        return this.tickManager;
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return EmptyTickSchedulers.getClientTickScheduler();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return EmptyTickSchedulers.getClientTickScheduler();
    }

    @Override
    public ClientChunkManager net_minecraft_client_world_ClientChunkManager_getChunkManager() {
        return this.chunkManager;
    }

    @Override
    @Nullable
    public MapState getMapState(MapIdComponent id) {
        return this.mapStates.get(id);
    }

    public void putClientsideMapState(MapIdComponent id, MapState state) {
        this.mapStates.put(id, state);
    }

    @Override
    public Scoreboard net_minecraft_scoreboard_Scoreboard_getScoreboard() {
        return this.networkHandler.getScoreboard();
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.worldRenderer.updateBlock(this, pos, oldState, newState, flags);
    }

    @Override
    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
        this.worldRenderer.scheduleBlockRerenderIfNeeded(pos, old, updated);
    }

    public void scheduleBlockRenders(int x, int y, int z) {
        this.worldRenderer.scheduleChunkRenders3x3x3(x, y, z);
    }

    public void scheduleChunkRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.worldRenderer.scheduleChunkRenders(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        this.worldRenderer.setBlockBreakingInfo(entityId, pos, progress);
    }

    @Override
    public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
        this.worldEventHandler.processGlobalEvent(eventId, pos, data);
    }

    @Override
    public void syncWorldEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {
        try {
            this.worldEventHandler.processWorldEvent(eventId, pos, data);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Playing level event");
            CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
            crashReportSection.add("Block coordinates", CrashReportSection.createPositionString(this, pos));
            crashReportSection.add("Event source", source);
            crashReportSection.add("Event type", eventId);
            crashReportSection.add("Event data", data);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void addParticleClient(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void addParticleClient(ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || force, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void addImportantParticleClient(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, false, true, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void addImportantParticleClient(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || force, true, x, y, z, velocityX, velocityY, velocityZ);
    }

    public List<AbstractClientPlayerEntity> getPlayers() {
        return this.players;
    }

    public List<EnderDragonPart> getEnderDragonParts() {
        return this.enderDragonParts;
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS);
    }

    public float getSkyBrightness(float tickProgress) {
        float f = this.getSkyAngle(tickProgress);
        float g = 1.0f - (MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.2f);
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = 1.0f - g;
        g *= 1.0f - this.getRainGradient(tickProgress) * 5.0f / 16.0f;
        return (g *= 1.0f - this.getThunderGradient(tickProgress) * 5.0f / 16.0f) * 0.8f + 0.2f;
    }

    public int getSkyColor(Vec3d cameraPos, float tickProgress) {
        int o;
        float k;
        float j;
        float f = this.getSkyAngle(tickProgress);
        Vec3d vec3d = cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25);
        Vec3d vec3d2 = CubicSampler.sampleColor(vec3d, (x, y, z) -> Vec3d.unpackRgb(this.getBiomeAccess().getBiomeForNoiseGen(x, y, z).value().getSkyColor()));
        float g = MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        vec3d2 = vec3d2.multiply(g);
        int i = ColorHelper.getArgb(vec3d2);
        float h = this.getRainGradient(tickProgress);
        if (h > 0.0f) {
            j = 0.6f;
            k = h * 0.75f;
            int l = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.6f);
            i = ColorHelper.lerp(k, i, l);
        }
        if ((j = this.getThunderGradient(tickProgress)) > 0.0f) {
            k = 0.2f;
            float m = j * 0.75f;
            int n = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.2f);
            i = ColorHelper.lerp(m, i, n);
        }
        if ((o = this.getLightningTicksLeft()) > 0) {
            float m = Math.min((float)o - tickProgress, 1.0f);
            i = ColorHelper.lerp(m *= 0.45f, i, ColorHelper.getArgb(204, 204, 255));
        }
        return i;
    }

    public int getCloudsColor(float tickProgress) {
        int i = Colors.WHITE;
        float f = this.getRainGradient(tickProgress);
        if (f > 0.0f) {
            int j = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.6f);
            i = ColorHelper.lerp(f * 0.95f, i, j);
        }
        float g = this.getSkyAngle(tickProgress);
        float h = MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        i = ColorHelper.mix(i, ColorHelper.fromFloats(1.0f, h * 0.9f + 0.1f, h * 0.9f + 0.1f, h * 0.85f + 0.15f));
        float k = this.getThunderGradient(tickProgress);
        if (k > 0.0f) {
            int l = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.2f);
            i = ColorHelper.lerp(k * 0.95f, i, l);
        }
        return i;
    }

    public float getStarBrightness(float tickProgress) {
        float f = this.getSkyAngle(tickProgress);
        float g = 1.0f - (MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        return g * g * 0.5f;
    }

    public int getLightningTicksLeft() {
        return this.client.options.getHideLightningFlashes().getValue() != false ? 0 : this.lightningTicksLeft;
    }

    @Override
    public void setLightningTicksLeft(int lightningTicksLeft) {
        this.lightningTicksLeft = lightningTicksLeft;
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        boolean bl = this.getDimensionEffects().isDarkened();
        if (!shaded) {
            return bl ? 0.9f : 1.0f;
        }
        switch (direction) {
            case DOWN: {
                return bl ? 0.9f : 0.5f;
            }
            case UP: {
                return bl ? 0.9f : 1.0f;
            }
            case NORTH: 
            case SOUTH: {
                return 0.8f;
            }
            case WEST: 
            case EAST: {
                return 0.6f;
            }
        }
        return 1.0f;
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        BiomeColorCache biomeColorCache = (BiomeColorCache)this.colorCache.get((Object)colorResolver);
        return biomeColorCache.getBiomeColor(pos);
    }

    public int calculateColor(BlockPos pos, ColorResolver colorResolver) {
        int i = MinecraftClient.getInstance().options.getBiomeBlendRadius().getValue();
        if (1 == 0) {
            return colorResolver.getColor(this.getBiome(pos).value(), pos.getX(), pos.getZ());
        }
        int j = 9;
        int k = 0;
        int l = 0;
        int m = 0;
        CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        while (cuboidBlockIterator.step()) {
            mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
            int n = colorResolver.getColor(this.getBiome(mutable).value(), mutable.getX(), mutable.getZ());
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
        }
        return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
    }

    public void setSpawnPos(BlockPos pos, float angle) {
        this.properties.setSpawnPos(pos, angle);
    }

    public String toString() {
        return "ClientLevel";
    }

    @Override
    public Properties net_minecraft_client_world_ClientWorld$Properties_getLevelProperties() {
        return this.clientWorldProperties;
    }

    @Override
    public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter) {
    }

    protected Map<MapIdComponent, MapState> getMapStates() {
        return ImmutableMap.copyOf(this.mapStates);
    }

    protected void putMapStates(Map<MapIdComponent, MapState> mapStates) {
        this.mapStates.putAll(mapStates);
    }

    @Override
    protected EntityLookup<Entity> getEntityLookup() {
        return this.entityManager.getLookup();
    }

    @Override
    public String asString() {
        return "Chunks[C] W: " + this.chunkManager.getDebugString() + " E: " + this.entityManager.getDebugString();
    }

    @Override
    public void addBlockBreakParticles(BlockPos pos, BlockState state) {
        this.client.particleManager.addBlockBreakParticles(pos, state);
    }

    public void setSimulationDistance(int simulationDistance) {
        this.simulationDistance = simulationDistance;
    }

    public int getSimulationDistance() {
        return this.simulationDistance;
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return this.networkHandler.getEnabledFeatures();
    }

    @Override
    public BrewingRecipeRegistry getBrewingRecipeRegistry() {
        return this.networkHandler.getBrewingRecipeRegistry();
    }

    @Override
    public FuelRegistry getFuelRegistry() {
        return this.networkHandler.getFuelRegistry();
    }

    @Override
    public void createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry<SoundEvent> soundEvent) {
    }

    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Override
    public int getBlockColor(BlockPos pos) {
        return MinecraftClient.getInstance().getBlockColors().getColor(this.getBlockState(pos), this, pos, 0);
    }

    @Override
    public void registerForCleaning(DataCache<ClientWorld, ?> dataCache) {
        this.networkHandler.registerForCleaning(dataCache);
    }

    @Override
    public WorldProperties net_minecraft_world_WorldProperties_getLevelProperties() {
        return this.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties();
    }

    public Collection getEnderDragonParts() {
        return this.getEnderDragonParts();
    }

    @Override
    public ChunkManager net_minecraft_world_chunk_ChunkManager_getChunkManager() {
        return this.net_minecraft_client_world_ClientChunkManager_getChunkManager();
    }

    @Environment(value=EnvType.CLIENT)
    final class ClientEntityHandler
    implements EntityHandler<Entity> {
        ClientEntityHandler() {
        }

        @Override
        public void create(Entity entity) {
        }

        @Override
        public void destroy(Entity entity) {
        }

        @Override
        public void startTicking(Entity entity) {
            ClientWorld.this.entityList.add(entity);
        }

        @Override
        public void stopTicking(Entity entity) {
            ClientWorld.this.entityList.remove(entity);
        }

        @Override
        public void startTracking(Entity entity) {
            Entity entity2 = entity;
            Objects.requireNonNull(entity2);
            Entity entity3 = entity2;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayerEntity.class, EnderDragonEntity.class}, (Object)entity3, n)) {
                case 0: {
                    AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity3;
                    ClientWorld.this.players.add(abstractClientPlayerEntity);
                    break;
                }
                case 1: {
                    EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity3;
                    ClientWorld.this.enderDragonParts.addAll(Arrays.asList(enderDragonEntity.getBodyParts()));
                    break;
                }
            }
        }

        @Override
        public void stopTracking(Entity entity) {
            entity.detach();
            Entity entity2 = entity;
            Objects.requireNonNull(entity2);
            Entity entity3 = entity2;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayerEntity.class, EnderDragonEntity.class}, (Object)entity3, n)) {
                case 0: {
                    AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity3;
                    ClientWorld.this.players.remove(abstractClientPlayerEntity);
                    break;
                }
                case 1: {
                    EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity3;
                    ClientWorld.this.enderDragonParts.removeAll(Arrays.asList(enderDragonEntity.getBodyParts()));
                    break;
                }
            }
        }

        @Override
        public void updateLoadStatus(Entity entity) {
        }

        @Override
        public void updateLoadStatus(Object entity) {
            this.updateLoadStatus((Entity)entity);
        }

        @Override
        public void stopTracking(Object entity) {
            this.stopTracking((Entity)entity);
        }

        @Override
        public void startTracking(Object entity) {
            this.startTracking((Entity)entity);
        }

        @Override
        public void startTicking(Object entity) {
            this.startTicking((Entity)entity);
        }

        @Override
        public void destroy(Object entity) {
            this.destroy((Entity)entity);
        }

        @Override
        public void create(Object entity) {
            this.create((Entity)entity);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Properties
    implements MutableWorldProperties {
        final private boolean hardcore;
        final private boolean flatWorld;
        private BlockPos spawnPos;
        private float spawnAngle;
        private long time;
        private long timeOfDay;
        private boolean raining;
        private Difficulty difficulty;
        private boolean difficultyLocked;

        public Properties(Difficulty difficulty, boolean hardcore, boolean flatWorld) {
            this.difficulty = difficulty;
            this.hardcore = hardcore;
            this.flatWorld = flatWorld;
        }

        @Override
        public BlockPos getSpawnPos() {
            return this.spawnPos;
        }

        @Override
        public float getSpawnAngle() {
            return this.spawnAngle;
        }

        @Override
        public long getTime() {
            return this.time;
        }

        @Override
        public long getTimeOfDay() {
            return this.timeOfDay;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setTimeOfDay(long timeOfDay) {
            this.timeOfDay = timeOfDay;
        }

        @Override
        public void setSpawnPos(BlockPos pos, float angle) {
            this.spawnPos = pos.toImmutable();
            this.spawnAngle = angle;
        }

        @Override
        public boolean isThundering() {
            return false;
        }

        @Override
        public boolean isRaining() {
            return this.raining;
        }

        @Override
        public void setRaining(boolean raining) {
            this.raining = raining;
        }

        @Override
        public boolean isHardcore() {
            return this.hardcore;
        }

        @Override
        public Difficulty getDifficulty() {
            return this.difficulty;
        }

        @Override
        public boolean isDifficultyLocked() {
            return this.difficultyLocked;
        }

        @Override
        public void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
            MutableWorldProperties.super.populateCrashReport(reportSection, world);
        }

        public void setDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        public void setDifficultyLocked(boolean difficultyLocked) {
            this.difficultyLocked = difficultyLocked;
        }

        public double getSkyDarknessHeight(HeightLimitView world) {
            if (this.flatWorld) {
                return world.getBottomY();
            }
            return 63.0;
        }

        public float getVoidDarknessRange() {
            if (this.flatWorld) {
                return 1.0f;
            }
            return 32.0f;
        }
    }
}

