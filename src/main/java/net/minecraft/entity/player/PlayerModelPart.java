/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import net.minecraft.text.Text;

public final class PlayerModelPart
extends Enum<PlayerModelPart> {
    final static public PlayerModelPart CAPE = new PlayerModelPart(0, "cape");
    final static public PlayerModelPart JACKET = new PlayerModelPart(1, "jacket");
    final static public PlayerModelPart LEFT_SLEEVE = new PlayerModelPart(2, "left_sleeve");
    final static public PlayerModelPart RIGHT_SLEEVE = new PlayerModelPart(3, "right_sleeve");
    final static public PlayerModelPart LEFT_PANTS_LEG = new PlayerModelPart(4, "left_pants_leg");
    final static public PlayerModelPart RIGHT_PANTS_LEG = new PlayerModelPart(5, "right_pants_leg");
    final static public PlayerModelPart HAT = new PlayerModelPart(6, "hat");
    final private int id;
    final private int bitFlag;
    final private String name;
    final private Text optionName;
    final static private PlayerModelPart[] field_7562;

    public static PlayerModelPart[] values() {
        return (PlayerModelPart[])field_7562.clone();
    }

    public static PlayerModelPart valueOf(String string) {
        return Enum.valueOf(PlayerModelPart.class, string);
    }

    private PlayerModelPart(int id, String name) {
        this.id = id;
        this.bitFlag = 1 << id;
        this.name = name;
        this.optionName = Text.translatable("options.modelPart." + name);
    }

    public int getBitFlag() {
        return this.bitFlag;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Text getOptionName() {
        return this.optionName;
    }

    private static PlayerModelPart[] method_36662() {
        return new PlayerModelPart[]{CAPE, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS_LEG, RIGHT_PANTS_LEG, HAT};
    }

    static {
        field_7562 = PlayerModelPart.method_36662();
    }
}

