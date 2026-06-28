/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public sealed interface ActionResult {
    final static public Success SUCCESS = new Success(SwingSource.CLIENT, ItemContext.KEEP_HAND_STACK);
    final static public Success SUCCESS_SERVER = new Success(SwingSource.SERVER, ItemContext.KEEP_HAND_STACK);
    final static public Success CONSUME = new Success(SwingSource.NONE, ItemContext.KEEP_HAND_STACK);
    final static public Fail FAIL = new Fail();
    final static public Pass PASS = new Pass();
    final static public PassToDefaultBlockAction PASS_TO_DEFAULT_BLOCK_ACTION = new PassToDefaultBlockAction();

    default public boolean isAccepted() {
        return false;
    }

    public record Success(SwingSource swingSource, ItemContext itemContext) implements ActionResult
    {
        @Override
        public boolean isAccepted() {
            return true;
        }

        public Success withNewHandStack(ItemStack newHandStack) {
            return new Success(this.swingSource, new ItemContext(true, newHandStack));
        }

        public Success noIncrementStat() {
            return new Success(this.swingSource, ItemContext.KEEP_HAND_STACK_NO_INCREMENT_STAT);
        }

        public boolean shouldIncrementStat() {
            return this.itemContext.incrementStat;
        }

        @Nullable
        public ItemStack getNewHandStack() {
            return this.itemContext.newHandStack;
        }
    }

    public static final class SwingSource
    extends Enum<SwingSource> {
        final static public SwingSource NONE = new SwingSource();
        final static public SwingSource CLIENT = new SwingSource();
        final static public SwingSource SERVER = new SwingSource();
        final static private SwingSource[] field_52429;

        public static SwingSource[] values() {
            return (SwingSource[])field_52429.clone();
        }

        public static SwingSource valueOf(String string) {
            return Enum.valueOf(SwingSource.class, string);
        }

        private static SwingSource[] method_61397() {
            return new SwingSource[]{NONE, CLIENT, SERVER};
        }

        static {
            field_52429 = SwingSource.method_61397();
        }
    }

    public static final class ItemContext
    extends Record {
        final boolean incrementStat;
        @Nullable
        final ItemStack newHandStack;
        static ItemContext KEEP_HAND_STACK_NO_INCREMENT_STAT = new ItemContext(false, null);
        static ItemContext KEEP_HAND_STACK = new ItemContext(true, null);

        public ItemContext(boolean bl, @Nullable ItemStack itemStack) {
            this.incrementStat = bl;
            this.newHandStack = itemStack;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "incrementStat", "newHandStack"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "incrementStat", "newHandStack"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "incrementStat", "newHandStack"}, this, object);
        }

        public boolean incrementStat() {
            return this.incrementStat;
        }

        @Nullable
        public ItemStack newHandStack() {
            return this.newHandStack;
        }
    }

    public record Fail() implements ActionResult
    {
    }

    public record Pass() implements ActionResult
    {
    }

    public record PassToDefaultBlockAction() implements ActionResult
    {
    }
}

