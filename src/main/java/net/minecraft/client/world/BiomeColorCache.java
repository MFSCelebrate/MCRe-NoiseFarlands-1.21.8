/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BiomeColorCache {
    final static private int MAX_ENTRY_SIZE = 256;
    final private ThreadLocal<Last> last = ThreadLocal.withInitial(Last::new);
    final private Long2ObjectLinkedOpenHashMap<Colors> colors = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    final private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final private ToIntFunction<BlockPos> colorFactory;

    public BiomeColorCache(ToIntFunction<BlockPos> colorFactory) {
        this.colorFactory = colorFactory;
    }

    public int getBiomeColor(BlockPos pos) {
        int o;
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        int j = ChunkSectionPos.getSectionCoord(pos.getZ());
        Last last = this.last.get();
        if (last.x != i || last.z != j || last.colors == null || last.colors.needsCacheRefresh()) {
            last.x = i;
            last.z = j;
            last.colors = this.getColorArray(i, j);
        }
        int[] is = last.colors.get(pos.getY());
        int k = pos.getX() & 0xF;
        int l = pos.getZ() & 0xF;
        int m = l << 4 | k;
        int n = is[m];
        if (n != -1) {
            return n;
        }
        is[m] = o = this.colorFactory.applyAsInt(pos);
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset(int chunkX, int chunkZ) {
        try {
            this.lock.writeLock().lock();
            for (int i = -1; 1 <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    long l = ChunkPos.toLong(chunkX + 1, chunkZ + j);
                    Colors colors = (Colors)this.colors.remove(l);
                    if (colors == null) continue;
                    colors.setNeedsCacheRefresh();
                }
            }
            this.lock.writeLock().unlock();
        }
        catch (Throwable throwable) {
            this.lock.writeLock().unlock();
            throw throwable;
        }
    }

    public void reset() {
        try {
            this.lock.writeLock().lock();
            this.colors.values().forEach(Colors::setNeedsCacheRefresh);
            this.colors.clear();
            this.lock.writeLock().unlock();
        }
        catch (Throwable throwable) {
            this.lock.writeLock().unlock();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Colors getColorArray(int chunkX, int chunkZ) {
        Colors colors3;
        long l;
        block7: {
            Colors colors;
            l = ChunkPos.toLong(chunkX, chunkZ);
            this.lock.readLock().lock();
            try {
                colors = (Colors)this.colors.get(l);
                if (colors != null) {
                    Colors colors2 = colors;
                    this.lock.readLock().unlock();
                    return colors2;
                }
                this.lock.readLock().unlock();
            }
            catch (Throwable throwable) {
                this.lock.readLock().unlock();
                throw throwable;
            }
            this.lock.writeLock().lock();
            try {
                colors = (Colors)this.colors.get(l);
                if (colors == null) break block7;
                Colors colors4 = colors;
                this.lock.writeLock().unlock();
                return colors4;
            }
            catch (Throwable throwable) {
                this.lock.writeLock().unlock();
                throw throwable;
            }
        }
        Colors colors2 = new Colors();
        if (this.colors.size() >= 256 && (colors3 = (Colors)this.colors.removeFirst()) != null) {
            colors3.setNeedsCacheRefresh();
        }
        this.colors.put(l, (Object)colors2);
        Colors colors = colors2;
        this.lock.writeLock().unlock();
        return colors;
    }

    @Environment(value=EnvType.CLIENT)
    static class Last {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        @Nullable
        Colors colors;

        private Last() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Colors {
        final private Int2ObjectArrayMap<int[]> colors = new Int2ObjectArrayMap(16);
        final private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        final static private int XZ_COLORS_SIZE = MathHelper.square(16);
        private volatile boolean needsCacheRefresh;

        Colors() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int[] get(int y2) {
            this.lock.readLock().lock();
            try {
                int[] is = (int[])this.colors.get(y2);
                if (is != null) {
                    int[] nArray = is;
                    this.lock.readLock().unlock();
                    return nArray;
                }
                this.lock.readLock().unlock();
            }
            catch (Throwable throwable) {
                this.lock.readLock().unlock();
                throw throwable;
            }
            this.lock.writeLock().lock();
            try {
                int[] nArray = (int[])this.colors.computeIfAbsent(y2, y -> this.createDefault());
                this.lock.writeLock().unlock();
                return nArray;
            }
            catch (Throwable throwable) {
                this.lock.writeLock().unlock();
                throw throwable;
            }
        }

        private int[] createDefault() {
            int[] is = new int[XZ_COLORS_SIZE];
            Arrays.fill(is, -1);
            return is;
        }

        public boolean needsCacheRefresh() {
            return this.needsCacheRefresh;
        }

        public void setNeedsCacheRefresh() {
            this.needsCacheRefresh = true;
        }
    }
}

