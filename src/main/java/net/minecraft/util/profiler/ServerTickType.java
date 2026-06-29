/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiler;

public final class ServerTickType
extends Enum<ServerTickType> {
    final static public ServerTickType FULL_TICK = new ServerTickType();
    final static public ServerTickType TICK_SERVER_METHOD = new ServerTickType();
    final static public ServerTickType SCHEDULED_TASKS = new ServerTickType();
    final static public ServerTickType IDLE = new ServerTickType();
    final static private ServerTickType[] field_48722;

    public static ServerTickType[] values() {
        return (ServerTickType[])field_48722.clone();
    }

    public static ServerTickType valueOf(String string) {
        return Enum.valueOf(ServerTickType.class, string);
    }

    private static ServerTickType[] method_56536() {
        return new ServerTickType[]{FULL_TICK, TICK_SERVER_METHOD, SCHEDULED_TASKS, IDLE};
    }

    static {
        field_48722 = ServerTickType.method_56536();
    }
}

