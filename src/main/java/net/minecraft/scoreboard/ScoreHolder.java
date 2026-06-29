/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface ScoreHolder {
    final static public String WILDCARD_NAME = "*";
    final static public ScoreHolder WILDCARD = new ScoreHolder(){

        @Override
        public String getNameForScoreboard() {
            return ScoreHolder.WILDCARD_NAME;
        }
    };

    public String getNameForScoreboard();

    @Nullable
    default public Text getDisplayName() {
        return null;
    }

    default public Text getStyledDisplayName() {
        Text text = this.getDisplayName();
        if (text != null) {
            return text.copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(this.getNameForScoreboard()))));
        }
        return Text.literal(this.getNameForScoreboard());
    }

    public static ScoreHolder fromName(final String name) {
        if (name.equals(WILDCARD_NAME)) {
            return WILDCARD;
        }
        final MutableText text = Text.literal(name);
        return new ScoreHolder(){

            @Override
            public String getNameForScoreboard() {
                return name;
            }

            @Override
            public Text getStyledDisplayName() {
                return text;
            }
        };
    }

    public static ScoreHolder fromProfile(GameProfile gameProfile) {
        final String string = gameProfile.getName();
        return new ScoreHolder(){

            @Override
            public String getNameForScoreboard() {
                return string;
            }
        };
    }
}

