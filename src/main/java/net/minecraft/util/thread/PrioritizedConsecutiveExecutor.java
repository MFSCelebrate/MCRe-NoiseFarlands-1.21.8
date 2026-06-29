/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.util.thread.ExecutorSampling;
import net.minecraft.util.thread.TaskQueue;

public class PrioritizedConsecutiveExecutor
extends ConsecutiveExecutor<TaskQueue.PrioritizedTask> {
    public PrioritizedConsecutiveExecutor(int priorityCount, Executor executor, String name) {
        super(new TaskQueue.Prioritized(priorityCount), executor, name);
        ExecutorSampling.INSTANCE.add(this);
    }

    @Override
    public TaskQueue.PrioritizedTask net_minecraft_util_thread_TaskQueue$PrioritizedTask_createTask(Runnable runnable) {
        return new TaskQueue.PrioritizedTask(0, runnable);
    }

    public <Source> CompletableFuture<Source> executeAsync(int priority, Consumer<CompletableFuture<Source>> future) {
        CompletableFuture completableFuture = new CompletableFuture();
        this.send(new TaskQueue.PrioritizedTask(priority, () -> future.accept(completableFuture)));
        return completableFuture;
    }

    @Override
    public Runnable java_lang_Runnable_createTask(Runnable runnable) {
        return this.net_minecraft_util_thread_TaskQueue$PrioritizedTask_createTask(runnable);
    }
}

