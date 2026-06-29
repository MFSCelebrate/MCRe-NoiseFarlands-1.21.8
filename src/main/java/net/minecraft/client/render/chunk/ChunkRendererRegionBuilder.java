/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(value=EnvType.CLIENT)
public class ChunkRendererRegionBuilder {
    final private Long2ObjectMap<RenderedChunk> renderedChunksByPos = new Long2ObjectOpenHashMap();

    public ChunkRendererRegion build(World world, long sectionPos) {
        int i = ChunkSectionPos.unpackX(sectionPos);
        int j = ChunkSectionPos.unpackY(sectionPos);
        int k = ChunkSectionPos.unpackZ(sectionPos);
        int l = i - 1;
        int m = j - 1;
        int n = k - 1;
        int o = i + 1;
        int p = j + 1;
        int q = k + 1;
        RenderedChunk[] renderedChunks = new RenderedChunk[27];
        for (int r = n; r <= q; ++r) {
            for (int s = m; s <= p; ++s) {
                for (int t = l; t <= o; ++t) {
                    int u = ChunkRendererRegion.getIndex(l, m, n, t, s, r);
                    renderedChunks[u] = this.getRenderedChunk(world, t, s, r);
                }
            }
        }
        return new ChunkRendererRegion(world, l, m, n, renderedChunks);
    }

    private RenderedChunk getRenderedChunk(World world, int sectionX, int sectionY, int sectionZ) {
        return (RenderedChunk)this.renderedChunksByPos.computeIfAbsent(ChunkSectionPos.asLong(sectionX, sectionY, sectionZ), pos -> {
            WorldChunk worldChunk = world.net_minecraft_world_chunk_WorldChunk_getChunk(sectionX, sectionZ);
            return new RenderedChunk(worldChunk, worldChunk.sectionCoordToIndex(sectionY));
        });
    }
}

