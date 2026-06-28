/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.piston;

public final class PistonBehavior
extends Enum<PistonBehavior> {
    final static public PistonBehavior NORMAL = new PistonBehavior();
    final static public PistonBehavior DESTROY = new PistonBehavior();
    final static public PistonBehavior BLOCK = new PistonBehavior();
    final static public PistonBehavior IGNORE = new PistonBehavior();
    final static public PistonBehavior PUSH_ONLY = new PistonBehavior();
    final static private PistonBehavior[] field_15973;

    public static PistonBehavior[] values() {
        return (PistonBehavior[])field_15973.clone();
    }

    public static PistonBehavior valueOf(String string) {
        return Enum.valueOf(PistonBehavior.class, string);
    }

    private static PistonBehavior[] method_36765() {
        return new PistonBehavior[]{NORMAL, DESTROY, BLOCK, IGNORE, PUSH_ONLY};
    }

    static {
        field_15973 = PistonBehavior.method_36765();
    }
}

