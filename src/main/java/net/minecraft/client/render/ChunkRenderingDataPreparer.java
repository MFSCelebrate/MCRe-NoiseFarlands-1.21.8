/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.slf4j.Logger
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HeightLimitView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderingDataPreparer {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Direction[] DIRECTIONS = Direction.values();
    final static private int DEFAULT_SECTION_DISTANCE = 60;
    final static private int SECTION_DISTANCE = ChunkSectionPos.getSectionCoord(60);
    final static private double CHUNK_INNER_DIAGONAL_LENGTH = Math.ceil(Math.sqrt(3.0) * 16.0);
    private boolean terrainUpdateScheduled = true;
    @Nullable
    private Future<?> terrainUpdateFuture;
    @Nullable
    private BuiltChunkStorage builtChunkStorage;
    final private AtomicReference<PreparerState> state = new AtomicReference();
    final private AtomicReference<Events> events = new AtomicReference();
    final private AtomicBoolean needsUpdate = new AtomicBoolean(false);

    public void setStorage(@Nullable BuiltChunkStorage storage) {
        if (this.terrainUpdateFuture != null) {
            try {
                this.terrainUpdateFuture.get();
                this.terrainUpdateFuture = null;
            }
            catch (Exception exception) {
                LOGGER.warn("Full update failed", (Throwable)exception);
            }
        }
        this.builtChunkStorage = storage;
        if (storage != null) {
            this.state.set(new PreparerState(storage));
            this.scheduleTerrainUpdate();
        } else {
            this.state.set(null);
        }
    }

    public void scheduleTerrainUpdate() {
        this.terrainUpdateScheduled = true;
    }

    public void collectChunks(Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, List<ChunkBuilder.BuiltChunk> nearbyChunks) {
        this.state.get().storage().octree.visit((node, skipVisibilityCheck, depth, nearCenter) -> {
            ChunkBuilder.BuiltChunk builtChunk = node.getBuiltChunk();
            if (builtChunk != null) {
                builtChunks.add(builtChunk);
                if (nearCenter) {
                    nearbyChunks.add(builtChunk);
                }
            }
        }, frustum, 32);
    }

    public boolean updateFrustum() {
        return this.needsUpdate.compareAndSet(true, false);
    }

    public void addNeighbors(ChunkPos chunkPos) {
        Events events2;
        Events events = this.events.get();
        if (events != null) {
            this.addNeighbors(events, chunkPos);
        }
        if ((events2 = this.state.get().events) != events) {
            this.addNeighbors(events2, chunkPos);
        }
    }

    public void schedulePropagationFrom(ChunkBuilder.BuiltChunk builtChunk) {
        Events events2;
        Events events = this.events.get();
        if (events != null) {
            events.sectionsToPropagateFrom.add(builtChunk);
        }
        if ((events2 = this.state.get().events) != events) {
            events2.sectionsToPropagateFrom.add(builtChunk);
        }
    }

    public void updateSectionOcclusionGraph(boolean cullChunks, Camera camera, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunk, LongOpenHashSet activeSections) {
        Vec3d vec3d = camera.getPos();
        if (this.terrainUpdateScheduled && (this.terrainUpdateFuture == null || this.terrainUpdateFuture.isDone())) {
            this.updateTerrain(cullChunks, camera, vec3d, activeSections);
        }
        this.updateNow(cullChunks, frustum, builtChunk, vec3d, activeSections);
    }

    private void updateTerrain(boolean cullChunks, Camera camera, Vec3d cameraPos, LongOpenHashSet activeSections) {
        this.terrainUpdateScheduled = false;
        LongOpenHashSet longOpenHashSet = activeSections.clone();
        this.terrainUpdateFuture = CompletableFuture.runAsync(() -> {
            PreparerState preparerState = new PreparerState(this.builtChunkStorage);
            this.events.set(preparerState.events);
            ArrayDeque queue = Queues.newArrayDeque();
            this.scheduleLater(camera, queue);
            queue.forEach(info -> preparerState.storage.infoList.setInfo(info.chunk, (ChunkInfo)info));
            this.update(preparerState.storage, cameraPos, queue, cullChunks, builtChunk -> {}, longOpenHashSet);
            this.state.set(preparerState);
            this.events.set(null);
            this.needsUpdate.set(true);
        }, Util.getMainWorkerExecutor());
    }

    private void updateNow(boolean cullChunks, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, Vec3d cameraPos, LongOpenHashSet activeSections) {
        PreparerState preparerState = this.state.get();
        this.scheduleNew(preparerState);
        if (!preparerState.events.sectionsToPropagateFrom.isEmpty()) {
            ArrayDeque queue = Queues.newArrayDeque();
            while (!preparerState.events.sectionsToPropagateFrom.isEmpty()) {
                ChunkBuilder.BuiltChunk builtChunk2 = (ChunkBuilder.BuiltChunk)preparerState.events.sectionsToPropagateFrom.poll();
                ChunkInfo chunkInfo = preparerState.storage.infoList.getInfo(builtChunk2);
                if (chunkInfo == null || chunkInfo.chunk != builtChunk2) continue;
                queue.add(chunkInfo);
            }
            Frustum frustum2 = WorldRenderer.offsetFrustum(frustum);
            Consumer<ChunkBuilder.BuiltChunk> consumer = builtChunk -> {
                if (frustum2.isVisible(builtChunk.getBoundingBox())) {
                    this.needsUpdate.set(true);
                }
            };
            this.update(preparerState.storage, cameraPos, queue, cullChunks, consumer, activeSections);
        }
    }

    private void scheduleNew(PreparerState preparerState) {
        LongIterator longIterator = preparerState.events.chunksWhichReceivedNeighbors.iterator();
        while (longIterator.hasNext()) {
            long l = longIterator.nextLong();
            List list = (List)preparerState.storage.queue.get(l);
            if (list == null || !((ChunkBuilder.BuiltChunk)list.get(0)).shouldBuild()) continue;
            preparerState.events.sectionsToPropagateFrom.addAll(list);
            preparerState.storage.queue.remove(l);
        }
        preparerState.events.chunksWhichReceivedNeighbors.clear();
    }

    private void addNeighbors(Events events, ChunkPos chunkPos) {
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z - 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z + 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z - 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z + 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z - 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z + 1));
    }

    private void scheduleLater(Camera camera, Queue<ChunkInfo> queue) {
        BlockPos blockPos = camera.getBlockPos();
        long l = ChunkSectionPos.toLong(blockPos);
        int i = ChunkSectionPos.unpackY(l);
        ChunkBuilder.BuiltChunk builtChunk = this.builtChunkStorage.getRenderedChunk(l);
        if (builtChunk == null) {
            HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
            boolean bl = i < heightLimitView.getBottomSectionCoord();
            int j = bl ? heightLimitView.getBottomSectionCoord() : heightLimitView.getTopSectionCoord();
            int k = this.builtChunkStorage.getViewDistance();
            ArrayList list = Lists.newArrayList();
            int m = ChunkSectionPos.unpackX(l);
            int n = ChunkSectionPos.unpackZ(l);
            for (int o = -k; o <= k; ++o) {
                for (int p = -k; p <= k; ++p) {
                    ChunkBuilder.BuiltChunk builtChunk2 = this.builtChunkStorage.getRenderedChunk(ChunkSectionPos.asLong(o + m, j, p + n));
                    if (builtChunk2 == null || !this.isWithinViewDistance(l, builtChunk2.getSectionPos())) continue;
                    Direction direction = bl ? Direction.UP : Direction.DOWN;
                    ChunkInfo chunkInfo2 = new ChunkInfo(builtChunk2, direction, 0);
                    chunkInfo2.updateCullingState(chunkInfo2.cullingState, direction);
                    if (o > 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.EAST);
                    } else if (o < 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.WEST);
                    }
                    if (p > 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.SOUTH);
                    } else if (p < 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.NORTH);
                    }
                    list.add(chunkInfo2);
                }
            }
            list.sort(Comparator.comparingDouble(chunkInfo -> blockPos.getSquaredDistance(ChunkSectionPos.from(chunkInfo.chunk.getSectionPos()).getCenterPos())));
            queue.addAll(list);
        } else {
            queue.add(new ChunkInfo(builtChunk, null, 0));
        }
    }

    private void update(RenderableChunks renderableChunks, Vec3d pos, Queue<ChunkInfo> queue, boolean cullChunks, Consumer<ChunkBuilder.BuiltChunk> consumer, LongOpenHashSet longOpenHashSet) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
        long l2 = chunkSectionPos.asLong();
        BlockPos blockPos = chunkSectionPos.getCenterPos();
        while (!queue.isEmpty()) {
            long m;
            ChunkInfo chunkInfo = queue.poll();
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
            if (!longOpenHashSet.contains(chunkInfo.chunk.getSectionPos())) {
                if (renderableChunks.octree.add(chunkInfo.chunk)) {
                    consumer.accept(chunkInfo.chunk);
                }
            } else {
                chunkInfo.chunk.currentRenderData.compareAndSet(ChunkRenderData.HIDDEN, ChunkRenderData.READY);
            }
            boolean bl = Math.abs(ChunkSectionPos.unpackX(m = builtChunk.getSectionPos()) - chunkSectionPos.getSectionX()) > SECTION_DISTANCE || Math.abs(ChunkSectionPos.unpackY(m) - chunkSectionPos.getSectionY()) > SECTION_DISTANCE || Math.abs(ChunkSectionPos.unpackZ(m) - chunkSectionPos.getSectionZ()) > SECTION_DISTANCE;
            for (Direction direction : DIRECTIONS) {
                ChunkInfo chunkInfo2;
                int i;
                ChunkBuilder.BuiltChunk builtChunk2 = this.getRenderedChunk(l2, builtChunk, direction);
                if (builtChunk2 == null || cullChunks && chunkInfo.canCull(direction.getOpposite())) continue;
                if (cullChunks && chunkInfo.hasAnyDirection()) {
                    AbstractChunkRenderData abstractChunkRenderData = builtChunk.getCurrentRenderData();
                    boolean bl2 = false;
                    for (i = 0; i < DIRECTIONS.length; ++i) {
                        if (!chunkInfo.hasDirection(i) || !abstractChunkRenderData.isVisibleThrough(DIRECTIONS[i].getOpposite(), direction)) continue;
                        bl2 = true;
                        break;
                    }
                    if (!bl2) continue;
                }
                if (cullChunks && bl) {
                    boolean bl4;
                    boolean bl3;
                    int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(m));
                    int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(m));
                    i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(m));
                    boolean bl2 = direction.getAxis() == Direction.Axis.X ? blockPos.getX() > j : (bl3 = blockPos.getX() < j);
                    boolean bl5 = direction.getAxis() == Direction.Axis.Y ? blockPos.getY() > k : (bl4 = blockPos.getY() < k);
                    boolean bl52 = direction.getAxis() == Direction.Axis.Z ? blockPos.getZ() > i : blockPos.getZ() < i;
                    Vector3d vector3d = new Vector3d((double)(j + (bl3 ? 16 : 0)), (double)(k + (bl4 ? 16 : 0)), (double)(i + (bl52 ? 16 : 0)));
                    Vector3d vector3d2 = new Vector3d(pos.x, pos.y, pos.z).sub((Vector3dc)vector3d).normalize().mul(CHUNK_INNER_DIAGONAL_LENGTH);
                    boolean bl6 = true;
                    while (vector3d.distanceSquared(pos.x, pos.y, pos.z) > 3600.0) {
                        vector3d.add((Vector3dc)vector3d2);
                        HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
                        if (vector3d.y > (double)heightLimitView.getTopYInclusive() || vector3d.y < (double)heightLimitView.getBottomY()) break;
                        ChunkBuilder.BuiltChunk builtChunk3 = this.builtChunkStorage.getRenderedChunk(BlockPos.ofFloored(vector3d.x, vector3d.y, vector3d.z));
                        if (builtChunk3 != null && renderableChunks.infoList.getInfo(builtChunk3) != null) continue;
                        bl6 = false;
                        break;
                    }
                    if (!bl6) continue;
                }
                if ((chunkInfo2 = renderableChunks.infoList.getInfo(builtChunk2)) != null) {
                    chunkInfo2.addDirection(direction);
                    continue;
                }
                ChunkInfo chunkInfo3 = new ChunkInfo(builtChunk2, direction, chunkInfo.propagationLevel + 1);
                chunkInfo3.updateCullingState(chunkInfo.cullingState, direction);
                if (builtChunk2.shouldBuild()) {
                    queue.add(chunkInfo3);
                    renderableChunks.infoList.setInfo(builtChunk2, chunkInfo3);
                    continue;
                }
                if (!this.isWithinViewDistance(l2, builtChunk2.getSectionPos())) continue;
                renderableChunks.infoList.setInfo(builtChunk2, chunkInfo3);
                long n = ChunkSectionPos.toChunkPos(builtChunk2.getSectionPos());
                ((List)renderableChunks.queue.computeIfAbsent(n, l -> new ArrayList())).add(builtChunk2);
            }
        }
    }

    private boolean isWithinViewDistance(long centerSectionPos, long otherSectionPos) {
        return ChunkFilter.isWithinDistanceExcludingEdge(ChunkSectionPos.unpackX(centerSectionPos), ChunkSectionPos.unpackZ(centerSectionPos), this.builtChunkStorage.getViewDistance(), ChunkSectionPos.unpackX(otherSectionPos), ChunkSectionPos.unpackZ(otherSectionPos));
    }

    @Nullable
    private ChunkBuilder.BuiltChunk getRenderedChunk(long sectionPos, ChunkBuilder.BuiltChunk chunk, Direction direction) {
        long l = chunk.getOffsetSectionPos(direction);
        if (!this.isWithinViewDistance(sectionPos, l)) {
            return null;
        }
        if (MathHelper.abs(ChunkSectionPos.unpackY(sectionPos) - ChunkSectionPos.unpackY(l)) > this.builtChunkStorage.getViewDistance()) {
            return null;
        }
        return this.builtChunkStorage.getRenderedChunk(l);
    }

    @Nullable
    @Debug
    public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
        return this.state.get().storage.infoList.getInfo(chunk);
    }

    public Octree getOctree() {
        return this.state.get().storage.octree;
    }

    @Environment(value=EnvType.CLIENT)
    static final class PreparerState
    extends Record {
        final RenderableChunks storage;
        final Events events;

        PreparerState(BuiltChunkStorage storage) {
            this(new RenderableChunks(storage), new Events());
        }

        private PreparerState(RenderableChunks renderableChunks, Events events) {
            this.storage = renderableChunks;
            this.events = events;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PreparerState.class, "storage;events", "storage", "events"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PreparerState.class, "storage;events", "storage", "events"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PreparerState.class, "storage;events", "storage", "events"}, this, object);
        }

        public RenderableChunks storage() {
            return this.storage;
        }

        public Events events() {
            return this.events;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class RenderableChunks {
        final public ChunkInfoList infoList;
        final public Octree octree;
        final public Long2ObjectMap<List<ChunkBuilder.BuiltChunk>> queue;

        public RenderableChunks(BuiltChunkStorage storage) {
            this.infoList = new ChunkInfoList(storage.chunks.length);
            this.octree = new Octree(storage.getSectionPos(), storage.getViewDistance(), storage.sizeY, storage.world.getBottomY());
            this.queue = new Long2ObjectOpenHashMap();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Events
    extends Record {
        final LongSet chunksWhichReceivedNeighbors;
        final BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom;

        Events() {
            this((LongSet)new LongOpenHashSet(), new LinkedBlockingQueue<ChunkBuilder.BuiltChunk>());
        }

        private Events(LongSet longSet, BlockingQueue<ChunkBuilder.BuiltChunk> blockingQueue) {
            this.chunksWhichReceivedNeighbors = longSet;
            this.sectionsToPropagateFrom = blockingQueue;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Events.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Events.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Events.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this, object);
        }

        public LongSet chunksWhichReceivedNeighbors() {
            return this.chunksWhichReceivedNeighbors;
        }

        public BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom() {
            return this.sectionsToPropagateFrom;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChunkInfoList {
        final private ChunkInfo[] current;

        ChunkInfoList(int size) {
            this.current = new ChunkInfo[size];
        }

        public void setInfo(ChunkBuilder.BuiltChunk chunk, ChunkInfo info) {
            this.current[chunk.index] = info;
        }

        @Nullable
        public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
            int i = chunk.index;
            if (i < 0 || i >= this.current.length) {
                return null;
            }
            return this.current[i];
        }
    }

    @Environment(value=EnvType.CLIENT)
    @Debug
    public static class ChunkInfo {
        @Debug
        final protected ChunkBuilder.BuiltChunk chunk;
        private byte direction;
        byte cullingState;
        @Debug
        final public int propagationLevel;

        ChunkInfo(ChunkBuilder.BuiltChunk chunk, @Nullable Direction direction, int propagationLevel) {
            this.chunk = chunk;
            if (direction != null) {
                this.addDirection(direction);
            }
            this.propagationLevel = propagationLevel;
        }

        void updateCullingState(byte parentCullingState, Direction from) {
            this.cullingState = (byte)(this.cullingState | (parentCullingState | 1 << from.ordinal()));
        }

        boolean canCull(Direction from) {
            return (this.cullingState & 1 << from.ordinal()) > 0;
        }

        void addDirection(Direction direction) {
            this.direction = (byte)(this.direction | (this.direction | 1 << direction.ordinal()));
        }

        @Debug
        public boolean hasDirection(int ordinal) {
            return (this.direction & 1 << ordinal) > 0;
        }

        boolean hasAnyDirection() {
            return this.direction != 0;
        }

        public int hashCode() {
            return Long.hashCode(this.chunk.getSectionPos());
        }

        public boolean equals(Object o) {
            if (!(o instanceof ChunkInfo)) {
                return false;
            }
            ChunkInfo chunkInfo = (ChunkInfo)o;
            return this.chunk.getSectionPos() == chunkInfo.chunk.getSectionPos();
        }
    }
}

