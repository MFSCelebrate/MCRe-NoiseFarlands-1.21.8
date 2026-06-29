/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkLevelManager;
import net.minecraft.server.world.ChunkLevels;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.OptionalChunk;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnDensityCapper;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.NbtScannable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerChunkManager
extends ChunkManager {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private ChunkLevelManager levelManager;
    final private ServerWorld world;
    final Thread serverThread;
    final ServerLightingProvider lightingProvider;
    final private MainThreadExecutor mainThreadExecutor;
    final public ServerChunkLoadingManager chunkLoadingManager;
    final private PersistentStateManager persistentStateManager;
    final private ChunkTicketManager ticketManager;
    private long lastTickTime;
    private boolean spawnMonsters = true;
    private boolean spawnAnimals = true;
    final static private int CACHE_SIZE = 4;
    final private long[] chunkPosCache = new long[4];
    final private ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
    final private Chunk[] chunkCache = new Chunk[4];
    final private List<WorldChunk> spawningChunks = new ObjectArrayList();
    final private Set<ChunkHolder> chunksToBroadcastUpdate = new ReferenceOpenHashSet();
    @Nullable
    @Debug
    private SpawnHelper.Info spawnInfo;

    public ServerChunkManager(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor workerExecutor, ChunkGenerator chunkGenerator, int viewDistance, int simulationDistance, boolean dsync, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier<PersistentStateManager> persistentStateManagerFactory) {
        this.world = world;
        this.mainThreadExecutor = new MainThreadExecutor(world);
        this.serverThread = Thread.currentThread();
        Path path = session.getWorldDirectory(world.getRegistryKey()).resolve("data");
        try {
            PathUtil.createDirectories(path);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to create dimension data storage directory", (Throwable)iOException);
        }
        this.persistentStateManager = new PersistentStateManager(new PersistentState.Context(world), path, dataFixer, world.getRegistryManager());
        this.ticketManager = this.persistentStateManager.getOrCreate(ChunkTicketManager.STATE_TYPE);
        this.chunkLoadingManager = new ServerChunkLoadingManager(world, session, dataFixer, structureTemplateManager, workerExecutor, this.mainThreadExecutor, this, chunkGenerator, worldGenerationProgressListener, chunkStatusChangeListener, persistentStateManagerFactory, this.ticketManager, viewDistance, dsync);
        this.lightingProvider = this.chunkLoadingManager.getLightingProvider();
        this.levelManager = this.chunkLoadingManager.getLevelManager();
        this.levelManager.setSimulationDistance(simulationDistance);
        this.initChunkCaches();
    }

    @Override
    public ServerLightingProvider net_minecraft_server_world_ServerLightingProvider_getLightingProvider() {
        return this.lightingProvider;
    }

    @Nullable
    private ChunkHolder getChunkHolder(long pos) {
        return this.chunkLoadingManager.getChunkHolder(pos);
    }

    public int getTotalChunksLoadedCount() {
        return this.chunkLoadingManager.getTotalChunksLoadedCount();
    }

    private void putInCache(long pos, @Nullable Chunk chunk, ChunkStatus status) {
        for (int i = 3; i > 0; --i) {
            this.chunkPosCache[i] = this.chunkPosCache[i - 1];
            this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
            this.chunkCache[i] = this.chunkCache[i - 1];
        }
        this.chunkPosCache[0] = pos;
        this.chunkStatusCache[0] = status;
        this.chunkCache[0] = chunk;
    }

    @Override
    @Nullable
    public Chunk net_minecraft_world_chunk_Chunk_getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        if (Thread.currentThread() != this.serverThread) {
            return CompletableFuture.supplyAsync(() -> this.net_minecraft_world_chunk_Chunk_getChunk(x, z, leastStatus, create), this.mainThreadExecutor).join();
        }
        Profiler profiler = Profilers.get();
        profiler.visit("getChunk");
        long l = ChunkPos.toLong(x, z);
        for (int i = 0; i < 4; ++i) {
            Chunk chunk;
            if (l != this.chunkPosCache[i] || leastStatus != this.chunkStatusCache[i] || (chunk = this.chunkCache[i]) == null && create) continue;
            return chunk;
        }
        profiler.visit("getChunkCacheMiss");
        CompletableFuture<OptionalChunk<Chunk>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
        this.mainThreadExecutor.runTasks(completableFuture::isDone);
        OptionalChunk<Chunk> optionalChunk = completableFuture.join();
        Chunk chunk2 = optionalChunk.orElse(null);
        if (chunk2 == null && create) {
            throw Util.getFatalOrPause(new IllegalStateException("Chunk not there when requested: " + optionalChunk.getError()));
        }
        this.putInCache(l, chunk2, leastStatus);
        return chunk2;
    }

    @Override
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        if (Thread.currentThread() != this.serverThread) {
            return null;
        }
        Profilers.get().visit("getChunkNow");
        long l = ChunkPos.toLong(chunkX, chunkZ);
        for (int i = 0; i < 4; ++i) {
            if (l != this.chunkPosCache[i] || this.chunkStatusCache[i] != ChunkStatus.FULL) continue;
            Chunk chunk = this.chunkCache[i];
            return chunk instanceof WorldChunk ? (WorldChunk)chunk : null;
        }
        ChunkHolder chunkHolder = this.getChunkHolder(l);
        if (chunkHolder == null) {
            return null;
        }
        Chunk chunk = chunkHolder.getOrNull(ChunkStatus.FULL);
        if (chunk != null) {
            this.putInCache(l, chunk, ChunkStatus.FULL);
            if (chunk instanceof WorldChunk) {
                return (WorldChunk)chunk;
            }
        }
        return null;
    }

    private void initChunkCaches() {
        Arrays.fill(this.chunkPosCache, ChunkPos.MARKER);
        Arrays.fill(this.chunkStatusCache, null);
        Arrays.fill(this.chunkCache, null);
    }

    public CompletableFuture<OptionalChunk<Chunk>> getChunkFutureSyncOnMainThread(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        CompletionStage<OptionalChunk<Chunk>> completableFuture;
        boolean bl;
        boolean bl2 = bl = Thread.currentThread() == this.serverThread;
        if (bl) {
            completableFuture = this.getChunkFuture(chunkX, chunkZ, leastStatus, create);
            this.mainThreadExecutor.runTasks(() -> completableFuture.isDone());
        } else {
            completableFuture = CompletableFuture.supplyAsync(() -> this.getChunkFuture(chunkX, chunkZ, leastStatus, create), this.mainThreadExecutor).thenCompose(future -> future);
        }
        return completableFuture;
    }

    private CompletableFuture<OptionalChunk<Chunk>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        long l = chunkPos.toLong();
        int i = ChunkLevels.getLevelFromStatus(leastStatus);
        ChunkHolder chunkHolder = this.getChunkHolder(l);
        if (create) {
            this.addTicket(new ChunkTicket(ChunkTicketType.UNKNOWN, i), chunkPos);
            if (this.isMissingForLevel(chunkHolder, i)) {
                Profiler profiler = Profilers.get();
                profiler.push("chunkLoad");
                this.updateChunks();
                chunkHolder = this.getChunkHolder(l);
                profiler.pop();
                if (this.isMissingForLevel(chunkHolder, i)) {
                    throw Util.getFatalOrPause(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }
        if (this.isMissingForLevel(chunkHolder, i)) {
            return AbstractChunkHolder.UNLOADED_FUTURE;
        }
        return chunkHolder.load(leastStatus, this.chunkLoadingManager);
    }

    private boolean isMissingForLevel(@Nullable ChunkHolder holder, int maxLevel) {
        return holder == null || holder.getLevel() > maxLevel;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        int i;
        ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(x, z).toLong());
        return !this.isMissingForLevel(chunkHolder, i = ChunkLevels.getLevelFromStatus(ChunkStatus.FULL));
    }

    @Override
    @Nullable
    public LightSourceView getChunk(int chunkX, int chunkZ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
        ChunkHolder chunkHolder = this.getChunkHolder(l);
        if (chunkHolder == null) {
            return null;
        }
        return chunkHolder.getUncheckedOrNull(ChunkStatus.INITIALIZE_LIGHT.getPrevious());
    }

    @Override
    public World net_minecraft_world_World_getWorld() {
        return this.world;
    }

    public boolean executeQueuedTasks() {
        return this.mainThreadExecutor.runTask();
    }

    boolean updateChunks() {
        boolean bl = this.levelManager.update(this.chunkLoadingManager);
        boolean bl2 = this.chunkLoadingManager.updateHolderMap();
        this.chunkLoadingManager.updateChunks();
        if (bl || bl2) {
            this.initChunkCaches();
            return true;
        }
        return false;
    }

    public boolean isTickingFutureReady(long pos) {
        if (!this.world.shouldTickBlocksInChunk(pos)) {
            return false;
        }
        ChunkHolder chunkHolder = this.getChunkHolder(pos);
        if (chunkHolder == null) {
            return false;
        }
        return chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).isPresent();
    }

    public void save(boolean flush) {
        this.updateChunks();
        this.chunkLoadingManager.save(flush);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.persistentStateManager.close();
        this.lightingProvider.close();
        this.chunkLoadingManager.close();
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
        Profiler profiler = Profilers.get();
        profiler.push("purge");
        if (this.world.getTickManager().shouldTick() || !tickChunks) {
            this.ticketManager.tick(this.chunkLoadingManager);
        }
        this.updateChunks();
        profiler.swap("chunks");
        if (tickChunks) {
            this.tickChunks();
            this.chunkLoadingManager.tickEntityMovement();
        }
        profiler.swap("unload");
        this.chunkLoadingManager.tick(shouldKeepTicking);
        profiler.pop();
        this.initChunkCaches();
    }

    private void tickChunks() {
        long l = this.world.getTime();
        long m = l - this.lastTickTime;
        this.lastTickTime = l;
        if (this.world.isDebugWorld()) {
            return;
        }
        Profiler profiler = Profilers.get();
        profiler.push("pollingChunks");
        if (this.world.getTickManager().shouldTick()) {
            profiler.push("tickingChunks");
            this.tickChunks(profiler, m);
            profiler.pop();
        }
        this.broadcastUpdates(profiler);
        profiler.pop();
    }

    private void broadcastUpdates(Profiler profiler) {
        profiler.push("broadcast");
        for (ChunkHolder chunkHolder : this.chunksToBroadcastUpdate) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk == null) continue;
            chunkHolder.flushUpdates(worldChunk);
        }
        this.chunksToBroadcastUpdate.clear();
        profiler.pop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tickChunks(Profiler profiler, long timeDelta) {
        List<SpawnGroup> list;
        SpawnHelper.Info info;
        profiler.swap("naturalSpawnCount");
        int i = this.levelManager.getTickedChunkCount();
        this.spawnInfo = info = SpawnHelper.setupSpawn(i, this.world.iterateEntities(), this::ifChunkLoaded, new SpawnDensityCapper(this.chunkLoadingManager));
        profiler.swap("spawnAndTick");
        boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
        int j = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        if (bl && (this.spawnMonsters || this.spawnAnimals)) {
            boolean bl2 = this.world.net_minecraft_world_WorldProperties_getLevelProperties().getTime() % 400L == 0L;
            list = SpawnHelper.collectSpawnableGroups(info, this.spawnAnimals, this.spawnMonsters, bl2);
        } else {
            list = List.of();
        }
        List<WorldChunk> list2 = this.spawningChunks;
        try {
            profiler.push("filteringSpawningChunks");
            this.chunkLoadingManager.collectSpawningChunks(list2);
            profiler.swap("shuffleSpawningChunks");
            Util.shuffle(list2, this.world.random);
            profiler.swap("tickSpawningChunks");
            for (WorldChunk worldChunk : list2) {
                this.tickSpawningChunk(worldChunk, timeDelta, list, info);
            }
            list2.clear();
        }
        catch (Throwable throwable) {
            list2.clear();
            throw throwable;
        }
        profiler.swap("tickTickingChunks");
        this.chunkLoadingManager.forEachBlockTickingChunk(chunk -> this.world.tickChunk((WorldChunk)chunk, j));
        profiler.pop();
        profiler.swap("customSpawners");
        if (bl) {
            this.world.tickSpawners(this.spawnMonsters, this.spawnAnimals);
        }
    }

    private void tickSpawningChunk(WorldChunk chunk, long timeDelta, List<SpawnGroup> spawnableGroups, SpawnHelper.Info info) {
        ChunkPos chunkPos = chunk.getPos();
        chunk.increaseInhabitedTime(timeDelta);
        if (this.levelManager.shouldTickEntities(chunkPos.toLong())) {
            this.world.tickThunder(chunk);
        }
        if (spawnableGroups.isEmpty()) {
            return;
        }
        if (this.world.canSpawnEntitiesAt(chunkPos)) {
            SpawnHelper.spawn(this.world, chunk, info, spawnableGroups);
        }
    }

    private void ifChunkLoaded(long pos, Consumer<WorldChunk> chunkConsumer) {
        ChunkHolder chunkHolder = this.getChunkHolder(pos);
        if (chunkHolder != null) {
            chunkHolder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).ifPresent(chunkConsumer);
        }
    }

    @Override
    public String getDebugString() {
        return Integer.toString(this.getLoadedChunkCount());
    }

    @VisibleForTesting
    public int getPendingTasks() {
        return this.mainThreadExecutor.getTaskCount();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.chunkLoadingManager.getChunkGenerator();
    }

    public StructurePlacementCalculator getStructurePlacementCalculator() {
        return this.chunkLoadingManager.getStructurePlacementCalculator();
    }

    public NoiseConfig getNoiseConfig() {
        return this.chunkLoadingManager.getNoiseConfig();
    }

    @Override
    public int getLoadedChunkCount() {
        return this.chunkLoadingManager.getLoadedChunkCount();
    }

    public void markForUpdate(BlockPos pos) {
        int j;
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(i, j = ChunkSectionPos.getSectionCoord(pos.getZ())));
        if (chunkHolder != null && chunkHolder.markForBlockUpdate(pos)) {
            this.chunksToBroadcastUpdate.add(chunkHolder);
        }
    }

    @Override
    public void onLightUpdate(LightType type, ChunkSectionPos pos) {
        this.mainThreadExecutor.execute(() -> {
            ChunkHolder chunkHolder = this.getChunkHolder(pos.toChunkPos().toLong());
            if (chunkHolder != null && chunkHolder.markForLightUpdate(type, pos.getSectionY())) {
                this.chunksToBroadcastUpdate.add(chunkHolder);
            }
        });
    }

    public void addTicket(ChunkTicket ticket, ChunkPos pos) {
        this.ticketManager.addTicket(ticket, pos);
    }

    public void addTicket(ChunkTicketType type, ChunkPos pos, int radius) {
        this.ticketManager.addTicket(type, pos, radius);
    }

    public void removeTicket(ChunkTicketType type, ChunkPos pos, int radius) {
        this.ticketManager.removeTicket(type, pos, radius);
    }

    @Override
    public boolean setChunkForced(ChunkPos pos, boolean forced) {
        return this.ticketManager.setChunkForced(pos, forced);
    }

    @Override
    public LongSet getForcedChunks() {
        return this.ticketManager.getForcedChunks();
    }

    public void updatePosition(ServerPlayerEntity player) {
        if (!player.isRemoved()) {
            this.chunkLoadingManager.updatePosition(player);
            if (player.canReceiveWaypoints()) {
                this.world.getWaypointHandler().updatePlayerPos(player);
            }
        }
    }

    public void unloadEntity(Entity entity) {
        this.chunkLoadingManager.unloadEntity(entity);
    }

    public void loadEntity(Entity entity) {
        this.chunkLoadingManager.loadEntity(entity);
    }

    public void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        this.chunkLoadingManager.sendToNearbyPlayers(entity, packet);
    }

    public void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
        this.chunkLoadingManager.sendToOtherNearbyPlayers(entity, packet);
    }

    public void applyViewDistance(int watchDistance) {
        this.chunkLoadingManager.setViewDistance(watchDistance);
    }

    public void applySimulationDistance(int simulationDistance) {
        this.levelManager.setSimulationDistance(simulationDistance);
    }

    @Override
    public void setMobSpawnOptions(boolean spawnMonsters) {
        this.spawnMonsters = spawnMonsters;
        this.spawnAnimals = this.spawnAnimals;
    }

    public String getChunkLoadingDebugInfo(ChunkPos pos) {
        return this.chunkLoadingManager.getChunkLoadingDebugInfo(pos);
    }

    public PersistentStateManager getPersistentStateManager() {
        return this.persistentStateManager;
    }

    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.chunkLoadingManager.getPointOfInterestStorage();
    }

    public NbtScannable getChunkIoWorker() {
        return this.chunkLoadingManager.getWorker();
    }

    @Nullable
    @Debug
    public SpawnHelper.Info getSpawnInfo() {
        return this.spawnInfo;
    }

    public void shutdown() {
        this.ticketManager.shutdown();
    }

    public void markForUpdate(ChunkHolder chunkHolder) {
        if (chunkHolder.hasPendingUpdates()) {
            this.chunksToBroadcastUpdate.add(chunkHolder);
        }
    }

    @Override
    public LightingProvider net_minecraft_world_chunk_light_LightingProvider_getLightingProvider() {
        return this.net_minecraft_server_world_ServerLightingProvider_getLightingProvider();
    }

    @Override
    public BlockView net_minecraft_world_BlockView_getWorld() {
        return this.net_minecraft_world_World_getWorld();
    }

    final class MainThreadExecutor
    extends ThreadExecutor<Runnable> {
        MainThreadExecutor(World world) {
            super("Chunk source main thread executor for " + String.valueOf(world.getRegistryKey().getValue()));
        }

        @Override
        public void runTasks(BooleanSupplier stopCondition) {
            super.runTasks(() -> MinecraftServer.checkWorldGenException() && stopCondition.getAsBoolean());
        }

        @Override
        public Runnable createTask(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable task) {
            return true;
        }

        @Override
        protected boolean shouldExecuteAsync() {
            return true;
        }

        @Override
        protected Thread getThread() {
            return ServerChunkManager.this.serverThread;
        }

        @Override
        protected void executeTask(Runnable task) {
            Profilers.get().visit("runTask");
            super.executeTask(task);
        }

        @Override
        protected boolean runTask() {
            if (ServerChunkManager.this.updateChunks()) {
                return true;
            }
            ServerChunkManager.this.lightingProvider.tick();
            return super.runTask();
        }
    }
}

