/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class EquipmentModelLoader
extends JsonDataLoader<EquipmentModel> {
    final static public EquipmentModel EMPTY = new EquipmentModel(Map.of());
    final static private ResourceFinder FINDER = ResourceFinder.json("equipment");
    private Map<RegistryKey<EquipmentAsset>, EquipmentModel> models = Map.of();

    public EquipmentModelLoader() {
        super(EquipmentModel.CODEC, FINDER);
    }

    @Override
    protected void apply(Map<Identifier, EquipmentModel> map, ResourceManager resourceManager, Profiler profiler) {
        this.models = map.entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, (Identifier)entry.getKey()), Map.Entry::getValue));
    }

    public EquipmentModel get(RegistryKey<EquipmentAsset> assetKey) {
        return this.models.getOrDefault(assetKey, EMPTY);
    }
}

