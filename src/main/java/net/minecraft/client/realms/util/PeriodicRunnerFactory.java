/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.TimeSupplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PeriodicRunnerFactory {
    final static Logger LOGGER = LogUtils.getLogger();
    final Executor executor;
    final TimeUnit timeUnit;
    final TimeSupplier timeSupplier;

    public PeriodicRunnerFactory(Executor executor, TimeUnit timeUnit, TimeSupplier timeSupplier) {
        this.executor = executor;
        this.timeUnit = timeUnit;
        this.timeSupplier = timeSupplier;
    }

    public <T> PeriodicRunner<T> create(String name, Callable<T> task, Duration cycle, Backoff backoff) {
        long l = this.timeUnit.convert(cycle);
        if (l == 0L) {
            throw new IllegalArgumentException("Period of " + String.valueOf(cycle) + " too short for selected resolution of " + String.valueOf((Object)this.timeUnit));
        }
        return new PeriodicRunner<T>(name, task, l, backoff);
    }

    public RunnersManager create() {
        return new RunnersManager();
    }

    @Environment(value=EnvType.CLIENT)
    public class PeriodicRunner<T> {
        final private String name;
        final private Callable<T> task;
        final private long unitDuration;
        final private Backoff backoff;
        @Nullable
        private CompletableFuture<TimedErrableResult<T>> resultFuture;
        @Nullable
        TimedResult<T> lastResult;
        private long nextTime = -1L;

        PeriodicRunner(String name, Callable<T> task, long unitDuration, Backoff backoff) {
            this.name = name;
            this.task = task;
            this.unitDuration = unitDuration;
            this.backoff = backoff;
        }

        void run(long currentTime) {
            if (this.resultFuture != null) {
                TimedErrableResult timedErrableResult = this.resultFuture.getNow(null);
                if (timedErrableResult == null) {
                    return;
                }
                this.resultFuture = null;
                long l = timedErrableResult.time;
                timedErrableResult.value().ifLeft(value -> {
                    this.lastResult = new TimedResult<Object>(value, l);
                    this.nextTime = l + this.unitDuration * this.backoff.success();
                }).ifRight(exception -> {
                    long m = this.backoff.fail();
                    LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.name, m, exception});
                    this.nextTime = l + this.unitDuration * m;
                });
            }
            if (this.nextTime <= currentTime) {
                this.resultFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        T object = this.task.call();
                        long l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                        return new TimedErrableResult(Either.left(object), l);
                    }
                    catch (Exception exception) {
                        long l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                        return new TimedErrableResult(Either.right((Object)exception), l);
                    }
                }, PeriodicRunnerFactory.this.executor);
            }
        }

        public void reset() {
            this.resultFuture = null;
            this.lastResult = null;
            this.nextTime = -1L;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class RunnersManager {
        final private List<ResultListenableRunner<?>> runners = new ArrayList();

        public <T> void add(PeriodicRunner<T> runner, Consumer<T> resultListener) {
            ResultListenableRunner<T> resultListenableRunner = new ResultListenableRunner<T>(PeriodicRunnerFactory.this, runner, resultListener);
            this.runners.add(resultListenableRunner);
            resultListenableRunner.runListener();
        }

        public void forceRunListeners() {
            for (ResultListenableRunner<?> resultListenableRunner : this.runners) {
                resultListenableRunner.forceRunListener();
            }
        }

        public void runAll() {
            for (ResultListenableRunner<?> resultListenableRunner : this.runners) {
                resultListenableRunner.run(PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit));
            }
        }

        public void resetAll() {
            for (ResultListenableRunner<?> resultListenableRunner : this.runners) {
                resultListenableRunner.reset();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ResultListenableRunner<T> {
        final private PeriodicRunner<T> runner;
        final private Consumer<T> resultListener;
        private long lastRunTime = -1L;

        ResultListenableRunner(PeriodicRunnerFactory periodicRunnerFactory, PeriodicRunner<T> runner, Consumer<T> resultListener) {
            this.runner = runner;
            this.resultListener = resultListener;
        }

        void run(long currentTime) {
            this.runner.run(currentTime);
            this.runListener();
        }

        void runListener() {
            TimedResult timedResult = this.runner.lastResult;
            if (timedResult != null && this.lastRunTime < timedResult.time) {
                this.resultListener.accept(timedResult.value);
                this.lastRunTime = timedResult.time;
            }
        }

        void forceRunListener() {
            TimedResult timedResult = this.runner.lastResult;
            if (timedResult != null) {
                this.resultListener.accept(timedResult.value);
                this.lastRunTime = timedResult.time;
            }
        }

        void reset() {
            this.runner.reset();
            this.lastRunTime = -1L;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class TimedResult<T>
    extends Record {
        final T value;
        final long time;

        TimedResult(T object, long l) {
            this.value = object;
            this.time = l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TimedResult.class, "value;time", "value", "time"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TimedResult.class, "value;time", "value", "time"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TimedResult.class, "value;time", "value", "time"}, this, object);
        }

        public T value() {
            return this.value;
        }

        public long time() {
            return this.time;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class TimedErrableResult<T>
    extends Record {
        final private Either<T, Exception> value;
        final long time;

        TimedErrableResult(Either<T, Exception> either, long l) {
            this.value = either;
            this.time = l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TimedErrableResult.class, "value;time", "value", "time"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TimedErrableResult.class, "value;time", "value", "time"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TimedErrableResult.class, "value;time", "value", "time"}, this, object);
        }

        public Either<T, Exception> value() {
            return this.value;
        }

        public long time() {
            return this.time;
        }
    }
}

