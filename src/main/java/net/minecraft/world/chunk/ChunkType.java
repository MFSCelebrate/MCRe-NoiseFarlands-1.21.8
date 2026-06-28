/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk;

public final class ChunkType
extends Enum<ChunkType> {
    final static public ChunkType PROTOCHUNK = new ChunkType();
    final static public ChunkType LEVELCHUNK = new ChunkType();
    final static private ChunkType[] field_12806;

    public static ChunkType[] values() {
        return (ChunkType[])field_12806.clone();
    }

    public static ChunkType valueOf(String string) {
        return Enum.valueOf(ChunkType.class, string);
    }

    private static ChunkType[] method_36741() {
        return new ChunkType[]{PROTOCHUNK, LEVELCHUNK};
    }

    static {
        field_12806 = ChunkType.method_36741();
    }
}

