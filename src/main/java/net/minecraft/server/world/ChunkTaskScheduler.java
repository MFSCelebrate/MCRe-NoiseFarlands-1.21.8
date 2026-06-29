/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.world;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.PrioritizedConsecutiveExecutor;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChunkTaskScheduler
implements ChunkHolder.LevelUpdateListener,
AutoCloseable {
    final static public int LEVELS = 4;
    final static private Logger LOGGER = LogUtils.getLogger();
    final private LevelPrioritizedQueue queue;
    final private TaskExecutor<Runnable> executor;
    final private PrioritizedConsecutiveExecutor dispatcher;
    protected boolean pollOnUpdate;

    public ChunkTaskScheduler(TaskExecutor<Runnable> executor, Executor dispatchExecutor) {
        this.queue = new LevelPrioritizedQueue(executor.getName() + "_queue");
        this.executor = executor;
        this.dispatcher = new PrioritizedConsecutiveExecutor(4, dispatchExecutor, "dispatcher");
        this.pollOnUpdate = true;
    }

    public boolean shouldDelayShutdown() {
        return this.dispatcher.hasQueuedTasks() || this.queue.hasQueuedElement();
    }

    @Override
    public void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter) {
        this.dispatcher.send(new TaskQueue.PrioritizedTask(0, () -> {
            int j = levelGetter.getAsInt();
            this.queue.updateLevel(j, pos, 1);
            levelSetter.accept(1);
        }));
    }

    public void remove(long pos, Runnable callback, boolean removeElement) {
        this.dispatcher.send(new TaskQueue.PrioritizedTask(1, () -> {
            this.queue.remove(pos, removeElement);
            this.onRemove(pos);
            if (this.pollOnUpdate) {
                this.pollOnUpdate = false;
                this.pollTask();
            }
            callback.run();
        }));
    }

    public void add(Runnable runnable, long pos, IntSupplier levelGetter) {
        this.dispatcher.send(new TaskQueue.PrioritizedTask(2, () -> {
            int i = levelGetter.getAsInt();
            this.queue.add(runnable, pos, i);
            if (this.pollOnUpdate) {
                this.pollOnUpdate = false;
                this.pollTask();
            }
        }));
    }

    protected void pollTask() {
        this.dispatcher.send(new TaskQueue.PrioritizedTask(3, () -> {
            LevelPrioritizedQueue.Entry entry = this.poll();
            if (entry == null) {
                this.pollOnUpdate = true;
            } else {
                this.schedule(entry);
            }
        }));
    }

    protected void schedule(LevelPrioritizedQueue.Entry entry) {
        CompletableFuture.allOf((CompletableFuture[])entry.tasks().stream().map(runnable -> this.executor.executeAsync(future -> {
            runnable.run();
            future.complete(Unit.INSTANCE);
        })).toArray(CompletableFuture[]::new)).thenAccept(v -> this.pollTask());
    }

    protected void onRemove(long chunkPos) {
    }

    @Nullable
    protected LevelPrioritizedQueue.Entry poll() {
        return this.queue.poll();
    }

    @Override
    public void close() {
        this.executor.close();
    }
}

