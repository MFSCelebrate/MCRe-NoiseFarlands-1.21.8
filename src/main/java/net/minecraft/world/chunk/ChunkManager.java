/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkManager
implements ChunkProvider,
AutoCloseable {
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ, boolean create) {
        return (WorldChunk)this.net_minecraft_world_chunk_Chunk_getChunk(chunkX, chunkZ, ChunkStatus.FULL, create);
    }

    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        return this.getWorldChunk(chunkX, chunkZ, false);
    }

    @Override
    @Nullable
    public LightSourceView getChunk(int chunkX, int chunkZ) {
        return this.net_minecraft_world_chunk_Chunk_getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
    }

    public boolean isChunkLoaded(int x, int z) {
        return this.net_minecraft_world_chunk_Chunk_getChunk(x, z, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract Chunk net_minecraft_world_chunk_Chunk_getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    public abstract void tick(BooleanSupplier var1, boolean var2);

    public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
    }

    public abstract String getDebugString();

    public abstract int getLoadedChunkCount();

    @Override
    public void close() throws IOException {
    }

    public abstract LightingProvider net_minecraft_world_chunk_light_LightingProvider_getLightingProvider();

    public void setMobSpawnOptions(boolean spawnMonsters) {
    }

    public boolean setChunkForced(ChunkPos pos, boolean forced) {
        return false;
    }

    public LongSet getForcedChunks() {
        return LongSet.of();
    }
}

