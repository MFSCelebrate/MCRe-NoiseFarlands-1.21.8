/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.List;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;

public final class ServerDynamicRegistryType
extends Enum<ServerDynamicRegistryType> {
    final static public ServerDynamicRegistryType STATIC = new ServerDynamicRegistryType();
    final static public ServerDynamicRegistryType WORLDGEN = new ServerDynamicRegistryType();
    final static public ServerDynamicRegistryType DIMENSIONS = new ServerDynamicRegistryType();
    final static public ServerDynamicRegistryType RELOADABLE = new ServerDynamicRegistryType();
    final static private List<ServerDynamicRegistryType> VALUES;
    final static private DynamicRegistryManager.Immutable STATIC_REGISTRY_MANAGER;
    final static private ServerDynamicRegistryType[] field_39977;

    public static ServerDynamicRegistryType[] values() {
        return (ServerDynamicRegistryType[])field_39977.clone();
    }

    public static ServerDynamicRegistryType valueOf(String string) {
        return Enum.valueOf(ServerDynamicRegistryType.class, string);
    }

    public static CombinedDynamicRegistries<ServerDynamicRegistryType> createCombinedDynamicRegistries() {
        return new CombinedDynamicRegistries<ServerDynamicRegistryType>(VALUES).with(STATIC, STATIC_REGISTRY_MANAGER);
    }

    private static ServerDynamicRegistryType[] method_45140() {
        return new ServerDynamicRegistryType[]{STATIC, WORLDGEN, DIMENSIONS, RELOADABLE};
    }

    static {
        field_39977 = ServerDynamicRegistryType.method_45140();
        VALUES = List.of(ServerDynamicRegistryType.values());
        STATIC_REGISTRY_MANAGER = DynamicRegistryManager.of(Registries.REGISTRIES);
    }
}

