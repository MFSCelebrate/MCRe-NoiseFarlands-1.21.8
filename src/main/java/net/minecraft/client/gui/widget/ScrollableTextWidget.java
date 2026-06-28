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
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ScrollableTextWidget
extends ScrollableTextFieldWidget {
    final private TextRenderer textRenderer;
    final private MultilineTextWidget wrapped;

    public ScrollableTextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.wrapped = new MultilineTextWidget(message, textRenderer).setMaxWidth(this.getWidth() - this.getPadding());
    }

    public ScrollableTextWidget textColor(int textColor) {
        this.wrapped.net_minecraft_client_gui_widget_MultilineTextWidget_setTextColor(textColor);
        return this;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.wrapped.setMaxWidth(this.getWidth() - this.getPadding());
    }

    @Override
    protected int getContentsHeight() {
        return this.wrapped.getHeight();
    }

    @Override
    protected double getDeltaYPerScroll() {
        return this.textRenderer.fontHeight;
    }

    @Override
    protected void drawBox(DrawContext context) {
        super.drawBox(context);
    }

    public boolean textOverflows() {
        return super.overflows();
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)this.getTextX(), (float)this.getTextY());
        this.wrapped.render(context, mouseX, mouseY, deltaTicks);
        context.getMatrices().popMatrix();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }

    @Override
    public void setMessage(Text message) {
        super.setMessage(message);
        this.wrapped.setMessage(message);
    }
}

