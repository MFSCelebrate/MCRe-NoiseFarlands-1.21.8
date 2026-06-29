/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.packrat;

public interface Cut {
    final static public Cut NOOP = new Cut(){

        @Override
        public void cut() {
        }

        @Override
        public boolean isCut() {
            return false;
        }
    };

    public void cut();

    public boolean isCut();
}

