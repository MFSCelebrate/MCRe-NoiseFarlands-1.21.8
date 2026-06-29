/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class Attachment
extends Enum<Attachment>
implements StringIdentifiable {
    final static public Attachment FLOOR = new Attachment("floor");
    final static public Attachment CEILING = new Attachment("ceiling");
    final static public Attachment SINGLE_WALL = new Attachment("single_wall");
    final static public Attachment DOUBLE_WALL = new Attachment("double_wall");
    final private String name;
    final static private Attachment[] field_17103;

    public static Attachment[] values() {
        return (Attachment[])field_17103.clone();
    }

    public static Attachment valueOf(String string) {
        return Enum.valueOf(Attachment.class, string);
    }

    private Attachment(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static Attachment[] method_36723() {
        return new Attachment[]{FLOOR, CEILING, SINGLE_WALL, DOUBLE_WALL};
    }

    static {
        field_17103 = Attachment.method_36723();
    }
}

