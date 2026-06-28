/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public final class NetworkIoStatistics<T> {
    final private PacketStatistics combinedStatistics;
    final private List<Pair<T, PacketStatistics>> topContributors;
    final private Duration duration;

    public NetworkIoStatistics(Duration duration, List<Pair<T, PacketStatistics>> packetsToStatistics) {
        this.duration = duration;
        this.combinedStatistics = packetsToStatistics.stream().map(Pair::getSecond).reduce(new PacketStatistics(0L, 0L), PacketStatistics::add);
        this.topContributors = packetsToStatistics.stream().sorted(Comparator.comparing(Pair::getSecond, PacketStatistics.COMPARATOR)).limit(10L).toList();
    }

    public double getCountPerSecond() {
        return (double)this.combinedStatistics.totalCount / (double)this.duration.getSeconds();
    }

    public double getBytesPerSecond() {
        return (double)this.combinedStatistics.totalSize / (double)this.duration.getSeconds();
    }

    public long getTotalCount() {
        return this.combinedStatistics.totalCount;
    }

    public long getTotalSize() {
        return this.combinedStatistics.totalSize;
    }

    public List<Pair<T, PacketStatistics>> getTopContributors() {
        return this.topContributors;
    }

    public static final class PacketStatistics
    extends Record {
        final long totalCount;
        final long totalSize;
        final static Comparator<PacketStatistics> COMPARATOR = Comparator.comparing(PacketStatistics::totalSize).thenComparing(PacketStatistics::totalCount).reversed();

        public PacketStatistics(long l, long m) {
            this.totalCount = l;
            this.totalSize = m;
        }

        PacketStatistics add(PacketStatistics statistics) {
            return new PacketStatistics(this.totalCount + statistics.totalCount, this.totalSize + statistics.totalSize);
        }

        public float getAverageSize() {
            return (float)this.totalSize / (float)this.totalCount;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PacketStatistics.class, "totalCount;totalSize", "totalCount", "totalSize"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PacketStatistics.class, "totalCount;totalSize", "totalCount", "totalSize"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PacketStatistics.class, "totalCount;totalSize", "totalCount", "totalSize"}, this, object);
        }

        public long totalCount() {
            return this.totalCount;
        }

        public long totalSize() {
            return this.totalSize;
        }
    }
}

