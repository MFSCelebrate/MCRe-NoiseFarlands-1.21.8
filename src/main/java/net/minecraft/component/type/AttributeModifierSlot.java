/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class AttributeModifierSlot
extends Enum<AttributeModifierSlot>
implements StringIdentifiable,
Iterable<EquipmentSlot> {
    final static public AttributeModifierSlot ANY = new AttributeModifierSlot(0, "any", slot -> true);
    final static public AttributeModifierSlot MAINHAND = new AttributeModifierSlot(1, "mainhand", EquipmentSlot.MAINHAND);
    final static public AttributeModifierSlot OFFHAND = new AttributeModifierSlot(2, "offhand", EquipmentSlot.OFFHAND);
    final static public AttributeModifierSlot HAND = new AttributeModifierSlot(3, "hand", slot -> slot.getType() == EquipmentSlot.Type.HAND);
    final static public AttributeModifierSlot FEET = new AttributeModifierSlot(4, "feet", EquipmentSlot.FEET);
    final static public AttributeModifierSlot LEGS = new AttributeModifierSlot(5, "legs", EquipmentSlot.LEGS);
    final static public AttributeModifierSlot CHEST = new AttributeModifierSlot(6, "chest", EquipmentSlot.CHEST);
    final static public AttributeModifierSlot HEAD = new AttributeModifierSlot(7, "head", EquipmentSlot.HEAD);
    final static public AttributeModifierSlot ARMOR = new AttributeModifierSlot(8, "armor", EquipmentSlot::isArmorSlot);
    final static public AttributeModifierSlot BODY = new AttributeModifierSlot(9, "body", EquipmentSlot.BODY);
    final static public AttributeModifierSlot SADDLE = new AttributeModifierSlot(10, "saddle", EquipmentSlot.SADDLE);
    final static public IntFunction<AttributeModifierSlot> ID_TO_VALUE;
    final static public Codec<AttributeModifierSlot> CODEC;
    final static public PacketCodec<ByteBuf, AttributeModifierSlot> PACKET_CODEC;
    final private int id;
    final private String name;
    final private Predicate<EquipmentSlot> slotPredicate;
    final private List<EquipmentSlot> slots;
    final static private AttributeModifierSlot[] field_49231;

    public static AttributeModifierSlot[] values() {
        return (AttributeModifierSlot[])field_49231.clone();
    }

    public static AttributeModifierSlot valueOf(String string) {
        return Enum.valueOf(AttributeModifierSlot.class, string);
    }

    private AttributeModifierSlot(int id, String name, Predicate<EquipmentSlot> slotPredicate) {
        this.id = id;
        this.name = name;
        this.slotPredicate = slotPredicate;
        this.slots = EquipmentSlot.VALUES.stream().filter(slotPredicate).toList();
    }

    private AttributeModifierSlot(int id, String name, EquipmentSlot slot) {
        this(id, name, (EquipmentSlot slotx) -> slotx == slot);
    }

    public static AttributeModifierSlot forEquipmentSlot(EquipmentSlot slot) {
        return switch (slot) {
            default -> throw new MatchException(null, null);
            case EquipmentSlot.MAINHAND -> MAINHAND;
            case EquipmentSlot.OFFHAND -> OFFHAND;
            case EquipmentSlot.FEET -> FEET;
            case EquipmentSlot.LEGS -> LEGS;
            case EquipmentSlot.CHEST -> CHEST;
            case EquipmentSlot.HEAD -> HEAD;
            case EquipmentSlot.BODY -> BODY;
            case EquipmentSlot.SADDLE -> SADDLE;
        };
    }

    @Override
    public String asString() {
        return this.name;
    }

    public boolean matches(EquipmentSlot slot) {
        return this.slotPredicate.test(slot);
    }

    public List<EquipmentSlot> getSlots() {
        return this.slots;
    }

    @Override
    public Iterator<EquipmentSlot> iterator() {
        return this.slots.iterator();
    }

    private static AttributeModifierSlot[] method_57285() {
        return new AttributeModifierSlot[]{ANY, MAINHAND, OFFHAND, HAND, FEET, LEGS, CHEST, HEAD, ARMOR, BODY, SADDLE};
    }

    static {
        field_49231 = AttributeModifierSlot.method_57285();
        ID_TO_VALUE = ValueLists.createIndexToValueFunction(id -> id.id, AttributeModifierSlot.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(AttributeModifierSlot::values);
        PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, id -> id.id);
    }
}

