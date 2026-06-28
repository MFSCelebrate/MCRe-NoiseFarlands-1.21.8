/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.InactivityFpsLimit;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class InactivityFpsLimiter {
    final static private int IN_GUI_FPS = 60;
    final static private int MINIMIZED_FPS = 10;
    final static private int AFK_STAGE_1_FPS = 30;
    final static private int AFK_STAGE_2_FPS = 10;
    final static private long AFK_STAGE_1_THRESHOLD = 60000L;
    final static private long AFK_STAGE_2_THRESHOLD = 600000L;
    final private GameOptions options;
    final private MinecraftClient client;
    private int maxFps;
    private long lastInputTime;

    public InactivityFpsLimiter(GameOptions options, MinecraftClient client) {
        this.options = options;
        this.client = client;
        this.maxFps = options.getMaxFps().getValue();
    }

    public int update() {
        return switch (this.getLimitReason().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.maxFps;
            case 1 -> 10;
            case 2 -> 10;
            case 3 -> Math.min(this.maxFps, 30);
            case 4 -> 60;
        };
    }

    public LimitReason getLimitReason() {
        InactivityFpsLimit inactivityFpsLimit = this.options.getInactivityFpsLimit().getValue();
        if (this.client.getWindow().isMinimized()) {
            return LimitReason.WINDOW_ICONIFIED;
        }
        if (inactivityFpsLimit == InactivityFpsLimit.AFK) {
            long l = Util.getMeasuringTimeMs() - this.lastInputTime;
            if (l > 600000L) {
                return LimitReason.LONG_AFK;
            }
            if (l > 60000L) {
                return LimitReason.SHORT_AFK;
            }
        }
        if (this.client.world == null && (this.client.currentScreen != null || this.client.getOverlay() != null)) {
            return LimitReason.OUT_OF_LEVEL_MENU;
        }
        return LimitReason.NONE;
    }

    public boolean shouldDisableProfilerTimeout() {
        LimitReason limitReason = this.getLimitReason();
        return limitReason == LimitReason.WINDOW_ICONIFIED || limitReason == LimitReason.LONG_AFK;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public void onInput() {
        this.lastInputTime = Util.getMeasuringTimeMs();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class LimitReason
    extends Enum<LimitReason> {
        final static public LimitReason NONE = new LimitReason();
        final static public LimitReason WINDOW_ICONIFIED = new LimitReason();
        final static public LimitReason LONG_AFK = new LimitReason();
        final static public LimitReason SHORT_AFK = new LimitReason();
        final static public LimitReason OUT_OF_LEVEL_MENU = new LimitReason();
        final static private LimitReason[] field_55848;

        public static LimitReason[] values() {
            return (LimitReason[])field_55848.clone();
        }

        public static LimitReason valueOf(String string) {
            return Enum.valueOf(LimitReason.class, string);
        }

        private static LimitReason[] method_66516() {
            return new LimitReason[]{NONE, WINDOW_ICONIFIED, LONG_AFK, SHORT_AFK, OUT_OF_LEVEL_MENU};
        }

        static {
            field_55848 = LimitReason.method_66516();
        }
    }
}

