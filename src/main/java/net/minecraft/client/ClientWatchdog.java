/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import java.io.File;
import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.util.crash.CrashReport;

@Environment(value=EnvType.CLIENT)
public class ClientWatchdog {
    final static private Duration TIMEOUT = Duration.ofSeconds(15L);

    public static void shutdownClient(File runDir, long threadId) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(TIMEOUT);
            }
            catch (InterruptedException interruptedException) {
                return;
            }
            CrashReport crashReport = DedicatedServerWatchdog.createCrashReport("Client shutdown", threadId);
            MinecraftClient.saveCrashReport(runDir, crashReport);
        });
        thread.setDaemon(true);
        thread.setName("Client shutdown watchdog");
        thread.start();
    }
}

