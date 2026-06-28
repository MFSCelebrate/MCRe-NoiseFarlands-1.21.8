/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class BlockFace
extends Enum<BlockFace>
implements StringIdentifiable {
    final static public BlockFace FLOOR = new BlockFace("floor");
    final static public BlockFace WALL = new BlockFace("wall");
    final static public BlockFace CEILING = new BlockFace("ceiling");
    final private String name;
    final static private BlockFace[] field_12474;

    public static BlockFace[] values() {
        return (BlockFace[])field_12474.clone();
    }

    public static BlockFace valueOf(String string) {
        return Enum.valueOf(BlockFace.class, string);
    }

    private BlockFace(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static BlockFace[] method_36720() {
        return new BlockFace[]{FLOOR, WALL, CEILING};
    }

    static {
        field_12474 = BlockFace.method_36720();
    }
}

