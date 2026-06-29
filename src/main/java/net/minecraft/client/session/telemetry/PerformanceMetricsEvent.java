/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.telemetry.SampleEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.client.session.telemetry.TelemetrySender;

@Environment(value=EnvType.CLIENT)
public final class PerformanceMetricsEvent
extends SampleEvent {
    final static private long MAX_MEMORY_KB = PerformanceMetricsEvent.toKilos(Runtime.getRuntime().maxMemory());
    final private LongList fpsSamples = new LongArrayList();
    final private LongList renderTimeSamples = new LongArrayList();
    final private LongList usedMemorySamples = new LongArrayList();

    @Override
    public void tick(TelemetrySender sender) {
        if (MinecraftClient.getInstance().isOptionalTelemetryEnabled()) {
            super.tick(sender);
        }
    }

    private void clearSamples() {
        this.fpsSamples.clear();
        this.renderTimeSamples.clear();
        this.usedMemorySamples.clear();
    }

    @Override
    public void sample() {
        this.fpsSamples.add((long)MinecraftClient.getInstance().getCurrentFps());
        this.sampleUsedMemory();
        this.renderTimeSamples.add(MinecraftClient.getInstance().getRenderTime());
    }

    private void sampleUsedMemory() {
        long l = Runtime.getRuntime().totalMemory();
        long m = Runtime.getRuntime().freeMemory();
        long n = l - m;
        this.usedMemorySamples.add(PerformanceMetricsEvent.toKilos(n));
    }

    @Override
    public void send(TelemetrySender sender) {
        sender.send(TelemetryEventType.PERFORMANCE_METRICS, map -> {
            map.put(TelemetryEventProperty.FRAME_RATE_SAMPLES, new LongArrayList(this.fpsSamples));
            map.put(TelemetryEventProperty.RENDER_TIME_SAMPLES, new LongArrayList(this.renderTimeSamples));
            map.put(TelemetryEventProperty.USED_MEMORY_SAMPLES, new LongArrayList(this.usedMemorySamples));
            map.put(TelemetryEventProperty.NUMBER_OF_SAMPLES, this.getSampleCount());
            map.put(TelemetryEventProperty.RENDER_DISTANCE, MinecraftClient.getInstance().options.getClampedViewDistance());
            map.put(TelemetryEventProperty.DEDICATED_MEMORY_KB, (int)MAX_MEMORY_KB);
        });
        this.clearSamples();
    }

    private static long toKilos(long bytes) {
        return bytes / 1000L;
    }
}

