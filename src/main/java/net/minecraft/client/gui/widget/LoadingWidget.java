/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LoadingWidget
extends ClickableWidget {
    final private TextRenderer textRenderer;

    public LoadingWidget(TextRenderer textRenderer, Text message) {
        super(0, 0, textRenderer.getWidth(message), textRenderer.fontHeight * 3, message);
        this.textRenderer = textRenderer;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.getX() + this.getWidth() / 2;
        int j = this.getY() + this.getHeight() / 2;
        Text text = this.getMessage();
        context.drawTextWithShadow(this.textRenderer, text, i - this.textRenderer.getWidth(text) / 2, j - this.textRenderer.fontHeight, Colors.WHITE);
        String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
        context.drawTextWithShadow(this.textRenderer, string, i - this.textRenderer.getWidth(string) / 2, j + this.textRenderer.fontHeight, Colors.GRAY);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public boolean isNarratable() {
        return false;
    }

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }
}

