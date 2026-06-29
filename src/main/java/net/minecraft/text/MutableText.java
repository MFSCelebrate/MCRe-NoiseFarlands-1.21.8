/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class MutableText
implements Text {
    final private TextContent content;
    final private List<Text> siblings;
    private Style style;
    private OrderedText ordered = OrderedText.EMPTY;
    @Nullable
    private Language language;

    MutableText(TextContent content, List<Text> siblings, Style style) {
        this.content = content;
        this.siblings = siblings;
        this.style = style;
    }

    public static MutableText of(TextContent content) {
        return new MutableText(content, Lists.newArrayList(), Style.EMPTY);
    }

    @Override
    public TextContent getContent() {
        return this.content;
    }

    @Override
    public List<Text> getSiblings() {
        return this.siblings;
    }

    public MutableText setStyle(Style style) {
        this.style = style;
        return this;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    public MutableText append(String text) {
        if (text.isEmpty()) {
            return this;
        }
        return this.append(Text.literal(text));
    }

    public MutableText append(Text text) {
        this.siblings.add(text);
        return this;
    }

    public MutableText styled(UnaryOperator<Style> styleUpdater) {
        this.setStyle((Style)styleUpdater.apply(this.getStyle()));
        return this;
    }

    public MutableText fillStyle(Style styleOverride) {
        this.setStyle(styleOverride.withParent(this.getStyle()));
        return this;
    }

    public MutableText formatted(Formatting ... formattings) {
        this.setStyle(this.getStyle().withFormatting(formattings));
        return this;
    }

    public MutableText formatted(Formatting formatting) {
        this.setStyle(this.getStyle().withFormatting(formatting));
        return this;
    }

    public MutableText withColor(int color) {
        this.setStyle(this.getStyle().withColor(color));
        return this;
    }

    @Override
    public OrderedText asOrderedText() {
        Language language = Language.getInstance();
        if (this.language != language) {
            this.ordered = language.reorder(this);
            this.language = language;
        }
        return this.ordered;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof MutableText) {
            MutableText mutableText = (MutableText)o;
            return this.content.equals(mutableText.content) && this.style.equals(mutableText.style) && this.siblings.equals(mutableText.siblings);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.content, this.style, this.siblings);
    }

    public String toString() {
        boolean bl2;
        StringBuilder stringBuilder = new StringBuilder(this.content.toString());
        boolean bl = !this.style.isEmpty();
        boolean bl3 = bl2 = !this.siblings.isEmpty();
        if (bl || bl2) {
            stringBuilder.append('[');
            if (bl) {
                stringBuilder.append("style=");
                stringBuilder.append(this.style);
            }
            if (bl && bl2) {
                stringBuilder.append(", ");
            }
            if (bl2) {
                stringBuilder.append("siblings=");
                stringBuilder.append(this.siblings);
            }
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }
}

