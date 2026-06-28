/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.function;

public interface BooleanBiFunction {
    final static public BooleanBiFunction FALSE = (a, b) -> false;
    final static public BooleanBiFunction NOT_OR = (a, b) -> !a && !b;
    final static public BooleanBiFunction ONLY_SECOND = (a, b) -> b && !a;
    final static public BooleanBiFunction NOT_FIRST = (a, b) -> !a;
    final static public BooleanBiFunction ONLY_FIRST = (a, b) -> a && !b;
    final static public BooleanBiFunction NOT_SECOND = (a, b) -> !b;
    final static public BooleanBiFunction NOT_SAME = (a, b) -> a != b;
    final static public BooleanBiFunction NOT_AND = (a, b) -> !a || !b;
    final static public BooleanBiFunction AND = (a, b) -> a && b;
    final static public BooleanBiFunction SAME = (a, b) -> a == b;
    final static public BooleanBiFunction SECOND = (a, b) -> b;
    final static public BooleanBiFunction CAUSES = (a, b) -> !a || b;
    final static public BooleanBiFunction FIRST = (a, b) -> a;
    final static public BooleanBiFunction CAUSED_BY = (a, b) -> a || !b;
    final static public BooleanBiFunction OR = (a, b) -> a || b;
    final static public BooleanBiFunction TRUE = (a, b) -> true;

    public boolean apply(boolean var1, boolean var2);
}

