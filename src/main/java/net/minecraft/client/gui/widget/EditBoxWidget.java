/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class EditBoxWidget
extends ScrollableTextFieldWidget {
    final static private int CURSOR_PADDING = 1;
    final static private int CURSOR_COLOR = -3092272;
    final static private String UNDERSCORE = "_";
    final static private int FOCUSED_BOX_TEXT_COLOR = -2039584;
    final static private int UNFOCUSED_BOX_TEXT_COLOR = -857677600;
    final static private int CURSOR_BLINK_INTERVAL = 300;
    final private TextRenderer textRenderer;
    final private Text placeholder;
    final private EditBox editBox;
    final private int textColor;
    final private boolean textShadow;
    final private int cursorColor;
    private long lastSwitchFocusTime = Util.getMeasuringTimeMs();

    EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message, int textColor, boolean textShadow, int cursorColor, boolean hasBackground, boolean hasOverlay) {
        super(x, y, width, height, message, hasBackground, hasOverlay);
        this.textRenderer = textRenderer;
        this.textShadow = textShadow;
        this.textColor = textColor;
        this.cursorColor = cursorColor;
        this.placeholder = placeholder;
        this.editBox = new EditBox(textRenderer, width - this.getPadding());
        this.editBox.setCursorChangeListener(this::onCursorChange);
    }

    public void setMaxLength(int maxLength) {
        this.editBox.setMaxLength(maxLength);
    }

    public void setMaxLines(int maxLines) {
        this.editBox.setMaxLines(maxLines);
    }

    public void setChangeListener(Consumer<String> changeListener) {
        this.editBox.setChangeListener(changeListener);
    }

    public void setText(String text) {
        this.setText(text, false);
    }

    public void setText(String text, boolean allowOverflow) {
        this.editBox.setText(text, allowOverflow);
    }

    public String getText() {
        return this.editBox.getText();
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)Text.translatable("gui.narrate.editBox", this.getMessage(), this.getText()));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.editBox.setSelecting(Screen.hasShiftDown());
        this.moveCursor(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.editBox.setSelecting(true);
        this.moveCursor(mouseX, mouseY);
        this.editBox.setSelecting(Screen.hasShiftDown());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.editBox.handleSpecialKey(keyCode);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!(this.visible && this.isFocused() && StringHelper.isValidChar(chr))) {
            return false;
        }
        this.editBox.replaceSelection(Character.toString(chr));
        return true;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        String string = this.editBox.getText();
        if (string.isEmpty() && !this.isFocused()) {
            context.drawWrappedTextWithShadow(this.textRenderer, this.placeholder, this.getTextX(), this.getTextY(), this.width - this.getPadding(), -857677600);
            return;
        }
        int i = this.editBox.getCursor();
        boolean bl = this.isFocused() && (Util.getMeasuringTimeMs() - this.lastSwitchFocusTime) / 300L % 2L == 0L;
        boolean bl2 = i < string.length();
        int j = 0;
        int k = 0;
        int l = this.getTextY();
        for (EditBox.Substring substring : this.editBox.getLines()) {
            boolean bl3 = this.isVisible(l, l + this.textRenderer.fontHeight);
            int m = this.getTextX();
            if (bl && bl2 && i >= substring.beginIndex() && i < substring.endIndex()) {
                if (bl3) {
                    string2 = string.substring(substring.beginIndex(), i);
                    context.drawText(this.textRenderer, string2, m, l, this.textColor, this.textShadow);
                    j = m + this.textRenderer.getWidth(string2);
                    context.fill(j, l - 1, j + 1, l + 1 + this.textRenderer.fontHeight, this.cursorColor);
                    context.drawText(this.textRenderer, string.substring(i, substring.endIndex()), j, l, this.textColor, this.textShadow);
                }
            } else {
                if (bl3) {
                    string2 = string.substring(substring.beginIndex(), substring.endIndex());
                    context.drawText(this.textRenderer, string2, m, l, this.textColor, this.textShadow);
                    j = m + this.textRenderer.getWidth(string2) - 1;
                }
                k = l;
            }
            l += this.textRenderer.fontHeight;
        }
        if (bl && !bl2 && this.isVisible(k, k + this.textRenderer.fontHeight)) {
            context.drawText(this.textRenderer, UNDERSCORE, j, k, this.cursorColor, this.textShadow);
        }
        if (this.editBox.hasSelection()) {
            EditBox.Substring substring2 = this.editBox.getSelection();
            int n = this.getTextX();
            l = this.getTextY();
            for (EditBox.Substring substring3 : this.editBox.getLines()) {
                if (substring2.beginIndex() > substring3.endIndex()) {
                    l += this.textRenderer.fontHeight;
                    continue;
                }
                if (substring3.beginIndex() > substring2.endIndex()) break;
                if (this.isVisible(l, l + this.textRenderer.fontHeight)) {
                    int o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                    int p = substring2.endIndex() > substring3.endIndex() ? this.width - this.getTextMargin() : this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                    context.drawSelection(n + o, l, n + p, l + this.textRenderer.fontHeight);
                }
                l += this.textRenderer.fontHeight;
            }
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        super.renderOverlay(context);
        if (this.editBox.hasMaxLength()) {
            int i = this.editBox.getMaxLength();
            MutableText text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
            context.drawTextWithShadow(this.textRenderer, text, this.getX() + this.width - this.textRenderer.getWidth(text), this.getY() + this.height + 4, Colors.LIGHT_GRAY);
        }
    }

    @Override
    public int getContentsHeight() {
        return this.textRenderer.fontHeight * this.editBox.getLineCount();
    }

    @Override
    protected double getDeltaYPerScroll() {
        return (double)this.textRenderer.fontHeight / 2.0;
    }

    private void onCursorChange() {
        double d = this.getScrollY();
        EditBox.Substring substring = this.editBox.getLine((int)(d / (double)this.textRenderer.fontHeight));
        if (this.editBox.getCursor() <= substring.beginIndex()) {
            d = this.editBox.getCurrentLineIndex() * this.textRenderer.fontHeight;
        } else {
            EditBox.Substring substring2 = this.editBox.getLine((int)((d + (double)this.height) / (double)this.textRenderer.fontHeight) - 1);
            if (this.editBox.getCursor() > substring2.endIndex()) {
                d = this.editBox.getCurrentLineIndex() * this.textRenderer.fontHeight - this.height + this.textRenderer.fontHeight + this.getPadding();
            }
        }
        this.setScrollY(d);
    }

    private void moveCursor(double mouseX, double mouseY) {
        double d = mouseX - (double)this.getX() - (double)this.getTextMargin();
        double e = mouseY - (double)this.getY() - (double)this.getTextMargin() + this.getScrollY();
        this.editBox.moveCursor(d, e);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.lastSwitchFocusTime = Util.getMeasuringTimeMs();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private int x;
        private int y;
        private Text placeholder = ScreenTexts.EMPTY;
        private int textColor = -2039584;
        private boolean textShadow = true;
        private int cursorColor = -3092272;
        private boolean hasBackground = true;
        private boolean hasOverlay = true;

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder placeholder(Text placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder textShadow(boolean textShadow) {
            this.textShadow = textShadow;
            return this;
        }

        public Builder cursorColor(int cursorColor) {
            this.cursorColor = cursorColor;
            return this;
        }

        public Builder hasBackground(boolean hasBackground) {
            this.hasBackground = hasBackground;
            return this;
        }

        public Builder hasOverlay(boolean hasOverlay) {
            this.hasOverlay = hasOverlay;
            return this;
        }

        public EditBoxWidget build(TextRenderer textRenderer, int width, int height, Text message) {
            return new EditBoxWidget(textRenderer, this.x, this.y, width, height, this.placeholder, message, this.textColor, this.textShadow, this.cursorColor, this.hasBackground, this.hasOverlay);
        }
    }
}

