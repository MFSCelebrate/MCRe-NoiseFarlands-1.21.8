/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.session.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.SentTelemetryEvent;
import net.minecraft.client.session.telemetry.TelemetryLogger;
import net.minecraft.util.logging.LogWriter;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ThreadedLogWriter
implements AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private LogWriter<SentTelemetryEvent> writer;
    final private SimpleConsecutiveExecutor executor;

    public ThreadedLogWriter(FileChannel channel, Executor executor) {
        this.writer = new LogWriter<SentTelemetryEvent>(SentTelemetryEvent.CODEC, channel);
        this.executor = new SimpleConsecutiveExecutor(executor, "telemetry-event-log");
    }

    public TelemetryLogger getLogger() {
        return event -> this.executor.send(() -> {
            try {
                this.writer.write(event);
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to write telemetry event to log", (Throwable)iOException);
            }
        });
    }

    @Override
    public void close() {
        this.executor.send(() -> IOUtils.closeQuietly(this.writer));
        this.executor.close();
    }
}

