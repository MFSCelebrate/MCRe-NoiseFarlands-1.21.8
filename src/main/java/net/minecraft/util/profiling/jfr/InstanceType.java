/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiling.jfr;

import net.minecraft.server.MinecraftServer;

public final class InstanceType
extends Enum<InstanceType> {
    final static public InstanceType CLIENT = new InstanceType("client");
    final static public InstanceType SERVER = new InstanceType("server");
    final private String name;
    final static private InstanceType[] field_34415;

    public static InstanceType[] values() {
        return (InstanceType[])field_34415.clone();
    }

    public static InstanceType valueOf(String string) {
        return Enum.valueOf(InstanceType.class, string);
    }

    private InstanceType(String name) {
        this.name = name;
    }

    public static InstanceType get(MinecraftServer server) {
        return server.isDedicated() ? SERVER : CLIENT;
    }

    public String getName() {
        return this.name;
    }

    private static InstanceType[] method_37988() {
        return new InstanceType[]{CLIENT, SERVER};
    }

    static {
        field_34415 = InstanceType.method_37988();
    }
}

