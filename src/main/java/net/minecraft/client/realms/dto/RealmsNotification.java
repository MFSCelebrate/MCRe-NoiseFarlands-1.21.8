/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.RealmsText;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsNotification {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private String NOTIFICATION_UUID_KEY = "notificationUuid";
    final static private String DISMISSABLE_KEY = "dismissable";
    final static private String SEEN_KEY = "seen";
    final static private String TYPE_KEY = "type";
    final static private String VISIT_URL_TYPE = "visitUrl";
    final static private String INFO_POPUP_TYPE = "infoPopup";
    final static Text OPEN_LINK_TEXT = Text.translatable("mco.notification.visitUrl.buttonText.default");
    final UUID uuid;
    final boolean dismissable;
    final boolean seen;
    final String type;

    RealmsNotification(UUID uuid, boolean dismissable, boolean seen, String type) {
        this.uuid = uuid;
        this.dismissable = dismissable;
        this.seen = seen;
        this.type = type;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public boolean isDismissable() {
        return this.dismissable;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static List<RealmsNotification> parse(String json) {
        ArrayList<RealmsNotification> list = new ArrayList<RealmsNotification>();
        try {
            JsonArray jsonArray = LenientJsonParser.parse(json).getAsJsonObject().get("notifications").getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                list.add(RealmsNotification.fromJson(jsonElement.getAsJsonObject()));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse list of RealmsNotifications", (Throwable)exception);
        }
        return list;
    }

    private static RealmsNotification fromJson(JsonObject json) {
        UUID uUID = JsonUtils.getUuidOr(NOTIFICATION_UUID_KEY, json, null);
        if (uUID == null) {
            throw new IllegalStateException("Missing required property notificationUuid");
        }
        boolean bl = JsonUtils.getBooleanOr(DISMISSABLE_KEY, json, true);
        boolean bl2 = JsonUtils.getBooleanOr(SEEN_KEY, json, false);
        String string = JsonUtils.getString(TYPE_KEY, json);
        RealmsNotification realmsNotification = new RealmsNotification(uUID, bl, bl2, string);
        return switch (string) {
            case VISIT_URL_TYPE -> VisitUrl.fromJson(realmsNotification, json);
            case INFO_POPUP_TYPE -> InfoPopup.fromJson(realmsNotification, json);
            default -> realmsNotification;
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class VisitUrl
    extends RealmsNotification {
        final static private String URL_KEY = "url";
        final static private String BUTTON_TEXT_KEY = "buttonText";
        final static private String MESSAGE_KEY = "message";
        final private String url;
        final private RealmsText buttonText;
        final private RealmsText message;

        private VisitUrl(RealmsNotification parent, String url, RealmsText buttonText, RealmsText message) {
            super(parent.uuid, parent.dismissable, parent.seen, parent.type);
            this.url = url;
            this.buttonText = buttonText;
            this.message = message;
        }

        public static VisitUrl fromJson(RealmsNotification parent, JsonObject json) {
            String string = JsonUtils.getString(URL_KEY, json);
            RealmsText realmsText = JsonUtils.get(BUTTON_TEXT_KEY, json, RealmsText::fromJson);
            RealmsText realmsText2 = JsonUtils.get(MESSAGE_KEY, json, RealmsText::fromJson);
            return new VisitUrl(parent, string, realmsText, realmsText2);
        }

        public Text getDefaultMessage() {
            return this.message.toText(Text.translatable("mco.notification.visitUrl.message.default"));
        }

        public ButtonWidget createButton(Screen currentScreen) {
            Text text = this.buttonText.toText(OPEN_LINK_TEXT);
            return ButtonWidget.builder(text, ConfirmLinkScreen.opening(currentScreen, this.url)).build();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class InfoPopup
    extends RealmsNotification {
        final static private String TITLE_KEY = "title";
        final static private String MESSAGE_KEY = "message";
        final static private String IMAGE_KEY = "image";
        final static private String URL_BUTTON_KEY = "urlButton";
        final private RealmsText title;
        final private RealmsText message;
        final private Identifier image;
        @Nullable
        final private UrlButton urlButton;

        private InfoPopup(RealmsNotification parent, RealmsText title, RealmsText message, Identifier image, @Nullable UrlButton urlButton) {
            super(parent.uuid, parent.dismissable, parent.seen, parent.type);
            this.title = title;
            this.message = message;
            this.image = image;
            this.urlButton = urlButton;
        }

        public static InfoPopup fromJson(RealmsNotification parent, JsonObject json) {
            RealmsText realmsText = JsonUtils.get(TITLE_KEY, json, RealmsText::fromJson);
            RealmsText realmsText2 = JsonUtils.get(MESSAGE_KEY, json, RealmsText::fromJson);
            Identifier identifier = Identifier.of(JsonUtils.getString(IMAGE_KEY, json));
            UrlButton urlButton = JsonUtils.getNullable(URL_BUTTON_KEY, json, UrlButton::fromJson);
            return new InfoPopup(parent, realmsText, realmsText2, identifier, urlButton);
        }

        @Nullable
        public PopupScreen createScreen(Screen backgroundScreen, Consumer<UUID> dismissCallback) {
            Text text = this.title.toText();
            if (text == null) {
                LOGGER.warn("Realms info popup had title with no available translation: {}", (Object)this.title);
                return null;
            }
            PopupScreen.Builder builder = new PopupScreen.Builder(backgroundScreen, text).image(this.image).message(this.message.toText(ScreenTexts.EMPTY));
            if (this.urlButton != null) {
                builder.button(this.urlButton.urlText.toText(OPEN_LINK_TEXT), screen -> {
                    MinecraftClient minecraftClient = MinecraftClient.getInstance();
                    minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
                        if (confirmed) {
                            Util.getOperatingSystem().open(this.urlButton.url);
                            minecraftClient.setScreen(backgroundScreen);
                        } else {
                            minecraftClient.setScreen((Screen)screen);
                        }
                    }, this.urlButton.url, true));
                    dismissCallback.accept(this.getUuid());
                });
            }
            builder.button(ScreenTexts.OK, screen -> {
                screen.close();
                dismissCallback.accept(this.getUuid());
            });
            builder.onClosed(() -> dismissCallback.accept(this.getUuid()));
            return builder.build();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class UrlButton
    extends Record {
        final String url;
        final RealmsText urlText;
        final static private String URL_KEY = "url";
        final static private String URL_TEXT_KEY = "urlText";

        private UrlButton(String string, RealmsText realmsText) {
            this.url = string;
            this.urlText = realmsText;
        }

        public static UrlButton fromJson(JsonObject json) {
            String string = JsonUtils.getString(URL_KEY, json);
            RealmsText realmsText = JsonUtils.get(URL_TEXT_KEY, json, RealmsText::fromJson);
            return new UrlButton(string, realmsText);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{UrlButton.class, "url;urlText", "url", "urlText"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UrlButton.class, "url;urlText", "url", "urlText"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UrlButton.class, "url;urlText", "url", "urlText"}, this, object);
        }

        public String url() {
            return this.url;
        }

        public RealmsText urlText() {
            return this.urlText;
        }
    }
}

