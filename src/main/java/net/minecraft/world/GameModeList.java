/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world;

import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.GameMode;

public record GameModeList(List<GameMode> gameModes) {
    final static public GameModeList ALL = GameModeList.of(GameMode.values());
    final static public GameModeList SURVIVAL_LIKE = GameModeList.of(GameMode.SURVIVAL, GameMode.ADVENTURE);
    final static public Codec<GameModeList> CODEC = GameMode.CODEC.listOf().xmap(GameModeList::new, GameModeList::gameModes);

    public static GameModeList of(GameMode ... gameModes) {
        return new GameModeList(Arrays.stream(gameModes).toList());
    }

    public boolean contains(GameMode gameMode) {
        return this.gameModes.contains(gameMode);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameModeList.class, "types", "gameModes"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameModeList.class, "types", "gameModes"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameModeList.class, "types", "gameModes"}, this, object);
    }
}

