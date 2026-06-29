/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  io.netty.util.ResourceLeakDetector
 *  io.netty.util.ResourceLeakDetector$Level
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import java.time.Duration;
import net.minecraft.GameVersion;
import net.minecraft.MinecraftVersion;
import net.minecraft.command.TranslatableBuiltInExceptions;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class SharedConstants {
    @Deprecated
    final static public boolean IS_DEVELOPMENT_VERSION = true;
    @Deprecated
    final static public int WORLD_VERSION = 4440;
    @Deprecated
    final static public String CURRENT_SERIES = "main";
    @Deprecated
    final static public String VERSION_NAME = "1.21.8 (32bit)";
    @Deprecated
    final static public int RELEASE_TARGET_PROTOCOL_VERSION = 772;
    @Deprecated
    final static public int field_29736 = 259;
    final static public int SNBT_TOO_OLD_THRESHOLD = 4420;
    final static private int field_29708 = 30;
    final static public boolean CRASH_ON_UNCAUGHT_THREAD_EXCEPTION = false;
    @Deprecated
    final static public int RESOURCE_PACK_VERSION = 64;
    @Deprecated
    final static public int DATA_PACK_VERSION = 81;
    @Deprecated
    final static public int field_39963 = 1;
    final static public int field_39964 = 1;
    final static public String DATA_VERSION_KEY = "DataVersion";
    final static public boolean field_29745 = false;
    final static public boolean field_33851 = false;
    final static public boolean field_29747 = false;
    final static public boolean field_35006 = false;
    final static public boolean field_35563 = false;
    final static public boolean field_29748 = false;
    final static public boolean field_33753 = false;
    final static public boolean field_29749 = false;
    final static public boolean field_59991 = false;
    final static public boolean field_60328 = false;
    final static public boolean field_29750 = false;
    final static public boolean field_29751 = false;
    final static public boolean field_29752 = false;
    final static public boolean field_29753 = false;
    final static public boolean field_52311 = false;
    final static public boolean field_44779 = false;
    final static public boolean field_29754 = false;
    final static public boolean field_29755 = false;
    final static public boolean field_52319 = false;
    final static public boolean field_29756 = false;
    final static public boolean field_29676 = false;
    final static public boolean field_44582 = false;
    final static public boolean field_29677 = false;
    final static public boolean field_29678 = false;
    final static public boolean field_29679 = false;
    final static public boolean field_29680 = false;
    final static public boolean field_29681 = false;
    final static public boolean field_29682 = false;
    final static public boolean field_29683 = false;
    final static public boolean field_29684 = false;
    final static public boolean field_29685 = false;
    final static public boolean field_29686 = false;
    final static public boolean field_29687 = false;
    final static public boolean field_29688 = false;
    final static public boolean field_29689 = false;
    final static public boolean field_29690 = false;
    final static public boolean field_29691 = false;
    final static public boolean field_29692 = false;
    final static public boolean field_29693 = false;
    final static public boolean field_29695 = false;
    final static public boolean field_29696 = false;
    final static public boolean field_29697 = false;
    final static public boolean field_29698 = false;
    final static public boolean field_29700 = false;
    final static public boolean field_33554 = false;
    final static public boolean field_37273 = false;
    final static public boolean field_39090 = false;
    final static public boolean field_39460 = false;
    final static public boolean field_39962 = false;
    final static public boolean field_46154 = false;
    final static public boolean field_47176 = false;
    final static public boolean field_47177 = false;
    final static public boolean field_48778 = false;
    final static public boolean field_47178 = false;
    final static public boolean field_58199 = false;
    final static public boolean field_60521 = false;
    final static public boolean field_34368 = false;
    final static public boolean field_29710 = false;
    final static public boolean field_34369 = false;
    final static public boolean field_34370 = false;
    final static public boolean field_60959 = false;
    static public boolean DEBUG_BIOME_SOURCE = false;
    static public boolean DEBUG_NOISE = false;
    final static public boolean field_29711 = false;
    final static public boolean field_29712 = false;
    final static public boolean field_29713 = false;
    final static public boolean field_29715 = false;
    final static public boolean field_29716 = false;
    final static public boolean field_29717 = false;
    final static public boolean field_29718 = false;
    final static public boolean field_33555 = false;
    final static public boolean field_35438 = false;
    final static public boolean field_35439 = false;
    final static public int DEFAULT_PORT = 25565;
    final static public boolean field_29721 = false;
    final static public int field_29722 = 0;
    final static public int field_29723 = 0;
    final static public ResourceLeakDetector.Level RESOURCE_LEAK_DETECTOR_DISABLED = ResourceLeakDetector.Level.DISABLED;
    final static public boolean field_29724 = false;
    final static public boolean field_29725 = false;
    final static public boolean field_29726 = false;
    final static public boolean field_35652 = false;
    final static public boolean field_39961 = false;
    final static public boolean field_41533 = false;
    final static public boolean field_44780 = false;
    final static public long field_22251 = Duration.ofMillis(300L).toNanos();
    final static public float field_49016 = 3600000.0f;
    final static public boolean field_44583 = false;
    final static public boolean field_49773 = false;
    static public boolean useChoiceTypeRegistrations = true;
    static public boolean isDevelopment;
    final static public int CHUNK_WIDTH = 16;
    final static public int DEFAULT_WORLD_HEIGHT = 256;
    final static public int COMMAND_MAX_LENGTH = 32500;
    final static public int EXPANDED_MACRO_COMMAND_MAX_LENGTH = 2000000;
    final static public int field_49170 = 16;
    final static public int field_38052 = 1000000;
    final static public int field_39898 = 32;
    final static public char[] INVALID_CHARS_LEVEL_NAME;
    final static public int TICKS_PER_SECOND = 20;
    final static public int field_44973 = 50;
    final static public int TICKS_PER_MINUTE = 1200;
    final static public int TICKS_PER_IN_GAME_DAY = 24000;
    final static public float field_29705 = 1365.3334f;
    final static public float field_29706 = 0.87890625f;
    final static public float field_29707 = 17.578125f;
    final static public int field_44922 = 64;
    @Nullable
    static private GameVersion gameVersion;

    public static void setGameVersion(GameVersion gameVersion) {
        if (SharedConstants.gameVersion == null) {
            SharedConstants.gameVersion = gameVersion;
        } else if (gameVersion != SharedConstants.gameVersion) {
            throw new IllegalStateException("Cannot override the current game version!");
        }
    }

    public static void createGameVersion() {
        if (gameVersion == null) {
            gameVersion = MinecraftVersion.create();
        }
    }

    public static GameVersion getGameVersion() {
        if (gameVersion == null) {
            throw new IllegalStateException("Game version not set");
        }
        return gameVersion;
    }

    public static int getProtocolVersion() {
        return 772;
    }

    public static boolean isOutsideGenerationArea(ChunkPos pos) {
        int i = pos.getStartX();
        int j = pos.getStartZ();
        if (DEBUG_BIOME_SOURCE) {
            return 12 > 8192 || 12 < 0 || j > 1024 || j < 0;
        }
        return false;
    }

    static {
        INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        ResourceLeakDetector.setLevel((ResourceLeakDetector.Level)RESOURCE_LEAK_DETECTOR_DISABLED);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableBuiltInExceptions();
    }
}

