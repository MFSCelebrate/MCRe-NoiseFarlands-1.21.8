/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ColorLerper {
    final static public DyeColor[] RAINBOW_COLORS = new DyeColor[]{DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.CYAN, DyeColor.GREEN, DyeColor.LIME, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.PINK, DyeColor.RED, DyeColor.MAGENTA};

    public static int lerpColor(Type type, float step) {
        int i = MathHelper.floor(step);
        int j = i / type.colorDuration;
        int k = type.colors.length;
        int l = j % k;
        int m = (j + 1) % k;
        float f = ((float)(i % type.colorDuration) + MathHelper.fractionalPart(step)) / (float)type.colorDuration;
        int n = type.getArgb(type.colors[l]);
        int o = type.getArgb(type.colors[m]);
        return ColorHelper.lerp(f, n, o);
    }

    static int getArgb(DyeColor color, float multiplier) {
        if (color == DyeColor.WHITE) {
            return -1644826;
        }
        int i = color.getEntityColor();
        return ColorHelper.getArgb(255, MathHelper.floor((float)ColorHelper.getRed(i) * multiplier), MathHelper.floor((float)ColorHelper.getGreen(i) * multiplier), MathHelper.floor((float)ColorHelper.getBlue(i) * multiplier));
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        final static public Type SHEEP = new Type(25, DyeColor.values(), 0.75f);
        final static public Type MUSIC_NOTE = new Type(30, RAINBOW_COLORS, 1.25f);
        final int colorDuration;
        final private Map<DyeColor, Integer> colorToArgb;
        final DyeColor[] colors;
        final static private Type[] field_60692;

        public static Type[] values() {
            return (Type[])field_60692.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(int colorDuration, DyeColor[] colors, float multiplier) {
            this.colorDuration = colorDuration;
            this.colorToArgb = Maps.newHashMap(Arrays.stream(colors).collect(Collectors.toMap(color -> color, color -> ColorLerper.getArgb(color, multiplier))));
            this.colors = colors;
        }

        public final int getArgb(DyeColor color) {
            return this.colorToArgb.get(color);
        }

        private static Type[] method_71791() {
            return new Type[]{SHEEP, MUSIC_NOTE};
        }

        static {
            field_60692 = Type.method_71791();
        }
    }
}

