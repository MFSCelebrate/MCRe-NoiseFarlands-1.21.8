/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.text.Style;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EditBox {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public int UNLIMITED_LENGTH = Integer.MAX_VALUE;
    final static private int CURSOR_WIDTH = 2;
    final private TextRenderer textRenderer;
    final private List<Substring> lines = Lists.newArrayList();
    private String text;
    private int cursor;
    private int selectionEnd;
    private boolean selecting;
    private int maxLength = Integer.MAX_VALUE;
    private int maxLines = Integer.MAX_VALUE;
    final private int width;
    private Consumer<String> changeListener = text -> {};
    private Runnable cursorChangeListener = () -> {};

    public EditBox(TextRenderer textRenderer, int width) {
        this.textRenderer = textRenderer;
        this.width = width;
        this.setText("");
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        }
        this.maxLength = maxLength;
    }

    public void setMaxLines(int maxLines) {
        if (maxLines < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        }
        this.maxLines = maxLines;
    }

    public boolean hasMaxLength() {
        return this.maxLength != Integer.MAX_VALUE;
    }

    public boolean hasMaxLines() {
        return this.maxLines != Integer.MAX_VALUE;
    }

    public void setChangeListener(Consumer<String> changeListener) {
        this.changeListener = changeListener;
    }

    public void setCursorChangeListener(Runnable cursorChangeListener) {
        this.cursorChangeListener = cursorChangeListener;
    }

    public void setText(String setText) {
        this.setText(setText, false);
    }

    public void setText(String text, boolean allowOverflow) {
        String string = this.truncateForReplacement(text);
        if (!allowOverflow && this.exceedsMaxLines(string)) {
            return;
        }
        this.text = string;
        this.selectionEnd = this.cursor = this.text.length();
        this.onChange();
    }

    public String getText() {
        return this.text;
    }

    public void replaceSelection(String string) {
        if (string.isEmpty() && !this.hasSelection()) {
            return;
        }
        String string2 = this.truncate(StringHelper.stripInvalidChars(string, true));
        Substring substring = this.getSelection();
        String string3 = new StringBuilder(this.text).replace(substring.beginIndex, substring.endIndex, string2).toString();
        if (this.exceedsMaxLines(string3)) {
            return;
        }
        this.text = string3;
        this.selectionEnd = this.cursor = substring.beginIndex + string2.length();
        this.onChange();
    }

    public void delete(int offset) {
        if (!this.hasSelection()) {
            this.selectionEnd = MathHelper.clamp(this.cursor + offset, 0, this.text.length());
        }
        this.replaceSelection("");
    }

    public int getCursor() {
        return this.cursor;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public Substring getSelection() {
        return new Substring(Math.min(this.selectionEnd, this.cursor), Math.max(this.selectionEnd, this.cursor));
    }

    public int getLineCount() {
        return this.lines.size();
    }

    public int getCurrentLineIndex() {
        for (int i = 0; i < this.lines.size(); ++i) {
            Substring substring = this.lines.get(i);
            if (this.cursor < substring.beginIndex || this.cursor > substring.endIndex) continue;
            return i;
        }
        return -1;
    }

    public Substring getLine(int index) {
        return this.lines.get(MathHelper.clamp(index, 0, this.lines.size() - 1));
    }

    public void moveCursor(CursorMovement movement, int amount) {
        switch (movement) {
            case ABSOLUTE: {
                this.cursor = amount;
                break;
            }
            case RELATIVE: {
                this.cursor += amount;
                break;
            }
            case END: {
                this.cursor = this.text.length() + amount;
            }
        }
        this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
        this.cursorChangeListener.run();
        if (!this.selecting) {
            this.selectionEnd = this.cursor;
        }
    }

    public void moveCursorLine(int offset) {
        if (offset == 0) {
            return;
        }
        int i = this.textRenderer.getWidth(this.text.substring(this.getCurrentLine().beginIndex, this.cursor)) + 2;
        Substring substring = this.getOffsetLine(offset);
        int j = this.textRenderer.trimToWidth(this.text.substring(substring.beginIndex, substring.endIndex), i).length();
        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex + j);
    }

    public void moveCursor(double x, double y) {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y / (double)this.textRenderer.fontHeight);
        Substring substring = this.lines.get(MathHelper.clamp(j, 0, this.lines.size() - 1));
        int k = this.textRenderer.trimToWidth(this.text.substring(substring.beginIndex, substring.endIndex), i).length();
        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex + k);
    }

    public boolean handleSpecialKey(int keyCode) {
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.cursor = this.text.length();
            this.selectionEnd = 0;
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            this.replaceSelection(MinecraftClient.getInstance().keyboard.getClipboard());
            return true;
        }
        if (Screen.isCut(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            this.replaceSelection("");
            return true;
        }
        switch (keyCode) {
            case 263: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getPreviousWordAtCursor();
                    this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex);
                } else {
                    this.moveCursor(CursorMovement.RELATIVE, -1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getNextWordAtCursor();
                    this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex);
                } else {
                    this.moveCursor(CursorMovement.RELATIVE, 1);
                }
                return true;
            }
            case 265: {
                if (!Screen.hasControlDown()) {
                    this.moveCursorLine(-1);
                }
                return true;
            }
            case 264: {
                if (!Screen.hasControlDown()) {
                    this.moveCursorLine(1);
                }
                return true;
            }
            case 266: {
                this.moveCursor(CursorMovement.ABSOLUTE, 0);
                return true;
            }
            case 267: {
                this.moveCursor(CursorMovement.END, 0);
                return true;
            }
            case 268: {
                if (Screen.hasControlDown()) {
                    this.moveCursor(CursorMovement.ABSOLUTE, 0);
                } else {
                    this.moveCursor(CursorMovement.ABSOLUTE, this.getCurrentLine().beginIndex);
                }
                return true;
            }
            case 269: {
                if (Screen.hasControlDown()) {
                    this.moveCursor(CursorMovement.END, 0);
                } else {
                    this.moveCursor(CursorMovement.ABSOLUTE, this.getCurrentLine().endIndex);
                }
                return true;
            }
            case 259: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getPreviousWordAtCursor();
                    this.delete(substring.beginIndex - this.cursor);
                } else {
                    this.delete(-1);
                }
                return true;
            }
            case 261: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getNextWordAtCursor();
                    this.delete(substring.beginIndex - this.cursor);
                } else {
                    this.delete(1);
                }
                return true;
            }
            case 257: 
            case 335: {
                this.replaceSelection("\n");
                return true;
            }
        }
        return false;
    }

    public Iterable<Substring> getLines() {
        return this.lines;
    }

    public boolean hasSelection() {
        return this.selectionEnd != this.cursor;
    }

    @VisibleForTesting
    public String getSelectedText() {
        Substring substring = this.getSelection();
        return this.text.substring(substring.beginIndex, substring.endIndex);
    }

    private Substring getCurrentLine() {
        return this.getOffsetLine(0);
    }

    private Substring getOffsetLine(int offsetFromCurrent) {
        int i = this.getCurrentLineIndex();
        if (i < 0) {
            LOGGER.error("Cursor is not within text (cursor = {}, length = {})", (Object)this.cursor, (Object)this.text.length());
            return this.lines.getLast();
        }
        return this.lines.get(MathHelper.clamp(i + offsetFromCurrent, 0, this.lines.size() - 1));
    }

    @VisibleForTesting
    public Substring getPreviousWordAtCursor() {
        int i;
        if (this.text.isEmpty()) {
            return Substring.EMPTY;
        }
        for (i = MathHelper.clamp(this.cursor, 0, this.text.length() - 1); i > 0 && Character.isWhitespace(this.text.charAt(i - 1)); --i) {
        }
        while (i > 0 && !Character.isWhitespace(this.text.charAt(i - 1))) {
            --i;
        }
        return new Substring(i, this.getWordEndIndex(i));
    }

    @VisibleForTesting
    public Substring getNextWordAtCursor() {
        int i;
        if (this.text.isEmpty()) {
            return Substring.EMPTY;
        }
        for (i = MathHelper.clamp(this.cursor, 0, this.text.length() - 1); i < this.text.length() && !Character.isWhitespace(this.text.charAt(i)); ++i) {
        }
        while (i < this.text.length() && Character.isWhitespace(this.text.charAt(i))) {
            ++i;
        }
        return new Substring(i, this.getWordEndIndex(i));
    }

    private int getWordEndIndex(int startIndex) {
        int i;
        for (i = startIndex; i < this.text.length() && !Character.isWhitespace(this.text.charAt(i)); ++i) {
        }
        return i;
    }

    private void onChange() {
        this.rewrap();
        this.changeListener.accept(this.text);
        this.cursorChangeListener.run();
    }

    private void rewrap() {
        this.lines.clear();
        if (this.text.isEmpty()) {
            this.lines.add(Substring.EMPTY);
            return;
        }
        this.textRenderer.getTextHandler().wrapLines(this.text, this.width, Style.EMPTY, false, (style, start, end) -> this.lines.add(new Substring(start, end)));
        if (this.text.charAt(this.text.length() - 1) == '\n') {
            this.lines.add(new Substring(this.text.length(), this.text.length()));
        }
    }

    private String truncateForReplacement(String value) {
        if (this.hasMaxLength()) {
            return StringHelper.truncate(value, this.maxLength, false);
        }
        return value;
    }

    private String truncate(String value) {
        String string = value;
        if (this.hasMaxLength()) {
            int i = this.maxLength - this.text.length();
            string = StringHelper.truncate(value, 1, false);
        }
        return string;
    }

    private boolean exceedsMaxLines(String text) {
        return this.hasMaxLines() && this.textRenderer.getTextHandler().wrapLines(text, this.width, Style.EMPTY).size() + (StringHelper.endsWithLineBreak(text) ? 1 : 0) > this.maxLines;
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class Substring
    extends Record {
        final int beginIndex;
        final int endIndex;
        final static Substring EMPTY = new Substring(0, 0);

        protected Substring(int i, int j) {
            this.beginIndex = i;
            this.endIndex = j;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Substring.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Substring.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Substring.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this, object);
        }

        public int beginIndex() {
            return this.beginIndex;
        }

        public int endIndex() {
            return this.endIndex;
        }
    }
}

