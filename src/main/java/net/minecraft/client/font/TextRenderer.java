/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EmptyBakedGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class TextRenderer {
    final static private float Z_INDEX = 0.01f;
    final static private float field_60693 = 0.01f;
    final static private float field_60694 = -0.01f;
    final static public float FORWARD_SHIFT = 0.03f;
    final static public int field_55090 = 0;
    final public int fontHeight = 9;
    final public Random random = Random.create();
    final private Function<Identifier, FontStorage> fontStorageAccessor;
    final boolean validateAdvance;
    final private TextHandler handler;

    public TextRenderer(Function<Identifier, FontStorage> fontStorageAccessor, boolean validateAdvance) {
        this.fontStorageAccessor = fontStorageAccessor;
        this.validateAdvance = validateAdvance;
        this.handler = new TextHandler((codePoint, style) -> this.getFontStorage(style.getFont()).getGlyph(codePoint, this.validateAdvance).getAdvance(style.isBold()));
    }

    FontStorage getFontStorage(Identifier id) {
        return this.fontStorageAccessor.apply(id);
    }

    public String mirror(String text) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicShapingException) {
            return text;
        }
    }

    public void draw(String string, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(string, x, y, color, shadow, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
    }

    public void draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(text.asOrderedText(), x, y, color, shadow, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
    }

    public void draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(text, x, y, color, shadow, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
    }

    public void drawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        Drawer drawer = new Drawer(0.0f, 0.0f, outlineColor, false);
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) continue;
                float[] fs = new float[]{x};
                int k = i;
                int l = j;
                text.accept((index, style, codePoint) -> {
                    boolean bl = style.isBold();
                    FontStorage fontStorage = this.getFontStorage(style.getFont());
                    Glyph glyph = fontStorage.getGlyph(codePoint, this.validateAdvance);
                    drawer.x = fs[0] + (float)k * glyph.getShadowOffset();
                    drawer.y = y + (float)l * glyph.getShadowOffset();
                    fs[0] = fs[0] + glyph.getAdvance(bl);
                    return drawer.accept(index, style.withColor(outlineColor), codePoint);
                });
            }
        }
        GlyphDrawer glyphDrawer = GlyphDrawer.drawing(vertexConsumers, matrix, TextLayerType.NORMAL, light);
        for (BakedGlyph.DrawnGlyph drawnGlyph : drawer.drawnGlyphs) {
            glyphDrawer.drawGlyph(drawnGlyph);
        }
        Drawer drawer2 = new Drawer(x, y, color, false);
        text.accept(drawer2);
        drawer2.draw(GlyphDrawer.drawing(vertexConsumers, matrix, TextLayerType.POLYGON_OFFSET, light));
    }

    public GlyphDrawable prepare(String string, float x, float y, int color, boolean shadow, int backgroundColor) {
        if (this.isRightToLeft()) {
            string = this.mirror(string);
        }
        Drawer drawer = new Drawer(x, y, color, backgroundColor, shadow);
        TextVisitFactory.visitFormatted(string, Style.EMPTY, (CharacterVisitor)drawer);
        return drawer;
    }

    public GlyphDrawable prepare(OrderedText text, float x, float y, int color, boolean shadow, int backgroundColor) {
        Drawer drawer = new Drawer(x, y, color, backgroundColor, shadow);
        text.accept(drawer);
        return drawer;
    }

    public int getWidth(String text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(StringVisitable text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(OrderedText text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? this.handler.trimToWidthBackwards(text, maxWidth, Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public String trimToWidth(String text, int maxWidth) {
        return this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public StringVisitable trimToWidth(StringVisitable text, int width) {
        return this.handler.trimToWidth(text, width, Style.EMPTY);
    }

    public int getWrappedLinesHeight(String text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public List<OrderedText> wrapLines(StringVisitable text, int width) {
        return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
    }

    public List<StringVisitable> wrapLinesWithoutLanguage(StringVisitable text, int width) {
        return this.handler.wrapLines(text, width, Style.EMPTY);
    }

    public boolean isRightToLeft() {
        return Language.getInstance().isRightToLeft();
    }

    public TextHandler getTextHandler() {
        return this.handler;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface GlyphDrawable {
        public void draw(GlyphDrawer var1);

        @Nullable
        public ScreenRect getScreenRect();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface GlyphDrawer {
        public static GlyphDrawer drawing(final VertexConsumerProvider vertexConsumers, final Matrix4f matrix, final TextLayerType layerType, final int light) {
            return new GlyphDrawer(){

                @Override
                public void drawGlyph(BakedGlyph.DrawnGlyph glyph) {
                    BakedGlyph bakedGlyph = glyph.glyph();
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(bakedGlyph.getLayer(layerType));
                    bakedGlyph.draw(glyph, matrix, vertexConsumer, light, false);
                }

                @Override
                public void drawRectangle(BakedGlyph bakedGlyph, BakedGlyph.Rectangle rect) {
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(bakedGlyph.getLayer(layerType));
                    bakedGlyph.drawRectangle(rect, matrix, vertexConsumer, light, false);
                }
            };
        }

        public void drawGlyph(BakedGlyph.DrawnGlyph var1);

        public void drawRectangle(BakedGlyph var1, BakedGlyph.Rectangle var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class TextLayerType
    extends Enum<TextLayerType> {
        final static public TextLayerType NORMAL = new TextLayerType();
        final static public TextLayerType SEE_THROUGH = new TextLayerType();
        final static public TextLayerType POLYGON_OFFSET = new TextLayerType();
        final static private TextLayerType[] field_33996;

        public static TextLayerType[] values() {
            return (TextLayerType[])field_33996.clone();
        }

        public static TextLayerType valueOf(String string) {
            return Enum.valueOf(TextLayerType.class, string);
        }

        private static TextLayerType[] method_37344() {
            return new TextLayerType[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
        }

        static {
            field_33996 = TextLayerType.method_37344();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class Drawer
    implements CharacterVisitor,
    GlyphDrawable {
        final private boolean shadow;
        final private int color;
        final private int backgroundColor;
        float x;
        float y;
        private float minX = Float.MAX_VALUE;
        private float minY = Float.MAX_VALUE;
        private float maxX = -3.4028235E38f;
        private float maxY = -3.4028235E38f;
        private float minBackgroundX = Float.MAX_VALUE;
        private float minBackgroundY = Float.MAX_VALUE;
        private float maxBackgroundX = -3.4028235E38f;
        private float maxBackgroundY = -3.4028235E38f;
        final List<BakedGlyph.DrawnGlyph> drawnGlyphs = new ArrayList<BakedGlyph.DrawnGlyph>();
        @Nullable
        private List<BakedGlyph.Rectangle> rectangles;

        public Drawer(float x, float y, int color, boolean shadow) {
            this(x, y, color, 0, shadow);
        }

        public Drawer(float x, float y, int color, int backgroundColor, boolean shadow) {
            this.x = x;
            this.y = y;
            this.shadow = shadow;
            this.color = color;
            this.backgroundColor = backgroundColor;
            this.updateBackgroundBounds(x, y, 0.0f);
        }

        private void updateTextBounds(float minX, float minY, float maxX, float maxY) {
            this.minX = Math.min(this.minX, minX);
            this.minY = Math.min(this.minY, minY);
            this.maxX = Math.max(this.maxX, maxX);
            this.maxY = Math.max(this.maxY, maxY);
        }

        private void updateBackgroundBounds(float x, float y, float width) {
            if (ColorHelper.getAlpha(this.backgroundColor) == 0) {
                return;
            }
            this.minBackgroundX = Math.min(this.minBackgroundX, x - 1.0f);
            this.minBackgroundY = Math.min(this.minBackgroundY, y - 1.0f);
            this.maxBackgroundX = Math.max(this.maxBackgroundX, x + width);
            this.maxBackgroundY = Math.max(this.maxBackgroundY, y + 9.0f);
            this.updateTextBounds(this.minBackgroundX, this.minBackgroundY, this.maxBackgroundX, this.maxBackgroundY);
        }

        private void addGlyph(BakedGlyph.DrawnGlyph glyph) {
            this.drawnGlyphs.add(glyph);
            this.updateTextBounds(glyph.getEffectiveMinX(), glyph.getEffectiveMinY(), glyph.getEffectiveMaxX(), glyph.getEffectiveMaxY());
        }

        private void addRectangle(BakedGlyph.Rectangle rectangle) {
            if (this.rectangles == null) {
                this.rectangles = new ArrayList<BakedGlyph.Rectangle>();
            }
            this.rectangles.add(rectangle);
            this.updateTextBounds(rectangle.getEffectiveMinX(), rectangle.getEffectiveMinY(), rectangle.getEffectiveMaxX(), rectangle.getEffectiveMaxY());
        }

        @Override
        public boolean accept(int i, Style style, int j) {
            FontStorage fontStorage = TextRenderer.this.getFontStorage(style.getFont());
            Glyph glyph = fontStorage.getGlyph(j, TextRenderer.this.validateAdvance);
            BakedGlyph bakedGlyph = style.isObfuscated() && j != 32 ? fontStorage.getObfuscatedBakedGlyph(glyph) : fontStorage.getBaked(j);
            boolean bl = style.isBold();
            TextColor textColor = style.getColor();
            int k = this.getRenderColor(textColor);
            int l = this.getShadowColor(style, k);
            float f = glyph.getAdvance(bl);
            float g = i == 0 ? this.x - 1.0f : this.x;
            float h = glyph.getShadowOffset();
            if (!(bakedGlyph instanceof EmptyBakedGlyph)) {
                float m = bl ? glyph.getBoldOffset() : 0.0f;
                this.addGlyph(new BakedGlyph.DrawnGlyph(this.x, this.y, k, l, bakedGlyph, style, m, h));
            }
            this.updateBackgroundBounds(this.x, this.y, f);
            if (style.isStrikethrough()) {
                this.addRectangle(new BakedGlyph.Rectangle(g, this.y + 4.5f - 1.0f, this.x + f, this.y + 4.5f, 0.01f, k, l, h));
            }
            if (style.isUnderlined()) {
                this.addRectangle(new BakedGlyph.Rectangle(g, this.y + 9.0f - 1.0f, this.x + f, this.y + 9.0f, 0.01f, k, l, h));
            }
            this.x += f;
            return true;
        }

        @Override
        public void draw(GlyphDrawer glyphDrawer) {
            BakedGlyph bakedGlyph = null;
            if (ColorHelper.getAlpha(this.backgroundColor) != 0) {
                BakedGlyph.Rectangle rectangle = new BakedGlyph.Rectangle(this.minBackgroundX, this.minBackgroundY, this.maxBackgroundX, this.maxBackgroundY, -0.01f, this.backgroundColor);
                bakedGlyph = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
                glyphDrawer.drawRectangle(bakedGlyph, rectangle);
            }
            for (BakedGlyph.DrawnGlyph drawnGlyph : this.drawnGlyphs) {
                glyphDrawer.drawGlyph(drawnGlyph);
            }
            if (this.rectangles != null) {
                if (bakedGlyph == null) {
                    bakedGlyph = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
                }
                for (BakedGlyph.Rectangle rectangle2 : this.rectangles) {
                    glyphDrawer.drawRectangle(bakedGlyph, rectangle2);
                }
            }
        }

        private int getRenderColor(@Nullable TextColor override) {
            if (override != null) {
                int i = ColorHelper.getAlpha(this.color);
                int j = override.getRgb();
                return ColorHelper.withAlpha(i, j);
            }
            return this.color;
        }

        private int getShadowColor(Style style, int textColor) {
            Integer integer = style.getShadowColor();
            if (integer != null) {
                float f = ColorHelper.getAlphaFloat(textColor);
                float g = ColorHelper.getAlphaFloat(integer);
                if (f != 1.0f) {
                    return ColorHelper.withAlpha(ColorHelper.channelFromFloat(f * g), (int)integer);
                }
                return integer;
            }
            if (this.shadow) {
                return ColorHelper.scaleRgb(textColor, 0.25f);
            }
            return 0;
        }

        @Override
        @Nullable
        public ScreenRect getScreenRect() {
            if (this.minX >= this.maxX || this.minY >= this.maxY) {
                return null;
            }
            int i = MathHelper.floor(this.minX);
            int j = MathHelper.floor(this.minY);
            int k = MathHelper.ceil(this.maxX);
            int l = MathHelper.ceil(this.maxY);
            return new ScreenRect(i, j, k - i, l - j);
        }
    }
}

