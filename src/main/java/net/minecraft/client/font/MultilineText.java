/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.font;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface MultilineText {
    final static public MultilineText EMPTY = new MultilineText(){

        @Override
        public void drawCenterWithShadow(DrawContext context, int x, int y) {
        }

        @Override
        public void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
        }

        @Override
        public void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
        }

        @Override
        public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
            return y;
        }

        @Override
        @Nullable
        public Style getStyleAtCentered(int x, int y, int i, double mouseX, double mouseY) {
            return null;
        }

        @Override
        @Nullable
        public Style getStyleAtLeftAligned(int x, int y, int i, double mouseX, double mouseY) {
            return null;
        }

        @Override
        public int count() {
            return 0;
        }

        @Override
        public int getMaxWidth() {
            return 0;
        }
    };

    public static MultilineText create(TextRenderer renderer, Text ... texts) {
        return MultilineText.create(renderer, Integer.MAX_VALUE, Integer.MAX_VALUE, texts);
    }

    public static MultilineText create(TextRenderer renderer, int maxWidth, Text ... texts) {
        return MultilineText.create(renderer, maxWidth, Integer.MAX_VALUE, texts);
    }

    public static MultilineText create(TextRenderer renderer, Text text, int maxWidth) {
        return MultilineText.create(renderer, maxWidth, Integer.MAX_VALUE, text);
    }

    public static MultilineText create(final TextRenderer renderer, final int maxWidth, final int maxLines, final Text ... texts) {
        if (texts.length == 0) {
            return EMPTY;
        }
        return new MultilineText(){
            @Nullable
            private List<Line> lines;
            @Nullable
            private Language language;

            @Override
            public void drawCenterWithShadow(DrawContext context, int x, int y) {
                this.drawCenterWithShadow(context, x, y, renderer.fontHeight, -1);
            }

            @Override
            public void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
                int i = y;
                for (Line line : this.getLines()) {
                    context.drawTextWithShadow(renderer, line.text, x - line.width / 2, i, color);
                    i += lineHeight;
                }
            }

            @Override
            public void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
                int i = y;
                for (Line line : this.getLines()) {
                    context.drawTextWithShadow(renderer, line.text, x, i, color);
                    i += lineHeight;
                }
            }

            @Override
            public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
                int i = y;
                for (Line line : this.getLines()) {
                    context.drawText(renderer, line.text, x, i, color, false);
                    i += lineHeight;
                }
                return i;
            }

            @Override
            @Nullable
            public Style getStyleAtCentered(int x, int y, int i, double mouseX, double mouseY) {
                List<Line> list = this.getLines();
                int j = MathHelper.floor((mouseY - (double)y) / (double)i);
                if (j < 0 || j >= list.size()) {
                    return null;
                }
                Line line = list.get(j);
                int k = x - line.width / 2;
                if (mouseX < (double)k) {
                    return null;
                }
                int l = MathHelper.floor(mouseX - (double)k);
                return renderer.getTextHandler().getStyleAt(line.text, l);
            }

            @Override
            @Nullable
            public Style getStyleAtLeftAligned(int x, int y, int i, double mouseX, double mouseY) {
                if (mouseX < (double)x) {
                    return null;
                }
                List<Line> list = this.getLines();
                int j = MathHelper.floor((mouseY - (double)y) / (double)i);
                if (j < 0 || j >= list.size()) {
                    return null;
                }
                Line line = list.get(j);
                int k = MathHelper.floor(mouseX - (double)x);
                return renderer.getTextHandler().getStyleAt(line.text, k);
            }

            private List<Line> getLines() {
                Language language = Language.getInstance();
                if (this.lines != null && language == this.language) {
                    return this.lines;
                }
                this.language = language;
                ArrayList<StringVisitable> list = new ArrayList<StringVisitable>();
                for (Text text : texts) {
                    list.addAll(renderer.wrapLinesWithoutLanguage(text, maxWidth));
                }
                this.lines = new ArrayList<Line>();
                int i = Math.min(list.size(), maxLines);
                List list2 = list.subList(0, i);
                for (int j = 0; j < list2.size(); ++j) {
                    StringVisitable stringVisitable = (StringVisitable)list2.get(j);
                    OrderedText orderedText = Language.getInstance().reorder(stringVisitable);
                    if (j == list2.size() - 1 && i == maxLines && i != list.size()) {
                        StringVisitable stringVisitable2 = renderer.trimToWidth(stringVisitable, renderer.getWidth(stringVisitable) - renderer.getWidth(ScreenTexts.ELLIPSIS));
                        StringVisitable stringVisitable3 = StringVisitable.concat(stringVisitable2, ScreenTexts.ELLIPSIS);
                        this.lines.add(new Line(Language.getInstance().reorder(stringVisitable3), renderer.getWidth(stringVisitable3)));
                        continue;
                    }
                    this.lines.add(new Line(orderedText, renderer.getWidth(orderedText)));
                }
                return this.lines;
            }

            @Override
            public int count() {
                return this.getLines().size();
            }

            @Override
            public int getMaxWidth() {
                return Math.min(maxWidth, this.getLines().stream().mapToInt(Line::width).max().orElse(0));
            }
        };
    }

    public void drawCenterWithShadow(DrawContext var1, int var2, int var3);

    public void drawCenterWithShadow(DrawContext var1, int var2, int var3, int var4, int var5);

    public void drawWithShadow(DrawContext var1, int var2, int var3, int var4, int var5);

    public int draw(DrawContext var1, int var2, int var3, int var4, int var5);

    @Nullable
    public Style getStyleAtCentered(int var1, int var2, int var3, double var4, double var6);

    @Nullable
    public Style getStyleAtLeftAligned(int var1, int var2, int var3, double var4, double var6);

    public int count();

    public int getMaxWidth();

    @Environment(value=EnvType.CLIENT)
    public static final class Line
    extends Record {
        final OrderedText text;
        final int width;

        public Line(OrderedText text, int width) {
            this.text = text;
            this.width = width;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Line.class, "text;width", "text", "width"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Line.class, "text;width", "text", "width"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Line.class, "text;width", "text", "width"}, this, object);
        }

        public OrderedText text() {
            return this.text;
        }

        public int width() {
            return this.width;
        }
    }
}

