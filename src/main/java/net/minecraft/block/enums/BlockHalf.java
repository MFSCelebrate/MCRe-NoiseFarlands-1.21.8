/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class BlockHalf
extends Enum<BlockHalf>
implements StringIdentifiable {
    final static public BlockHalf TOP = new BlockHalf("top");
    final static public BlockHalf BOTTOM = new BlockHalf("bottom");
    final private String name;
    final static private BlockHalf[] field_12618;

    public static BlockHalf[] values() {
        return (BlockHalf[])field_12618.clone();
    }

    public static BlockHalf valueOf(String string) {
        return Enum.valueOf(BlockHalf.class, string);
    }

    private BlockHalf(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static BlockHalf[] method_36729() {
        return new BlockHalf[]{TOP, BOTTOM};
    }

    static {
        field_12618 = BlockHalf.method_36729();
    }
}

