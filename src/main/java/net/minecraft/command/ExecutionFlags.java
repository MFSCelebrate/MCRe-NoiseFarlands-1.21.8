/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

public record ExecutionFlags(byte flags) {
    final static public ExecutionFlags NONE = new ExecutionFlags(0);
    final static private byte SILENT = 1;
    final static private byte INSIDE_RETURN_RUN = 2;

    private ExecutionFlags set(byte flag) {
        int i = this.flags | flag;
        return i != this.flags ? new ExecutionFlags((byte)i) : this;
    }

    public boolean isSilent() {
        return (this.flags & 1) != 0;
    }

    public ExecutionFlags setSilent() {
        return this.set(1);
    }

    public boolean isInsideReturnRun() {
        return (this.flags & 2) != 0;
    }

    public ExecutionFlags setInsideReturnRun() {
        return this.set(2);
    }
}

