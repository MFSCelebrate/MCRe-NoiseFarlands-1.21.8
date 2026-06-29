/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUtil {
    final static private Logger field_61054 = LogUtils.getLogger();
    final static private Text NOW_TEXT = Text.translatable("mco.util.time.now");
    final static private int SECONDS_PER_MINUTE = 60;
    final static private int SECONDS_PER_HOUR = 3600;
    final static private int SECONDS_PER_DAY = 86400;

    public static Text convertToAgePresentation(long milliseconds) {
        if (milliseconds < 0L) {
            return NOW_TEXT;
        }
        long l = milliseconds / 1000L;
        if (l < 60L) {
            return Text.translatable("mco.time.secondsAgo", l);
        }
        if (l < 3600L) {
            long m = l / 60L;
            return Text.translatable("mco.time.minutesAgo", m);
        }
        if (l < 86400L) {
            long m = l / 3600L;
            return Text.translatable("mco.time.hoursAgo", m);
        }
        long m = l / 86400L;
        return Text.translatable("mco.time.daysAgo", m);
    }

    public static Text convertToAgePresentation(Date date) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date.getTime());
    }

    public static void drawPlayerHead(DrawContext context, int x, int y, int size, UUID playerUuid) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ProfileResult profileResult = minecraftClient.getSessionService().fetchProfile(playerUuid, false);
        SkinTextures skinTextures = profileResult != null ? minecraftClient.getSkinProvider().getSkinTextures(profileResult.profile()) : DefaultSkinHelper.getSkinTextures(playerUuid);
        PlayerSkinDrawer.draw(context, skinTextures, x, y, size);
    }

    public static <T> CompletableFuture<T> method_72217(class_11539<T> arg, @Nullable Consumer<RealmsServiceException> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            try {
                return arg.apply(realmsClient);
            }
            catch (Throwable throwable) {
                if (throwable instanceof RealmsServiceException) {
                    RealmsServiceException realmsServiceException = (RealmsServiceException)throwable;
                    if (consumer != null) {
                        consumer.accept(realmsServiceException);
                    }
                } else {
                    field_61054.error("Unhandled exception", throwable);
                }
                throw new RuntimeException(throwable);
            }
        }, Util.getDownloadWorkerExecutor());
    }

    public static CompletableFuture<Void> method_72216(class_11538 arg, @Nullable Consumer<RealmsServiceException> consumer) {
        return RealmsUtil.method_72217(arg, consumer);
    }

    public static Consumer<RealmsServiceException> method_72220(Function<RealmsServiceException, Screen> function) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return realmsServiceException -> minecraftClient.execute(() -> minecraftClient.setScreen((Screen)function.apply((RealmsServiceException)realmsServiceException)));
    }

    public static Consumer<RealmsServiceException> method_72221(Function<RealmsServiceException, Screen> function, String string) {
        return RealmsUtil.method_72220(function).andThen(realmsServiceException -> field_61054.error(string, (Throwable)realmsServiceException));
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface class_11539<T> {
        public T apply(RealmsClient var1) throws RealmsServiceException;
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface class_11538
    extends class_11539<Void> {
        public void accept(RealmsClient var1) throws RealmsServiceException;

        @Override
        default public Void apply(RealmsClient realmsClient) throws RealmsServiceException {
            this.accept(realmsClient);
            return null;
        }
    }
}

