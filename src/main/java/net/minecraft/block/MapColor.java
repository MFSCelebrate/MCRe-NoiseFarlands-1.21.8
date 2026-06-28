/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package net.minecraft.block;

import com.google.common.base.Preconditions;
import net.minecraft.util.math.ColorHelper;

public class MapColor {
    final static private MapColor[] COLORS = new MapColor[64];
    final static public MapColor CLEAR = new MapColor(0, 0);
    final static public MapColor PALE_GREEN = new MapColor(1, 8368696);
    final static public MapColor PALE_YELLOW = new MapColor(2, 16247203);
    final static public MapColor WHITE_GRAY = new MapColor(3, 0xC7C7C7);
    final static public MapColor BRIGHT_RED = new MapColor(4, 0xFF0000);
    final static public MapColor PALE_PURPLE = new MapColor(5, 0xA0A0FF);
    final static public MapColor IRON_GRAY = new MapColor(6, 0xA7A7A7);
    final static public MapColor DARK_GREEN = new MapColor(7, 31744);
    final static public MapColor WHITE = new MapColor(8, 0xFFFFFF);
    final static public MapColor LIGHT_BLUE_GRAY = new MapColor(9, 10791096);
    final static public MapColor DIRT_BROWN = new MapColor(10, 9923917);
    final static public MapColor STONE_GRAY = new MapColor(11, 0x707070);
    final static public MapColor WATER_BLUE = new MapColor(12, 0x4040FF);
    final static public MapColor OAK_TAN = new MapColor(13, 9402184);
    final static public MapColor OFF_WHITE = new MapColor(14, 0xFFFCF5);
    final static public MapColor ORANGE = new MapColor(15, 14188339);
    final static public MapColor MAGENTA = new MapColor(16, 11685080);
    final static public MapColor LIGHT_BLUE = new MapColor(17, 6724056);
    final static public MapColor YELLOW = new MapColor(18, 0xE5E533);
    final static public MapColor LIME = new MapColor(19, 8375321);
    final static public MapColor PINK = new MapColor(20, 15892389);
    final static public MapColor GRAY = new MapColor(21, 0x4C4C4C);
    final static public MapColor LIGHT_GRAY = new MapColor(22, 0x999999);
    final static public MapColor CYAN = new MapColor(23, 5013401);
    final static public MapColor PURPLE = new MapColor(24, 8339378);
    final static public MapColor BLUE = new MapColor(25, 3361970);
    final static public MapColor BROWN = new MapColor(26, 6704179);
    final static public MapColor GREEN = new MapColor(27, 6717235);
    final static public MapColor RED = new MapColor(28, 0x993333);
    final static public MapColor BLACK = new MapColor(29, 0x191919);
    final static public MapColor GOLD = new MapColor(30, 16445005);
    final static public MapColor DIAMOND_BLUE = new MapColor(31, 6085589);
    final static public MapColor LAPIS_BLUE = new MapColor(32, 4882687);
    final static public MapColor EMERALD_GREEN = new MapColor(33, 55610);
    final static public MapColor SPRUCE_BROWN = new MapColor(34, 8476209);
    final static public MapColor DARK_RED = new MapColor(35, 0x700200);
    final static public MapColor TERRACOTTA_WHITE = new MapColor(36, 13742497);
    final static public MapColor TERRACOTTA_ORANGE = new MapColor(37, 10441252);
    final static public MapColor TERRACOTTA_MAGENTA = new MapColor(38, 9787244);
    final static public MapColor TERRACOTTA_LIGHT_BLUE = new MapColor(39, 7367818);
    final static public MapColor TERRACOTTA_YELLOW = new MapColor(40, 12223780);
    final static public MapColor TERRACOTTA_LIME = new MapColor(41, 6780213);
    final static public MapColor TERRACOTTA_PINK = new MapColor(42, 10505550);
    final static public MapColor TERRACOTTA_GRAY = new MapColor(43, 0x392923);
    final static public MapColor TERRACOTTA_LIGHT_GRAY = new MapColor(44, 8874850);
    final static public MapColor TERRACOTTA_CYAN = new MapColor(45, 0x575C5C);
    final static public MapColor TERRACOTTA_PURPLE = new MapColor(46, 8014168);
    final static public MapColor TERRACOTTA_BLUE = new MapColor(47, 4996700);
    final static public MapColor TERRACOTTA_BROWN = new MapColor(48, 4993571);
    final static public MapColor TERRACOTTA_GREEN = new MapColor(49, 5001770);
    final static public MapColor TERRACOTTA_RED = new MapColor(50, 9321518);
    final static public MapColor TERRACOTTA_BLACK = new MapColor(51, 2430480);
    final static public MapColor DULL_RED = new MapColor(52, 12398641);
    final static public MapColor DULL_PINK = new MapColor(53, 9715553);
    final static public MapColor DARK_CRIMSON = new MapColor(54, 6035741);
    final static public MapColor TEAL = new MapColor(55, 1474182);
    final static public MapColor DARK_AQUA = new MapColor(56, 3837580);
    final static public MapColor DARK_DULL_PINK = new MapColor(57, 5647422);
    final static public MapColor BRIGHT_TEAL = new MapColor(58, 1356933);
    final static public MapColor DEEPSLATE_GRAY = new MapColor(59, 0x646464);
    final static public MapColor RAW_IRON_PINK = new MapColor(60, 14200723);
    final static public MapColor LICHEN_GREEN = new MapColor(61, 8365974);
    final public int color;
    final public int id;

