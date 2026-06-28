/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogListeners
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.event.Level
 */
package net.minecraft.client.util.tracy;

import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogListeners;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.event.Level;

@Environment(value=EnvType.CLIENT)
public class TracyLoader {
    static private boolean loaded;

    public static void load() {
        if (loaded) {
            return;
        }
        TracyClient.load();
        if (!TracyClient.isAvailable()) {
            return;
        }
        LogListeners.addListener((String)"Tracy", (message, level) -> TracyClient.message((String)message, (int)TracyLoader.getColor(level)));
        loaded = true;
    }

    private static int getColor(Level level) {
        return switch (level) {
            default -> 0xFFFFFF;
            case Level.DEBUG -> 0xAAAAAA;
            case Level.WARN -> 0xFFFFAA;
            case Level.ERROR -> 0xFFAAAA;
        };
    }
}

