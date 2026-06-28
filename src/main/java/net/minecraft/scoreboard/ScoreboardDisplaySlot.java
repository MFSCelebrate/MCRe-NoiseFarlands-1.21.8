/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import java.util.function.IntFunction;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Nullable;

public final class ScoreboardDisplaySlot
extends Enum<ScoreboardDisplaySlot>
implements StringIdentifiable {
    final static public ScoreboardDisplaySlot LIST = new ScoreboardDisplaySlot(0, "list");
    final static public ScoreboardDisplaySlot SIDEBAR = new ScoreboardDisplaySlot(1, "sidebar");
    final static public ScoreboardDisplaySlot BELOW_NAME = new ScoreboardDisplaySlot(2, "below_name");
    final static public ScoreboardDisplaySlot TEAM_BLACK = new ScoreboardDisplaySlot(3, "sidebar.team.black");
    final static public ScoreboardDisplaySlot TEAM_DARK_BLUE = new ScoreboardDisplaySlot(4, "sidebar.team.dark_blue");
    final static public ScoreboardDisplaySlot TEAM_DARK_GREEN = new ScoreboardDisplaySlot(5, "sidebar.team.dark_green");
    final static public ScoreboardDisplaySlot TEAM_DARK_AQUA = new ScoreboardDisplaySlot(6, "sidebar.team.dark_aqua");
    final static public ScoreboardDisplaySlot TEAM_DARK_RED = new ScoreboardDisplaySlot(7, "sidebar.team.dark_red");
    final static public ScoreboardDisplaySlot TEAM_DARK_PURPLE = new ScoreboardDisplaySlot(8, "sidebar.team.dark_purple");
    final static public ScoreboardDisplaySlot TEAM_GOLD = new ScoreboardDisplaySlot(9, "sidebar.team.gold");
    final static public ScoreboardDisplaySlot TEAM_GRAY = new ScoreboardDisplaySlot(10, "sidebar.team.gray");
    final static public ScoreboardDisplaySlot TEAM_DARK_GRAY = new ScoreboardDisplaySlot(11, "sidebar.team.dark_gray");
    final static public ScoreboardDisplaySlot TEAM_BLUE = new ScoreboardDisplaySlot(12, "sidebar.team.blue");
    final static public ScoreboardDisplaySlot TEAM_GREEN = new ScoreboardDisplaySlot(13, "sidebar.team.green");
    final static public ScoreboardDisplaySlot TEAM_AQUA = new ScoreboardDisplaySlot(14, "sidebar.team.aqua");
    final static public ScoreboardDisplaySlot TEAM_RED = new ScoreboardDisplaySlot(15, "sidebar.team.red");
    final static public ScoreboardDisplaySlot TEAM_LIGHT_PURPLE = new ScoreboardDisplaySlot(16, "sidebar.team.light_purple");
    final static public ScoreboardDisplaySlot TEAM_YELLOW = new ScoreboardDisplaySlot(17, "sidebar.team.yellow");
    final static public ScoreboardDisplaySlot TEAM_WHITE = new ScoreboardDisplaySlot(18, "sidebar.team.white");
    final static public StringIdentifiable.EnumCodec<ScoreboardDisplaySlot> CODEC;
    final static public IntFunction<ScoreboardDisplaySlot> FROM_ID;
    final private int id;
    final private String name;
    final static private ScoreboardDisplaySlot[] field_45179;

    public static ScoreboardDisplaySlot[] values() {
        return (ScoreboardDisplaySlot[])field_45179.clone();
    }

    public static ScoreboardDisplaySlot valueOf(String string) {
        return Enum.valueOf(ScoreboardDisplaySlot.class, string);
    }

    private ScoreboardDisplaySlot(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.name;
    }

    @Nullable
    public static ScoreboardDisplaySlot fromFormatting(Formatting formatting) {
        return switch (formatting) {
            default -> throw new MatchException(null, null);
            case Formatting.BLACK -> TEAM_BLACK;
            case Formatting.DARK_BLUE -> TEAM_DARK_BLUE;
            case Formatting.DARK_GREEN -> TEAM_DARK_GREEN;
            case Formatting.DARK_AQUA -> TEAM_DARK_AQUA;
            case Formatting.DARK_RED -> TEAM_DARK_RED;
            case Formatting.DARK_PURPLE -> TEAM_DARK_PURPLE;
            case Formatting.GOLD -> TEAM_GOLD;
            case Formatting.GRAY -> TEAM_GRAY;
            case Formatting.DARK_GRAY -> TEAM_DARK_GRAY;
            case Formatting.BLUE -> TEAM_BLUE;
            case Formatting.GREEN -> TEAM_GREEN;
            case Formatting.AQUA -> TEAM_AQUA;
            case Formatting.RED -> TEAM_RED;
            case Formatting.LIGHT_PURPLE -> TEAM_LIGHT_PURPLE;
            case Formatting.YELLOW -> TEAM_YELLOW;
            case Formatting.WHITE -> TEAM_WHITE;
            case Formatting.BOLD, Formatting.ITALIC, Formatting.UNDERLINE, Formatting.RESET, Formatting.OBFUSCATED, Formatting.STRIKETHROUGH -> null;
        };
    }

    private static ScoreboardDisplaySlot[] method_52623() {
        return new ScoreboardDisplaySlot[]{LIST, SIDEBAR, BELOW_NAME, TEAM_BLACK, TEAM_DARK_BLUE, TEAM_DARK_GREEN, TEAM_DARK_AQUA, TEAM_DARK_RED, TEAM_DARK_PURPLE, TEAM_GOLD, TEAM_GRAY, TEAM_DARK_GRAY, TEAM_BLUE, TEAM_GREEN, TEAM_AQUA, TEAM_RED, TEAM_LIGHT_PURPLE, TEAM_YELLOW, TEAM_WHITE};
    }

    static {
        field_45179 = ScoreboardDisplaySlot.method_52623();
        CODEC = StringIdentifiable.createCodec(ScoreboardDisplaySlot::values);
        FROM_ID = ValueLists.createIndexToValueFunction(ScoreboardDisplaySlot::getId, ScoreboardDisplaySlot.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}

