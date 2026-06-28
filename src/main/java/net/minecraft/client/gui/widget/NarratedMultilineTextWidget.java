/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class NarratedMultilineTextWidget
extends MultilineTextWidget {
    final static public int DEFAULT_MARGIN = 4;
    final private boolean alwaysShowBorders;
    final private boolean fillBackground;
    final private int margin;

    public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer) {
        this(maxWidth, message, textRenderer, 4);
    }

    public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer, int margin) {
        this(maxWidth, message, textRenderer, true, true, margin);
    }

    public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer, boolean alwaysShowBorders, boolean fillBackground, int margin) {
        super(message, textRenderer);
        this.setMaxWidth(maxWidth);
        this.setCentered(true);
        this.active = true;
        this.alwaysShowBorders = alwaysShowBorders;
        this.fillBackground = fillBackground;
        this.margin = margin;
    }

    public void initMaxWidth(int baseWidth) {
        this.setMaxWidth(baseWidth - this.margin * 4);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.getX() - this.margin;
        int j = this.getY() - this.margin;
        int k = this.getWidth() + this.margin * 2;
        int l = this.getHeight() + this.margin * 2;
        int m = ColorHelper.withAlpha(this.alpha, this.alwaysShowBorders ? (this.isFocused() ? Colors.WHITE : Colors.LIGHT_GRAY) : Colors.WHITE);
        if (this.fillBackground) {
            context.fill(i + 1, j, i + k, j + l, ColorHelper.withAlpha(this.alpha, Colors.BLACK));
        }
        if (this.isFocused() || this.alwaysShowBorders) {
            context.drawBorder(i, j, k, l, m);
        }
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }
}

