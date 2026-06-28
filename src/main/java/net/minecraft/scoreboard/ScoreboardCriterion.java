/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class ScoreboardCriterion {
    final static private Map<String, ScoreboardCriterion> SIMPLE_CRITERIA = Maps.newHashMap();
    final static private Map<String, ScoreboardCriterion> CRITERIA = Maps.newHashMap();
    final static public Codec<ScoreboardCriterion> CODEC = Codec.STRING.comapFlatMap(name -> ScoreboardCriterion.getOrCreateStatCriterion(name).map(DataResult::success).orElse(DataResult.error(() -> "No scoreboard criteria with name: " + name)), ScoreboardCriterion::getName);
    final static public ScoreboardCriterion DUMMY = ScoreboardCriterion.create("dummy");
    final static public ScoreboardCriterion TRIGGER = ScoreboardCriterion.create("trigger");
    final static public ScoreboardCriterion DEATH_COUNT = ScoreboardCriterion.create("deathCount");
    final static public ScoreboardCriterion PLAYER_KILL_COUNT = ScoreboardCriterion.create("playerKillCount");
    final static public ScoreboardCriterion TOTAL_KILL_COUNT = ScoreboardCriterion.create("totalKillCount");
    final static public ScoreboardCriterion HEALTH = ScoreboardCriterion.create("health", true, RenderType.HEARTS);
    final static public ScoreboardCriterion FOOD = ScoreboardCriterion.create("food", true, RenderType.INTEGER);
    final static public ScoreboardCriterion AIR = ScoreboardCriterion.create("air", true, RenderType.INTEGER);
    final static public ScoreboardCriterion ARMOR = ScoreboardCriterion.create("armor", true, RenderType.INTEGER);
    final static public ScoreboardCriterion XP = ScoreboardCriterion.create("xp", true, RenderType.INTEGER);
    final static public ScoreboardCriterion LEVEL = ScoreboardCriterion.create("level", true, RenderType.INTEGER);
    final static public ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{ScoreboardCriterion.create("teamkill." + Formatting.BLACK.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_BLUE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_GREEN.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_AQUA.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_RED.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_PURPLE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.GOLD.getName()), ScoreboardCriterion.create("teamkill." + Formatting.GRAY.getName()), ScoreboardCriterion.create("teamkill." + Formatting.DARK_GRAY.getName()), ScoreboardCriterion.create("teamkill." + Formatting.BLUE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.GREEN.getName()), ScoreboardCriterion.create("teamkill." + Formatting.AQUA.getName()), ScoreboardCriterion.create("teamkill." + Formatting.RED.getName()), ScoreboardCriterion.create("teamkill." + Formatting.LIGHT_PURPLE.getName()), ScoreboardCriterion.create("teamkill." + Formatting.YELLOW.getName()), ScoreboardCriterion.create("teamkill." + Formatting.WHITE.getName())};
    final static public ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{ScoreboardCriterion.create("killedByTeam." + Formatting.BLACK.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_BLUE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_GREEN.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_AQUA.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_RED.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_PURPLE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.GOLD.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.GRAY.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.DARK_GRAY.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.BLUE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.GREEN.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.AQUA.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.RED.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.LIGHT_PURPLE.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.YELLOW.getName()), ScoreboardCriterion.create("killedByTeam." + Formatting.WHITE.getName())};
    final private String name;
    final private boolean readOnly;
    final private RenderType defaultRenderType;

    public static ScoreboardCriterion create(String name, boolean readOnly, RenderType defaultRenderType) {
        ScoreboardCriterion scoreboardCriterion = new ScoreboardCriterion(name, readOnly, defaultRenderType);
        SIMPLE_CRITERIA.put(name, scoreboardCriterion);
        return scoreboardCriterion;
    }

    public static ScoreboardCriterion create(String name) {
        return ScoreboardCriterion.create(name, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String name) {
        this(name, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String name, boolean readOnly, RenderType defaultRenderType) {
        this.name = name;
        this.readOnly = readOnly;
        this.defaultRenderType = defaultRenderType;
        CRITERIA.put(name, this);
    }

    public static Set<String> getAllSimpleCriteria() {
        return ImmutableSet.copyOf(SIMPLE_CRITERIA.keySet());
    }

    public static Optional<ScoreboardCriterion> getOrCreateStatCriterion(String name) {
        ScoreboardCriterion scoreboardCriterion = CRITERIA.get(name);
        if (scoreboardCriterion != null) {
            return Optional.of(scoreboardCriterion);
        }
        int i = name.indexOf(58);
        if (i < 0) {
            return Optional.empty();
        }
        return Registries.STAT_TYPE.getOptionalValue(Identifier.splitOn(name.substring(0, i), '.')).flatMap(type -> ScoreboardCriterion.getOrCreateStatCriterion(type, Identifier.splitOn(name.substring(i + 1), '.')));
    }

    private static <T> Optional<ScoreboardCriterion> getOrCreateStatCriterion(StatType<T> statType, Identifier id) {
        return statType.getRegistry().getOptionalValue(id).map(statType::getOrCreateStat);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getDefaultRenderType() {
        return this.defaultRenderType;
    }

    public static final class RenderType
    extends Enum<RenderType>
    implements StringIdentifiable {
        final static public RenderType INTEGER = new RenderType("integer");
        final static public RenderType HEARTS = new RenderType("hearts");
        final private String name;
        final static public StringIdentifiable.EnumCodec<RenderType> CODEC;
        final static private RenderType[] field_1473;

        public static RenderType[] values() {
            return (RenderType[])field_1473.clone();
        }

        public static RenderType valueOf(String string) {
            return Enum.valueOf(RenderType.class, string);
        }

        private RenderType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static RenderType getType(String name) {
            return CODEC.byId(name, INTEGER);
        }

        private static RenderType[] method_36799() {
            return new RenderType[]{INTEGER, HEARTS};
        }

        static {
            field_1473 = RenderType.method_36799();
            CODEC = StringIdentifiable.createCodec(RenderType::values);
        }
    }
}

