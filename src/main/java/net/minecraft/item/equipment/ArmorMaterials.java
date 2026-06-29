/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.item.equipment;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;

public interface ArmorMaterials {
    final static public ArmorMaterial LEATHER = new ArmorMaterial(5, ArmorMaterials.createDefenseMap(1, 2, 3, 1, 3), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, ItemTags.REPAIRS_LEATHER_ARMOR, EquipmentAssetKeys.LEATHER);
    final static public ArmorMaterial CHAIN = new ArmorMaterial(15, ArmorMaterials.createDefenseMap(1, 4, 5, 2, 4), 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, ItemTags.REPAIRS_CHAIN_ARMOR, EquipmentAssetKeys.CHAINMAIL);
    final static public ArmorMaterial IRON = new ArmorMaterial(15, ArmorMaterials.createDefenseMap(2, 5, 6, 2, 5), 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, 0.0f, ItemTags.REPAIRS_IRON_ARMOR, EquipmentAssetKeys.IRON);
    final static public ArmorMaterial GOLD = new ArmorMaterial(7, ArmorMaterials.createDefenseMap(1, 3, 5, 2, 7), 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0f, 0.0f, ItemTags.REPAIRS_GOLD_ARMOR, EquipmentAssetKeys.GOLD);
    final static public ArmorMaterial DIAMOND = new ArmorMaterial(33, ArmorMaterials.createDefenseMap(3, 6, 8, 3, 11), 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f, 0.0f, ItemTags.REPAIRS_DIAMOND_ARMOR, EquipmentAssetKeys.DIAMOND);
    final static public ArmorMaterial TURTLE_SCUTE = new ArmorMaterial(25, ArmorMaterials.createDefenseMap(2, 5, 6, 2, 5), 9, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.0f, 0.0f, ItemTags.REPAIRS_TURTLE_HELMET, EquipmentAssetKeys.TURTLE_SCUTE);
    final static public ArmorMaterial NETHERITE = new ArmorMaterial(37, ArmorMaterials.createDefenseMap(3, 6, 8, 3, 11), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssetKeys.NETHERITE);
    final static public ArmorMaterial ARMADILLO_SCUTE = new ArmorMaterial(4, ArmorMaterials.createDefenseMap(3, 6, 8, 3, 11), 10, SoundEvents.ITEM_ARMOR_EQUIP_WOLF, 0.0f, 0.0f, ItemTags.REPAIRS_WOLF_ARMOR, EquipmentAssetKeys.ARMADILLO_SCUTE);

    private static Map<EquipmentType, Integer> createDefenseMap(int bootsDefense, int leggingsDefense, int chestplateDefense, int helmetDefense, int bodyDefense) {
        return Maps.newEnumMap(Map.of(EquipmentType.BOOTS, bootsDefense, EquipmentType.LEGGINGS, leggingsDefense, EquipmentType.CHESTPLATE, chestplateDefense, EquipmentType.HELMET, helmetDefense, EquipmentType.BODY, bodyDefense));
    }
}

