/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

public class ServerTask
implements Runnable {
    final private int creationTicks;
    final private Runnable runnable;

    public ServerTask(int creationTicks, Runnable runnable) {
        this.creationTicks = creationTicks;
        this.runnable = runnable;
    }

    public int getCreationTicks() {
        return this.creationTicks;
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}

