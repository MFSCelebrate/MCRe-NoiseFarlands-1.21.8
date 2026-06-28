/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.item.equipment;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.StringIdentifiable;

public final class EquipmentType
extends Enum<EquipmentType>
implements StringIdentifiable {
    final static public EquipmentType HELMET = new EquipmentType(EquipmentSlot.HEAD, 11, "helmet");
    final static public EquipmentType CHESTPLATE = new EquipmentType(EquipmentSlot.CHEST, 16, "chestplate");
    final static public EquipmentType LEGGINGS = new EquipmentType(EquipmentSlot.LEGS, 15, "leggings");
    final static public EquipmentType BOOTS = new EquipmentType(EquipmentSlot.FEET, 13, "boots");
    final static public EquipmentType BODY = new EquipmentType(EquipmentSlot.BODY, 16, "body");
    final static public Codec<EquipmentType> CODEC;
    final private EquipmentSlot equipmentSlot;
    final private String name;
    final private int baseMaxDamage;
    final static private EquipmentType[] field_41940;

    public static EquipmentType[] values() {
        return (EquipmentType[])field_41940.clone();
    }

    public static EquipmentType valueOf(String string) {
        return Enum.valueOf(EquipmentType.class, string);
    }

    private EquipmentType(EquipmentSlot equipmentSlot, int baseMaxDamage, String name) {
        this.equipmentSlot = equipmentSlot;
        this.name = name;
        this.baseMaxDamage = baseMaxDamage;
    }

    public int getMaxDamage(int multiplier) {
        return this.baseMaxDamage * multiplier;
    }

    public EquipmentSlot getEquipmentSlot() {
        return this.equipmentSlot;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static EquipmentType[] method_48401() {
        return new EquipmentType[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY};
    }

    static {
        field_41940 = EquipmentType.method_48401();
        CODEC = StringIdentifiable.createBasicCodec(EquipmentType::values);
    }
}

