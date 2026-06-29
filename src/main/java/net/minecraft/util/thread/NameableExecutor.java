/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.jtracy.Zone
 */
package net.minecraft.util.thread;

import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.SharedConstants;

public record NameableExecutor(ExecutorService service) implements Executor
{
    public Executor named(String name) {
        if (SharedConstants.isDevelopment) {
            return runnable -> this.service.execute(() -> {
                Thread thread = Thread.currentThread();
                String string2 = thread.getName();
                thread.setName(name);
                try {
                    block6: {
                        try (Zone zone = TracyClient.beginZone((String)name, (boolean)SharedConstants.isDevelopment);){
                            runnable.run();
                            if (zone == null) break block6;
                        }
                    }
                    thread.setName(string2);
                    return;
                }
                catch (Throwable throwable) {
                    thread.setName(string2);
                    throw throwable;
                }
            });
        }
        if (TracyClient.isAvailable()) {
            return runnable -> this.service.execute(() -> {
                try (Zone zone = TracyClient.beginZone((String)name, (boolean)SharedConstants.isDevelopment);){
                    runnable.run();
                    if (zone == null) return;
                }
            });
        }
        return this.service;
    }

    @Override
    public void execute(Runnable runnable) {
        this.service.execute(NameableExecutor.wrapForTracy(runnable));
    }

    public void shutdown(long time, TimeUnit unit) {
        boolean bl;
        this.service.shutdown();
        try {
            bl = this.service.awaitTermination(time, unit);
        }
        catch (InterruptedException interruptedException) {
            bl = false;
        }
        if (!bl) {
            this.service.shutdownNow();
        }
    }

    private static Runnable wrapForTracy(Runnable runnable) {
        if (!TracyClient.isAvailable()) {
            return runnable;
        }
        return () -> {
            try (Zone zone = TracyClient.beginZone((String)"task", (boolean)SharedConstants.isDevelopment);){
                runnable.run();
                if (zone == null) return;
            }
        };
    }
}

