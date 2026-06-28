/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LockHelper {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private String name;
    final private Semaphore semaphore = new Semaphore(1);
    final private Lock lock = new ReentrantLock();
    @Nullable
    private volatile Thread thread;
    @Nullable
    private volatile CrashException crashException;

    public LockHelper(String name) {
        this.name = name;
    }

    public void lock() {
        boolean bl = false;
        try {
            this.lock.lock();
            if (!this.semaphore.tryAcquire()) {
                this.thread = Thread.currentThread();
                bl = true;
                this.lock.unlock();
                try {
                    this.semaphore.acquire();
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                throw this.crashException;
            }
            if (!bl) {
                this.lock.unlock();
            }
        }
        catch (Throwable throwable) {
            if (!bl) {
                this.lock.unlock();
            }
            throw throwable;
        }
    }

    public void unlock() {
        try {
            this.lock.lock();
            Thread thread = this.thread;
            if (thread != null) {
                CrashException crashException;
                this.crashException = crashException = LockHelper.crash(this.name, thread);
                this.semaphore.release();
                throw crashException;
            }
            this.semaphore.release();
            this.lock.unlock();
        }
        catch (Throwable throwable) {
            this.lock.unlock();
            throw throwable;
        }
    }

    public static CrashException crash(String message, @Nullable Thread thread) {
        String string = Stream.of(Thread.currentThread(), thread).filter(Objects::nonNull).map(LockHelper::formatStackTraceForThread).collect(Collectors.joining("\n"));
        String string2 = "Accessing " + message + " from multiple threads";
        CrashReport crashReport = new CrashReport(string2, new IllegalStateException(string2));
        CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
        crashReportSection.add("Thread dumps", string);
        LOGGER.error("Thread dumps: \n" + string);
        return new CrashException(crashReport);
    }

    private static String formatStackTraceForThread(Thread thread) {
        return thread.getName() + ": \n\tat " + Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
    }
}

