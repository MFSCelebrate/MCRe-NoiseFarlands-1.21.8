/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.waypoint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.waypoint.WaypointStyle;
import net.minecraft.world.waypoint.WaypointStyles;

@Environment(value=EnvType.CLIENT)
public class WaypointStyleAssetManager
extends JsonDataLoader<WaypointStyleAsset> {
    final static private ResourceFinder FINDER = ResourceFinder.json("waypoint_style");
    final static private WaypointStyleAsset MISSING = new WaypointStyleAsset(0, 1, List.of(MissingSprite.getMissingSpriteId()));
    private Map<RegistryKey<WaypointStyle>, WaypointStyleAsset> registry = Map.of();

    public WaypointStyleAssetManager() {
        super(WaypointStyleAsset.CODEC, FINDER);
    }

    @Override
    protected void apply(Map<Identifier, WaypointStyleAsset> map, ResourceManager resourceManager, Profiler profiler) {
        this.registry = map.entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> RegistryKey.of(WaypointStyles.REGISTRY, (Identifier)entry.getKey()), Map.Entry::getValue));
    }

    public WaypointStyleAsset get(RegistryKey<WaypointStyle> key) {
        return this.registry.getOrDefault(key, MISSING);
    }
}

