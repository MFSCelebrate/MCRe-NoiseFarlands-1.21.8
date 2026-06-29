/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class Formatting
extends Enum<Formatting>
implements StringIdentifiable {
    final static public Formatting BLACK = new Formatting("BLACK", '0', 0, 0);
    final static public Formatting DARK_BLUE = new Formatting("DARK_BLUE", '1', 1, 170);
    final static public Formatting DARK_GREEN = new Formatting("DARK_GREEN", '2', 2, 43520);
    final static public Formatting DARK_AQUA = new Formatting("DARK_AQUA", '3', 3, 43690);
    final static public Formatting DARK_RED = new Formatting("DARK_RED", '4', 4, 0xAA0000);
    final static public Formatting DARK_PURPLE = new Formatting("DARK_PURPLE", '5', 5, 0xAA00AA);
    final static public Formatting GOLD = new Formatting("GOLD", '6', 6, 0xFFAA00);
    final static public Formatting GRAY = new Formatting("GRAY", '7', 7, 0xAAAAAA);
    final static public Formatting DARK_GRAY = new Formatting("DARK_GRAY", '8', 8, 0x555555);
    final static public Formatting BLUE = new Formatting("BLUE", '9', 9, 0x5555FF);
    final static public Formatting GREEN = new Formatting("GREEN", 'a', 10, 0x55FF55);
    final static public Formatting AQUA = new Formatting("AQUA", 'b', 11, 0x55FFFF);
    final static public Formatting RED = new Formatting("RED", 'c', 12, 0xFF5555);
    final static public Formatting LIGHT_PURPLE = new Formatting("LIGHT_PURPLE", 'd', 13, 0xFF55FF);
    final static public Formatting YELLOW = new Formatting("YELLOW", 'e', 14, 0xFFFF55);
    final static public Formatting WHITE = new Formatting("WHITE", 'f', 15, 0xFFFFFF);
    final static public Formatting OBFUSCATED = new Formatting("OBFUSCATED", 'k', true);
    final static public Formatting BOLD = new Formatting("BOLD", 'l', true);
    final static public Formatting STRIKETHROUGH = new Formatting("STRIKETHROUGH", 'm', true);
    final static public Formatting UNDERLINE = new Formatting("UNDERLINE", 'n', true);
    final static public Formatting ITALIC = new Formatting("ITALIC", 'o', true);
    final static public Formatting RESET = new Formatting("RESET", 'r', -1, null);
    final static public Codec<Formatting> CODEC;
    final static public Codec<Formatting> COLOR_CODEC;
    final static public char FORMATTING_CODE_PREFIX = '\u00a7';
    final static private Map<String, Formatting> BY_NAME;
    final static private Pattern FORMATTING_CODE_PATTERN;
    final private String name;
    final private char code;
    final private boolean modifier;
    final private String stringValue;
    final private int colorIndex;
    @Nullable
    final private Integer colorValue;
    final static private Formatting[] field_1072;

    public static Formatting[] values() {
        return (Formatting[])field_1072.clone();
    }

    public static Formatting valueOf(String string) {
        return Enum.valueOf(Formatting.class, string);
    }

    private static String sanitize(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private Formatting(String name, @Nullable char code, int colorIndex, Integer colorValue) {
        this(name, code, false, colorIndex, colorValue);
    }

    private Formatting(String name, char code, boolean modifier) {
        this(name, code, modifier, -1, null);
    }

    private Formatting(String name, char code, @Nullable boolean modifier, int colorIndex, Integer colorValue) {
        this.name = name;
        this.code = code;
        this.modifier = modifier;
        this.colorIndex = colorIndex;
        this.colorValue = colorValue;
        this.stringValue = "\u00a7" + String.valueOf(code);
    }

    public char getCode() {
        return this.code;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public boolean isModifier() {
        return this.modifier;
    }

    public boolean isColor() {
        return !this.modifier && this != RESET;
    }

    @Nullable
    public Integer getColorValue() {
        return this.colorValue;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.stringValue;
    }

    @Nullable
    @Contract(value="!null->!null;_->_")
    public static String strip(@Nullable String string) {
        return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
    }

    @Nullable
    public static Formatting byName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return BY_NAME.get(Formatting.sanitize(name));
    }

    @Nullable
    public static Formatting byColorIndex(int colorIndex) {
        if (colorIndex < 0) {
            return RESET;
        }
        for (Formatting formatting : Formatting.values()) {
            if (formatting.getColorIndex() != colorIndex) continue;
            return formatting;
        }
        return null;
    }

    @Nullable
    public static Formatting byCode(char code) {
        char c = Character.toLowerCase(code);
        for (Formatting formatting : Formatting.values()) {
            if (formatting.code != c) continue;
            return formatting;
        }
        return null;
    }

    public static Collection<String> getNames(boolean colors, boolean modifiers) {
        ArrayList list = Lists.newArrayList();
        for (Formatting formatting : Formatting.values()) {
            if (formatting.isColor() && !colors || formatting.isModifier() && !modifiers) continue;
            list.add(formatting.getName());
        }
        return list;
    }

    @Override
    public String asString() {
        return this.getName();
    }

    private static Formatting[] method_36946() {
        return new Formatting[]{BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET};
    }

    static {
        field_1072 = Formatting.method_36946();
        CODEC = StringIdentifiable.createCodec(Formatting::values);
        COLOR_CODEC = CODEC.validate(formatting -> formatting.isModifier() ? DataResult.error(() -> "Formatting was not a valid color: " + String.valueOf(formatting)) : DataResult.success((Object)formatting));
        BY_NAME = Arrays.stream(Formatting.values()).collect(Collectors.toMap(f -> Formatting.sanitize(f.name), f -> f));
        FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

