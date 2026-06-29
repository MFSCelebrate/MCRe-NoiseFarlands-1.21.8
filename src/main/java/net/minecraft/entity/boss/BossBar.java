/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.boss;

import com.mojang.serialization.Codec;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public abstract class BossBar {
    final private UUID uuid;
    protected Text name;
    protected float percent;
    protected Color color;
    protected Style style;
    protected boolean darkenSky;
    protected boolean dragonMusic;
    protected boolean thickenFog;

    public BossBar(UUID uuid, Text name, Color color, Style style) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.style = style;
        this.percent = 1.0f;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Text getName() {
        return this.name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public float getPercent() {
        return this.percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }

    public BossBar setDarkenSky(boolean darkenSky) {
        this.darkenSky = darkenSky;
        return this;
    }

    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    public BossBar setDragonMusic(boolean dragonMusic) {
        this.dragonMusic = dragonMusic;
        return this;
    }

    public BossBar setThickenFog(boolean thickenFog) {
        this.thickenFog = thickenFog;
        return this;
    }

    public boolean shouldThickenFog() {
        return this.thickenFog;
    }

    public static final class Color
    extends Enum<Color>
    implements StringIdentifiable {
        final static public Color PINK = new Color("pink", Formatting.RED);
        final static public Color BLUE = new Color("blue", Formatting.BLUE);
        final static public Color RED = new Color("red", Formatting.DARK_RED);
        final static public Color GREEN = new Color("green", Formatting.GREEN);
        final static public Color YELLOW = new Color("yellow", Formatting.YELLOW);
        final static public Color PURPLE = new Color("purple", Formatting.DARK_BLUE);
        final static public Color WHITE = new Color("white", Formatting.WHITE);
        final static public Codec<Color> CODEC;
        final private String name;
        final private Formatting format;
        final static private Color[] field_5789;

        public static Color[] values() {
            return (Color[])field_5789.clone();
        }

        public static Color valueOf(String string) {
            return Enum.valueOf(Color.class, string);
        }

        private Color(String name, Formatting format) {
            this.name = name;
            this.format = format;
        }

        public Formatting getTextFormat() {
            return this.format;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Color[] method_36595() {
            return new Color[]{PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE};
        }

        static {
            field_5789 = Color.method_36595();
            CODEC = StringIdentifiable.createCodec(Color::values);
        }
    }

    public static final class Style
    extends Enum<Style>
    implements StringIdentifiable {
        final static public Style PROGRESS = new Style("progress");
        final static public Style NOTCHED_6 = new Style("notched_6");
        final static public Style NOTCHED_10 = new Style("notched_10");
        final static public Style NOTCHED_12 = new Style("notched_12");
        final static public Style NOTCHED_20 = new Style("notched_20");
        final static public Codec<Style> CODEC;
        final private String name;
        final static private Style[] field_5792;

        public static Style[] values() {
            return (Style[])field_5792.clone();
        }

        public static Style valueOf(String string) {
            return Enum.valueOf(Style.class, string);
        }

        private Style(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Style[] method_36596() {
            return new Style[]{PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20};
        }

        static {
            field_5792 = Style.method_36596();
            CODEC = StringIdentifiable.createCodec(Style::values);
        }
    }
}

