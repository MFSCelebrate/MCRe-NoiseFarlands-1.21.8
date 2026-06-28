/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

public class HungerConstants {
    final static public int FULL_FOOD_LEVEL = 20;
    final static public float field_30705 = 20.0f;
    final static public float INITIAL_SATURATION_LEVEL = 5.0f;
    final static public float field_30707 = 2.5f;
    final static public float EXHAUSTION_UNIT = 4.0f;
    final static public int SLOW_HEALING_STARVING_INTERVAL = 80;
    final static public int FAST_HEALING_INTERVAL = 10;
    final static public int SLOW_HEALING_FOOD_LEVEL = 18;
    final static public int EXHAUSTION_PER_HITPOINT = 6;
    final static public int STARVING_FOOD_LEVEL = 0;
    final static public float field_30714 = 0.1f;
    final static public float field_30715 = 0.3f;
    final static public float field_30716 = 0.6f;
    final static public float field_30717 = 0.8f;
    final static public float field_30718 = 1.0f;
    final static public float field_30719 = 1.2f;
    final static public float field_30720 = 6.0f;
    final static public float field_30721 = 0.05f;
    final static public float field_30722 = 0.2f;
    final static public float field_30723 = 0.005f;
    final static public float field_30724 = 0.1f;
    final static public float field_30726 = 0.0f;
    final static public float field_30727 = 0.0f;
    final static public float field_30728 = 0.1f;
    final static public float field_30729 = 0.01f;

    public static float calculateSaturation(int nutrition, float saturationModifier) {
        return (float)nutrition * saturationModifier * 2.0f;
    }
}

