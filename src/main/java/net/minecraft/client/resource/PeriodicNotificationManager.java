/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.math.LongMath
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PeriodicNotificationManager
extends SinglePreparationResourceReloader<Map<String, List<Entry>>>
implements AutoCloseable {
    final static private Codec<Map<String, List<Entry>>> CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.optionalFieldOf("delay", (Object)0L).forGetter(Entry::delay), (App)Codec.LONG.fieldOf("period").forGetter(Entry::period), (App)Codec.STRING.fieldOf("title").forGetter(Entry::title), (App)Codec.STRING.fieldOf("message").forGetter(Entry::message)).apply((Applicative)instance, Entry::new)).listOf());
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Identifier id;
    final private Object2BooleanFunction<String> countryPredicate;
    @Nullable
    private Timer timer;
    @Nullable
    private NotifyTask task;

    public PeriodicNotificationManager(Identifier id, Object2BooleanFunction<String> countryPredicate) {
        this.id = id;
        this.countryPredicate = countryPredicate;
    }

    @Override
    protected Map<String, List<Entry>> prepare(ResourceManager resourceManager, Profiler profiler) {
        Map map;
        block8: {
            BufferedReader reader = resourceManager.openAsReader(this.id);
            try {
                map = (Map)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)StrictJsonParser.parse(reader)).result().orElseThrow();
                if (reader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to load {}", (Object)this.id, (Object)exception);
                    return ImmutableMap.of();
                }
            }
            ((Reader)reader).close();
        }
        return map;
    }

    @Override
    protected void apply(Map<String, List<Entry>> map, ResourceManager resourceManager, Profiler profiler) {
        List<Entry> list = map.entrySet().stream().filter(entry -> (Boolean)this.countryPredicate.apply((Object)((String)entry.getKey()))).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        if (list.isEmpty()) {
            this.cancelTimer();
            return;
        }
        if (list.stream().anyMatch(entry -> entry.period == 0L)) {
            Util.logErrorOrPause("A periodic notification in " + String.valueOf(this.id) + " has a period of zero minutes");
            this.cancelTimer();
            return;
        }
        long l = this.getMinDelay(list);
        long m = this.getPeriod(list, l);
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.task = this.task == null ? new NotifyTask(list, l, m) : this.task.reload(list, m);
        this.timer.scheduleAtFixedRate((TimerTask)this.task, TimeUnit.MINUTES.toMillis(l), TimeUnit.MINUTES.toMillis(m));
    }

    @Override
    public void close() {
        this.cancelTimer();
    }

    private void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private long getPeriod(List<Entry> entries, long minDelay) {
        return entries.stream().mapToLong(entry -> {
            long m = entry.delay - minDelay;
            return LongMath.gcd((long)m, (long)entry.period);
        }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + String.valueOf(this.id)));
    }

    private long getMinDelay(List<Entry> entries) {
        return entries.stream().mapToLong(entry -> entry.delay).min().orElse(0L);
    }

    @Override
    protected Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    static class NotifyTask
    extends TimerTask {
        final private MinecraftClient client = MinecraftClient.getInstance();
        final private List<Entry> entries;
        final private long periodMs;
        final private AtomicLong delayMs;

        public NotifyTask(List<Entry> entries, long minDelayMs, long periodMs) {
            this.entries = entries;
            this.periodMs = periodMs;
            this.delayMs = new AtomicLong(minDelayMs);
        }

        public NotifyTask reload(List<Entry> entries, long period) {
            this.cancel();
            return new NotifyTask(entries, this.delayMs.get(), period);
        }

        @Override
        public void run() {
            long l = this.delayMs.getAndAdd(this.periodMs);
            long m = this.delayMs.get();
            for (Entry entry : this.entries) {
                long o;
                long n;
                if (l < entry.delay || (n = l / entry.period) == (o = m / entry.period)) continue;
                this.client.execute(() -> SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable(entry.title, n), Text.translatable(entry.message, n)));
                return;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Entry
    extends Record {
        final long delay;
        final long period;
        final String title;
        final String message;

        public Entry(long delay, long period, String title, String message) {
            this.delay = delay != 0L ? delay : period;
            this.period = period;
            this.title = title;
            this.message = message;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "delay;period;title;message", "delay", "period", "title", "message"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "delay;period;title;message", "delay", "period", "title", "message"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "delay;period;title;message", "delay", "period", "title", "message"}, this, object);
        }

        public long delay() {
            return this.delay;
        }

        public long period() {
            return this.period;
        }

        public String title() {
            return this.title;
        }

        public String message() {
            return this.message;
        }
    }
}

