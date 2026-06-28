/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.scoreboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.PersistentState;

public class ScoreboardState
extends PersistentState {
    final static public String SCOREBOARD_KEY = "scoreboard";
    final private Scoreboard scoreboard;

    public ScoreboardState(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void unpack(Packed packed) {
        packed.objectives().forEach(this.scoreboard::addObjective);
        packed.scores().forEach(this.scoreboard::addEntry);
        packed.displaySlots().forEach((slot, objectiveName) -> {
            ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective((String)objectiveName);
            this.scoreboard.setObjectiveSlot((ScoreboardDisplaySlot)slot, scoreboardObjective);
        });
        packed.teams().forEach(this.scoreboard::addTeam);
    }

    public Packed pack() {
        EnumMap<ScoreboardDisplaySlot, String> map = new EnumMap<ScoreboardDisplaySlot, String>(ScoreboardDisplaySlot.class);
        for (ScoreboardDisplaySlot scoreboardDisplaySlot : ScoreboardDisplaySlot.values()) {
            ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
            if (scoreboardObjective == null) continue;
            map.put(scoreboardDisplaySlot, scoreboardObjective.getName());
        }
        return new Packed(this.scoreboard.getObjectives().stream().map(ScoreboardObjective::pack).toList(), this.scoreboard.pack(), map, this.scoreboard.getTeams().stream().map(Team::pack).toList());
    }

    public record Packed(List<ScoreboardObjective.Packed> objectives, List<Scoreboard.PackedEntry> scores, Map<ScoreboardDisplaySlot, String> displaySlots, List<Team.Packed> teams) {
        final static public Codec<Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ScoreboardObjective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(Packed::objectives), (App)Scoreboard.PackedEntry.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(Packed::scores), (App)Codec.unboundedMap(ScoreboardDisplaySlot.CODEC, (Codec)Codec.STRING).optionalFieldOf("DisplaySlots", Map.of()).forGetter(Packed::displaySlots), (App)Team.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(Packed::teams)).apply((Applicative)instance, Packed::new));
    }
}