    private MapColor(int id, int color) {
        if (id < 0 || id > 63) {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
        this.id = id;
        this.color = color;
        MapColor.COLORS[id] = this;
    }

    public int getRenderColor(Brightness brightness) {
        if (this == CLEAR) {
            return 0;
        }
        return ColorHelper.scaleRgb(ColorHelper.fullAlpha(this.color), brightness.brightness);
    }

    public static MapColor get(int id) {
        Preconditions.checkPositionIndex((int)id, (int)COLORS.length, (String)"material id");
        return MapColor.getUnchecked(id);
    }

    private static MapColor getUnchecked(int id) {
        MapColor mapColor = COLORS[id];
        return mapColor != null ? mapColor : CLEAR;
    }

    public static int getRenderColor(int colorByte) {
        int i = colorByte & 0xFF;
        return MapColor.getUnchecked(i >> 2).getRenderColor(Brightness.get(i & 3));
    }

    public byte getRenderColorByte(Brightness brightness) {
        return (byte)(this.id << 2 | brightness.id & 3);
    }

    public static final class Brightness
    extends Enum<Brightness> {
        final static public Brightness LOW = new Brightness(0, 180);
        final static public Brightness NORMAL = new Brightness(1, 220);
        final static public Brightness HIGH = new Brightness(2, 255);
        final static public Brightness LOWEST = new Brightness(3, 135);
        final static private Brightness[] VALUES;
        final public int id;
        final public int brightness;
        final static private Brightness[] field_34766;

        public static Brightness[] values() {
            return (Brightness[])field_34766.clone();
        }

        public static Brightness valueOf(String string) {
            return Enum.valueOf(Brightness.class, string);
        }

        private Brightness(int id, int brightness) {
            this.id = id;
            this.brightness = brightness;
        }

        public static Brightness validateAndGet(int id) {
            Preconditions.checkPositionIndex((int)id, (int)VALUES.length, (String)"brightness id");
            return Brightness.get(id);
        }

        static Brightness get(int id) {
            return VALUES[id];
        }

        private static Brightness[] method_38483() {
            return new Brightness[]{LOW, NORMAL, HIGH, LOWEST};
        }

        static {
            field_34766 = Brightness.method_38483();
            VALUES = new Brightness[]{LOW, NORMAL, HIGH, LOWEST};
        }
    }
}

