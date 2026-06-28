/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.profiling.jfr.sample;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.Quantiles;
import net.minecraft.util.profiling.jfr.sample.LongRunningSample;
import org.jetbrains.annotations.Nullable;

public record LongRunningSampleStatistics<T extends LongRunningSample>(T fastestSample, T slowestSample, @Nullable T secondSlowestSample, int count, Map<Integer, Double> quantiles, Duration totalDuration) {
    public LongRunningSampleStatistics(T longRunningSample, T longRunningSample2, @Nullable T longRunningSample3, int i, Map<Integer, Double> map, Duration duration) {
        this.fastestSample = longRunningSample;
        this.slowestSample = longRunningSample2;
        this.secondSlowestSample = longRunningSample3;
        this.count = 1;
        this.quantiles = map;
        this.totalDuration = duration;
    }

    public static <T extends LongRunningSample> LongRunningSampleStatistics<T> fromSamples(List<T> samples) {
        if (samples.isEmpty()) {
            throw new IllegalArgumentException("No values");
        }
        List<LongRunningSample> list = samples.stream().sorted(Comparator.comparing(LongRunningSample::duration)).toList();
        Duration duration = list.stream().map(LongRunningSample::duration).reduce(Duration::plus).orElse(Duration.ZERO);
        LongRunningSample longRunningSample = list.get(0);
        LongRunningSample longRunningSample2 = list.get(list.size() - 1);
        LongRunningSample longRunningSample3 = list.size() > 1 ? list.get(list.size() - 2) : null;
        int i = list.size();
        Map<Integer, Double> map = Quantiles.create(list.stream().mapToLong(sample -> sample.duration().toNanos()).toArray());
        return new LongRunningSampleStatistics<LongRunningSample>(longRunningSample, longRunningSample2, longRunningSample3, i, map, duration);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LongRunningSampleStatistics.class, "fastest;slowest;secondSlowest;count;percentilesNanos;totalDuration", "fastestSample", "slowestSample", "secondSlowestSample", "count", "quantiles", "totalDuration"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LongRunningSampleStatistics.class, "fastest;slowest;secondSlowest;count;percentilesNanos;totalDuration", "fastestSample", "slowestSample", "secondSlowestSample", "count", "quantiles", "totalDuration"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LongRunningSampleStatistics.class, "fastest;slowest;secondSlowest;count;percentilesNanos;totalDuration", "fastestSample", "slowestSample", "secondSlowestSample", "count", "quantiles", "totalDuration"}, this, o);
    }
}

