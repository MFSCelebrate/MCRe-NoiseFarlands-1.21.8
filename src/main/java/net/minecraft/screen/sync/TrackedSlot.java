/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.screen.sync;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;
import org.jetbrains.annotations.Nullable;

public interface TrackedSlot {
    final static public TrackedSlot ALWAYS_IN_SYNC = new TrackedSlot(){

        @Override
        public void setReceivedHash(ItemStackHash receivedHash) {
        }

        @Override
        public void setReceivedStack(ItemStack receivedStack) {
        }

        @Override
        public boolean isInSync(ItemStack actualStack) {
            return true;
        }
    };

    public void setReceivedStack(ItemStack var1);

    public void setReceivedHash(ItemStackHash var1);

    public boolean isInSync(ItemStack var1);

    public static class Impl
    implements TrackedSlot {
        final private ComponentChangesHash.ComponentHasher hasher;
        @Nullable
        private ItemStack receivedStack = null;
        @Nullable
        private ItemStackHash receivedHash = null;

        public Impl(ComponentChangesHash.ComponentHasher hasher) {
            this.hasher = hasher;
        }

        @Override
        public void setReceivedStack(ItemStack receivedStack) {
            this.receivedStack = receivedStack.copy();
            this.receivedHash = null;
        }

        @Override
        public void setReceivedHash(ItemStackHash receivedHash) {
            this.receivedStack = null;
            this.receivedHash = receivedHash;
        }

        @Override
        public boolean isInSync(ItemStack actualStack) {
            if (this.receivedStack != null) {
                return ItemStack.areEqual(this.receivedStack, actualStack);
            }
            if (this.receivedHash != null && this.receivedHash.hashEquals(actualStack, this.hasher)) {
                this.receivedStack = actualStack.copy();
                return true;
            }
            return false;
        }

        public void copyFrom(Impl slot) {
            this.receivedStack = slot.receivedStack;
            this.receivedHash = slot.receivedHash;
        }
    }
}

