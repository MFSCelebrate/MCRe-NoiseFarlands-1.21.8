/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.item;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class ItemDisplayContext
extends Enum<ItemDisplayContext>
implements StringIdentifiable {
    final static public ItemDisplayContext NONE = new ItemDisplayContext(0, "none");
    final static public ItemDisplayContext THIRD_PERSON_LEFT_HAND = new ItemDisplayContext(1, "thirdperson_lefthand");
    final static public ItemDisplayContext THIRD_PERSON_RIGHT_HAND = new ItemDisplayContext(2, "thirdperson_righthand");
    final static public ItemDisplayContext FIRST_PERSON_LEFT_HAND = new ItemDisplayContext(3, "firstperson_lefthand");
    final static public ItemDisplayContext FIRST_PERSON_RIGHT_HAND = new ItemDisplayContext(4, "firstperson_righthand");
    final static public ItemDisplayContext HEAD = new ItemDisplayContext(5, "head");
    final static public ItemDisplayContext GUI = new ItemDisplayContext(6, "gui");
    final static public ItemDisplayContext GROUND = new ItemDisplayContext(7, "ground");
    final static public ItemDisplayContext FIXED = new ItemDisplayContext(8, "fixed");
    final static public Codec<ItemDisplayContext> CODEC;
    final static public IntFunction<ItemDisplayContext> FROM_INDEX;
    final private byte index;
    final private String name;
    final static private ItemDisplayContext[] field_4314;

    public static ItemDisplayContext[] values() {
        return (ItemDisplayContext[])field_4314.clone();
    }

    public static ItemDisplayContext valueOf(String string) {
        return Enum.valueOf(ItemDisplayContext.class, string);
    }

    private ItemDisplayContext(int index, String name) {
        this.name = name;
        this.index = (byte)index;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public byte getIndex() {
        return this.index;
    }

    public boolean isFirstPerson() {
        return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
    }

    public boolean isLeftHand() {
        return this == FIRST_PERSON_LEFT_HAND || this == THIRD_PERSON_LEFT_HAND;
    }

    private static ItemDisplayContext[] method_36922() {
        return new ItemDisplayContext[]{NONE, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, HEAD, GUI, GROUND, FIXED};
    }

    static {
        field_4314 = ItemDisplayContext.method_36922();
        CODEC = StringIdentifiable.createCodec(ItemDisplayContext::values);
        FROM_INDEX = ValueLists.createIndexToValueFunction(ItemDisplayContext::getIndex, ItemDisplayContext.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}

