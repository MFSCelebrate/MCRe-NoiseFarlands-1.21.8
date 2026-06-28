/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public interface RealmsError {
    final static public Text NO_DETAILS_TEXT = Text.translatable("mco.errorMessage.noDetails");
    final static public Logger LOGGER = LogUtils.getLogger();

    public int getErrorCode();

    public Text getText();

    public String getErrorMessage();

    public static RealmsError ofHttp(int statusCode, String response) {
        if (statusCode == 429) {
            return SimpleHttpError.SERVICE_BUSY;
        }
        if (Strings.isNullOrEmpty((String)response)) {
            return SimpleHttpError.statusCodeOnly(statusCode);
        }
        try {
            JsonObject jsonObject = LenientJsonParser.parse(response).getAsJsonObject();
            String string = JsonHelper.getString(jsonObject, "reason", null);
            String string2 = JsonHelper.getString(jsonObject, "errorMsg", null);
            int i = JsonHelper.getInt(jsonObject, "errorCode", -1);
            if (string2 != null || string != null || i != -1) {
                return new DetailedHttpError(statusCode, i != -1 ? i : statusCode, string, string2);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsError", (Throwable)exception);
        }
        return new RawHttpPayloadError(statusCode, response);
    }

    @Environment(value=EnvType.CLIENT)
    public record SimpleHttpError(int httpCode, @Nullable Text payload) implements RealmsError
    {
        final static public SimpleHttpError SERVICE_BUSY = new SimpleHttpError(429, Text.translatable("mco.errorMessage.serviceBusy"));
        final static public Text RETRY_TEXT = Text.translatable("mco.errorMessage.retry");
        final static public String BODY_HTML_OPENING_TAG = "<body>";
        final static public String BODY_HTML_CLOSING_TAG = "</body>";

        public static SimpleHttpError unknownCompatibility(String response) {
            return new SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.unknownCompatibility", response));
        }

        public static SimpleHttpError configurationError() {
            return new SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.configurationError"));
        }

        public static SimpleHttpError connectivity(RealmsHttpException exception) {
            return new SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.connectivity", exception.getMessage()));
        }

        public static SimpleHttpError retryable(int statusCode) {
            return new SimpleHttpError(statusCode, RETRY_TEXT);
        }

        public static SimpleHttpError statusCodeOnly(int statusCode) {
            return new SimpleHttpError(statusCode, null);
        }

        public static SimpleHttpError unreadableHtmlBody(int statusCode, String html) {
            int i = html.indexOf(BODY_HTML_OPENING_TAG);
            int j = html.indexOf(BODY_HTML_CLOSING_TAG);
            if (i >= 0 && j > i) {
                return new SimpleHttpError(statusCode, Text.literal(html.substring(i + BODY_HTML_OPENING_TAG.length(), j).trim()));
            }
            LOGGER.error("Got an error with an unreadable html body {}", (Object)html);
            return new SimpleHttpError(statusCode, null);
        }

        @Override
        public int getErrorCode() {
            return this.httpCode;
        }

        @Override
        public Text getText() {
            return this.payload != null ? this.payload : NO_DETAILS_TEXT;
        }

        @Override
        public String getErrorMessage() {
            if (this.payload != null) {
                return String.format(Locale.ROOT, "Realms service error (%d) with message '%s'", this.httpCode, this.payload.getString());
            }
            return String.format(Locale.ROOT, "Realms service error (%d) with no payload", this.httpCode);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record DetailedHttpError(int httpCode, int code, @Nullable String reason, @Nullable String message) implements RealmsError
    {
        @Override
        public int getErrorCode() {
            return this.code;
        }

        @Override
        public Text getText() {
            String string2;
            String string = "mco.errorMessage." + this.code;
            if (I18n.hasTranslation(string)) {
                return Text.translatable(string);
            }
            if (this.reason != null && I18n.hasTranslation(string2 = "mco.errorReason." + this.reason)) {
                return Text.translatable(string2);
            }
            return this.message != null ? Text.literal(this.message) : NO_DETAILS_TEXT;
        }

        @Override
        public String getErrorMessage() {
            return String.format(Locale.ROOT, "Realms service error (%d/%d/%s) with message '%s'", this.httpCode, this.code, this.reason, this.message);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record RawHttpPayloadError(int httpCode, String payload) implements RealmsError
    {
        @Override
        public int getErrorCode() {
            return this.httpCode;
        }

        @Override
        public Text getText() {
            return Text.literal(this.payload);
        }

        @Override
        public String getErrorMessage() {
            return String.format(Locale.ROOT, "Realms service error (%d) with raw payload '%s'", this.httpCode, this.payload);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record AuthenticationError(String message) implements RealmsError
    {
        final static public int ERROR_CODE = 401;

        @Override
        public int getErrorCode() {
            return 401;
        }

        @Override
        public Text getText() {
            return Text.literal(this.message);
        }

        @Override
        public String getErrorMessage() {
            return String.format(Locale.ROOT, "Realms authentication error with message '%s'", this.message);
        }
    }
}

