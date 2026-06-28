/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MessageScreen
extends Screen {
    @Nullable
    private NarratedMultilineTextWidget textWidget;

    public MessageScreen(Text text) {
        super(text);
    }

    @Override
    protected void init() {
        this.textWidget = this.addDrawableChild(new NarratedMultilineTextWidget(this.width, this.title, this.textRenderer, 12));
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        if (this.textWidget != null) {
            this.textWidget.initMaxWidth(this.width);
            this.textWidget.setPosition(this.width / 2 - this.textWidget.getWidth() / 2, this.height / 2 - this.textRenderer.fontHeight / 2);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean hasUsageText() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderPanoramaBackground(context, deltaTicks);
        this.applyBlur(context);
        this.renderDarkening(context);
    }
}

