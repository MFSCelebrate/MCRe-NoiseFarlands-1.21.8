/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.tooltip;

public interface TooltipType {
    final static public Default BASIC = new Default(false, false);
    final static public Default ADVANCED = new Default(true, false);

    public boolean isAdvanced();

    public boolean isCreative();

    public record Default(boolean advanced, boolean creative) implements TooltipType
    {
        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }

        @Override
        public boolean isCreative() {
            return this.creative;
        }

        public Default withCreative() {
            return new Default(this.advanced, true);
        }
    }
}

