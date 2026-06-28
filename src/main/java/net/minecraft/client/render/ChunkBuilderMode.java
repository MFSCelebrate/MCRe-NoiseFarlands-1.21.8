/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class ChunkBuilderMode
extends Enum<ChunkBuilderMode>
implements TranslatableOption {
    final static public ChunkBuilderMode NONE = new ChunkBuilderMode(0, "options.prioritizeChunkUpdates.none");
    final static public ChunkBuilderMode PLAYER_AFFECTED = new ChunkBuilderMode(1, "options.prioritizeChunkUpdates.byPlayer");
    final static public ChunkBuilderMode NEARBY = new ChunkBuilderMode(2, "options.prioritizeChunkUpdates.nearby");
    final static private IntFunction<ChunkBuilderMode> BY_ID;
    final private int id;
    final private String name;
    final static private ChunkBuilderMode[] field_34794;

    public static ChunkBuilderMode[] values() {
        return (ChunkBuilderMode[])field_34794.clone();
    }

    public static ChunkBuilderMode valueOf(String string) {
        return Enum.valueOf(ChunkBuilderMode.class, string);
    }

    private ChunkBuilderMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.name;
    }

    public static ChunkBuilderMode get(int id) {
        return BY_ID.apply(id);
    }

    private static ChunkBuilderMode[] method_38526() {
        return new ChunkBuilderMode[]{NONE, PLAYER_AFFECTED, NEARBY};
    }

    static {
        field_34794 = ChunkBuilderMode.method_38526();
        BY_ID = ValueLists.createIndexToValueFunction(ChunkBuilderMode::getId, ChunkBuilderMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

