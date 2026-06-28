/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

public final class OffThreadException
extends RuntimeException {
    final static public OffThreadException INSTANCE = new OffThreadException();

    private OffThreadException() {
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        this.setStackTrace(new StackTraceElement[0]);
        return this;
    }
}

