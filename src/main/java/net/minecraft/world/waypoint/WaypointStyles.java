/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.waypoint;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.waypoint.WaypointStyle;

public interface WaypointStyles {
    final static public RegistryKey<? extends Registry<WaypointStyle>> REGISTRY = RegistryKey.ofRegistry(Identifier.ofVanilla("waypoint_style_asset"));
    final static public RegistryKey<WaypointStyle> DEFAULT = WaypointStyles.of("default");
    final static public RegistryKey<WaypointStyle> BOWTIE = WaypointStyles.of("bowtie");

    public static RegistryKey<WaypointStyle> of(String id) {
        return RegistryKey.of(REGISTRY, Identifier.ofVanilla(id));
    }
}

