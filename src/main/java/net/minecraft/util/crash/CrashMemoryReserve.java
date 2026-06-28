/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.crash;

import org.jetbrains.annotations.Nullable;

public class CrashMemoryReserve {
    @Nullable
    static private byte[] reservedMemory;

    public static void reserveMemory() {
        reservedMemory = new byte[0xA00000];
    }

    public static void releaseMemory() {
        if (reservedMemory != null) {
            reservedMemory = null;
            try {
                System.gc();
                System.gc();
                System.gc();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

