/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public final class BlockRenderType
extends Enum<BlockRenderType> {
    final static public BlockRenderType INVISIBLE = new BlockRenderType();
    final static public BlockRenderType MODEL = new BlockRenderType();
    final static private BlockRenderType[] field_11457;

    public static BlockRenderType[] values() {
        return (BlockRenderType[])field_11457.clone();
    }

    public static BlockRenderType valueOf(String string) {
        return Enum.valueOf(BlockRenderType.class, string);
    }

    private static BlockRenderType[] method_36708() {
        return new BlockRenderType[]{INVISIBLE, MODEL};
    }

    static {
        field_11457 = BlockRenderType.method_36708();
    }
}

