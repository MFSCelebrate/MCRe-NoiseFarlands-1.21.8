/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.equipment;

import java.util.Map;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface EquipmentAssetKeys {
    final static public RegistryKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset"));
    final static public RegistryKey<EquipmentAsset> LEATHER = EquipmentAssetKeys.register("leather");
    final static public RegistryKey<EquipmentAsset> CHAINMAIL = EquipmentAssetKeys.register("chainmail");
    final static public RegistryKey<EquipmentAsset> IRON = EquipmentAssetKeys.register("iron");
    final static public RegistryKey<EquipmentAsset> GOLD = EquipmentAssetKeys.register("gold");
    final static public RegistryKey<EquipmentAsset> DIAMOND = EquipmentAssetKeys.register("diamond");
    final static public RegistryKey<EquipmentAsset> TURTLE_SCUTE = EquipmentAssetKeys.register("turtle_scute");
    final static public RegistryKey<EquipmentAsset> NETHERITE = EquipmentAssetKeys.register("netherite");
    final static public RegistryKey<EquipmentAsset> ARMADILLO_SCUTE = EquipmentAssetKeys.register("armadillo_scute");
    final static public RegistryKey<EquipmentAsset> ELYTRA = EquipmentAssetKeys.register("elytra");
    final static public RegistryKey<EquipmentAsset> SADDLE = EquipmentAssetKeys.register("saddle");
    final static public Map<DyeColor, RegistryKey<EquipmentAsset>> CARPET_FROM_COLOR = Util.mapEnum(DyeColor.class, color -> EquipmentAssetKeys.register(color.asString() + "_carpet"));
    final static public RegistryKey<EquipmentAsset> TRADER_LLAMA = EquipmentAssetKeys.register("trader_llama");
    final static public Map<DyeColor, RegistryKey<EquipmentAsset>> HARNESS_FROM_COLOR = Util.mapEnum(DyeColor.class, color -> EquipmentAssetKeys.register(color.asString() + "_harness"));

    public static RegistryKey<EquipmentAsset> register(String name) {
        return RegistryKey.of(REGISTRY_KEY, Identifier.ofVanilla(name));
    }
}

