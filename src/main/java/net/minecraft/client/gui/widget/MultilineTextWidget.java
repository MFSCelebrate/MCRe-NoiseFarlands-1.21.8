/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultilineTextWidget
extends AbstractTextWidget {
    private OptionalInt maxWidth = OptionalInt.empty();
    private OptionalInt maxRows = OptionalInt.empty();
    final private CachedMapper<CacheKey, MultilineText> cacheKeyToText = Util.cachedMapper(cacheKey -> {
        if (cacheKey.maxRows.isPresent()) {
            return MultilineText.create(textRenderer, cacheKey.maxWidth, cacheKey.maxRows.getAsInt(), cacheKey.message);
        }
        return MultilineText.create(textRenderer, cacheKey.message, cacheKey.maxWidth);
    });
    private boolean centered = false;
    private boolean allowHoverEvents = false;
    @Nullable
    private Consumer<Style> onClick = null;

    public MultilineTextWidget(Text message, TextRenderer textRenderer) {
        this(0, 0, message, textRenderer);
    }

    public MultilineTextWidget(int x, int y, Text message, TextRenderer textRenderer) {
        super(x, y, 0, 0, message, textRenderer);
        this.active = false;
    }

    @Override
    public MultilineTextWidget net_minecraft_client_gui_widget_MultilineTextWidget_setTextColor(int i) {
        super.net_minecraft_client_gui_widget_AbstractTextWidget_setTextColor(i);
        return this;
    }

    public MultilineTextWidget setMaxWidth(int maxWidth) {
        this.maxWidth = OptionalInt.of(maxWidth);
        return this;
    }

    public MultilineTextWidget setMaxRows(int maxRows) {
        this.maxRows = OptionalInt.of(maxRows);
        return this;
    }

    public MultilineTextWidget setCentered(boolean centered) {
        this.centered = centered;
        return this;
    }

    public MultilineTextWidget setStyleConfig(boolean allowHoverEvents, @Nullable Consumer<Style> onClick) {
        this.allowHoverEvents = allowHoverEvents;
        this.onClick = onClick;
        return this;
    }

    @Override
    public int getWidth() {
        return this.cacheKeyToText.map(this.getCacheKey()).getMaxWidth();
    }

    @Override
    public int getHeight() {
        return this.cacheKeyToText.map(this.getCacheKey()).count() * this.getTextRenderer().fontHeight;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        MultilineText multilineText = this.cacheKeyToText.map(this.getCacheKey());
        int i = this.getX();
        int j = this.getY();
        int k = this.getTextRenderer().fontHeight;
        int l = this.getTextColor();
        if (this.centered) {
            multilineText.drawCenterWithShadow(context, i + this.getWidth() / 2, j, k, l);
        } else {
            multilineText.drawWithShadow(context, i, j, k, l);
        }
        if (this.allowHoverEvents) {
            Style style = this.getStyleAt(mouseX, mouseY);
            if (this.isHovered()) {
                context.drawHoverEvent(this.getTextRenderer(), style, mouseX, mouseY);
            }
        }
    }

    @Nullable
    private Style getStyleAt(double mouseX, double mouseY) {
        MultilineText multilineText = this.cacheKeyToText.map(this.getCacheKey());
        int i = this.getX();
        int j = this.getY();
        int k = this.getTextRenderer().fontHeight;
        if (this.centered) {
            return multilineText.getStyleAtCentered(i + this.getWidth() / 2, j, k, mouseX, mouseY);
        }
        return multilineText.getStyleAtLeftAligned(i, j, k, mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Style style;
        if (this.onClick != null && (style = this.getStyleAt(mouseX, mouseY)) != null) {
            this.onClick.accept(style);
            return;
        }
        super.onClick(mouseX, mouseY);
    }

    private CacheKey getCacheKey() {
        return new CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
    }

    @Override
    public AbstractTextWidget net_minecraft_client_gui_widget_AbstractTextWidget_setTextColor(int textColor) {
        return this.net_minecraft_client_gui_widget_MultilineTextWidget_setTextColor(textColor);
    }

    @Environment(value=EnvType.CLIENT)
    static final class CacheKey
    extends Record {
        final Text message;
        final int maxWidth;
        final OptionalInt maxRows;

        CacheKey(Text text, int i, OptionalInt optionalInt) {
            this.message = text;
            this.maxWidth = i;
            this.maxRows = optionalInt;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this, object);
        }

        public Text message() {
            return this.message;
        }

        public int maxWidth() {
            return this.maxWidth;
        }

        public OptionalInt maxRows() {
            return this.maxRows;
        }
    }
}

