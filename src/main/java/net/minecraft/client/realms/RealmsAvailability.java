/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsClientIncompatibleScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsParentalConsentScreen;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsAvailability {
    final static private Logger LOGGER = LogUtils.getLogger();
    @Nullable
    static private CompletableFuture<Info> currentFuture;

    public static CompletableFuture<Info> check() {
        if (currentFuture == null || RealmsAvailability.wasUnsuccessful(currentFuture)) {
            currentFuture = RealmsAvailability.checkInternal();
        }
        return currentFuture;
    }

    private static boolean wasUnsuccessful(CompletableFuture<Info> future) {
        Info info = future.getNow(null);
        return info != null && info.exception() != null;
    }

    private static CompletableFuture<Info> checkInternal() {
        Session session = MinecraftClient.getInstance().getSession();
        if (session.getAccountType() != Session.AccountType.MSA) {
            return CompletableFuture.completedFuture(new Info(Type.AUTHENTICATION_ERROR));
        }
        return CompletableFuture.supplyAsync(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            try {
                if (realmsClient.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                    return new Info(Type.INCOMPATIBLE_CLIENT);
                }
                if (!realmsClient.mcoEnabled()) {
                    return new Info(Type.NEEDS_PARENTAL_CONSENT);
                }
                return new Info(Type.SUCCESS);
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't connect to realms", (Throwable)realmsServiceException);
                if (realmsServiceException.error.getErrorCode() == 401) {
                    return new Info(Type.AUTHENTICATION_ERROR);
                }
                return new Info(realmsServiceException);
            }
        }, Util.getIoWorkerExecutor());
    }

    @Environment(value=EnvType.CLIENT)
    public record Info(Type type, @Nullable RealmsServiceException exception) {
        public Info(Type type) {
            this(type, null);
        }

        public Info(RealmsServiceException exception) {
            this(Type.UNEXPECTED_ERROR, exception);
        }

        @Nullable
        public Screen createScreen(Screen parent) {
            return switch (this.type.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> null;
                case 1 -> new RealmsClientIncompatibleScreen(parent);
                case 2 -> new RealmsParentalConsentScreen(parent);
                case 3 -> new RealmsGenericErrorScreen(Text.translatable("mco.error.invalid.session.title"), Text.translatable("mco.error.invalid.session.message"), parent);
                case 4 -> new RealmsGenericErrorScreen(Objects.requireNonNull(this.exception), parent);
            };
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        final static public Type SUCCESS = new Type();
        final static public Type INCOMPATIBLE_CLIENT = new Type();
        final static public Type NEEDS_PARENTAL_CONSENT = new Type();
        final static public Type AUTHENTICATION_ERROR = new Type();
        final static public Type UNEXPECTED_ERROR = new Type();
        final static private Type[] field_45190;

        public static Type[] values() {
            return (Type[])field_45190.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static Type[] method_52629() {
            return new Type[]{SUCCESS, INCOMPATIBLE_CLIENT, NEEDS_PARENTAL_CONSENT, AUTHENTICATION_ERROR, UNEXPECTED_ERROR};
        }

        static {
            field_45190 = Type.method_52629();
        }
    }
}

