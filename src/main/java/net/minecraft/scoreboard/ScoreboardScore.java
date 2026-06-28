/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

public class ScoreboardScore
implements ReadableScoreboardScore {
    final static public MapCodec<ScoreboardScore> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.optionalFieldOf("Score", (Object)0).forGetter(ScoreboardScore::getScore), (App)Codec.BOOL.optionalFieldOf("Locked", (Object)false).forGetter(ScoreboardScore::isLocked), (App)TextCodecs.CODEC.optionalFieldOf("display").forGetter(score -> Optional.ofNullable(score.displayText)), (App)NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter(score -> Optional.ofNullable(score.numberFormat))).apply((Applicative)instance, ScoreboardScore::new));
    private int score;
    private boolean locked = true;
    @Nullable
    private Text displayText;
    @Nullable
    private NumberFormat numberFormat;

    public ScoreboardScore() {
    }

    private ScoreboardScore(int score, boolean locked, Optional<Text> displayText, Optional<NumberFormat> numberFormat) {
        this.score = score;
        this.locked = locked;
        this.displayText = displayText.orElse(null);
        this.numberFormat = numberFormat.orElse(null);
    }

    @Override
    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Nullable
    public Text getDisplayText() {
        return this.displayText;
    }

    public void setDisplayText(@Nullable Text text) {
        this.displayText = text;
    }

    @Override
    @Nullable
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }
}

