/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component.type;

import net.minecraft.component.type.FoodComponent;

public class FoodComponents {
    final static public FoodComponent APPLE = new FoodComponent.Builder().nutrition(4).saturationModifier(0.3f).build();
    final static public FoodComponent BAKED_POTATO = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6f).build();
    final static public FoodComponent BEEF = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3f).build();
    final static public FoodComponent BEETROOT = new FoodComponent.Builder().nutrition(1).saturationModifier(0.6f).build();
    final static public FoodComponent BEETROOT_SOUP = FoodComponents.createStew(6).build();
    final static public FoodComponent BREAD = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6f).build();
    final static public FoodComponent CARROT = new FoodComponent.Builder().nutrition(3).saturationModifier(0.6f).build();
    final static public FoodComponent CHICKEN = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3f).build();
    final static public FoodComponent CHORUS_FRUIT = new FoodComponent.Builder().nutrition(4).saturationModifier(0.3f).alwaysEdible().build();
    final static public FoodComponent COD = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1f).build();
    final static public FoodComponent COOKED_BEEF = new FoodComponent.Builder().nutrition(8).saturationModifier(0.8f).build();
    final static public FoodComponent COOKED_CHICKEN = new FoodComponent.Builder().nutrition(6).saturationModifier(0.6f).build();
    final static public FoodComponent COOKED_COD = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6f).build();
    final static public FoodComponent COOKED_MUTTON = new FoodComponent.Builder().nutrition(6).saturationModifier(0.8f).build();
    final static public FoodComponent COOKED_PORKCHOP = new FoodComponent.Builder().nutrition(8).saturationModifier(0.8f).build();
    final static public FoodComponent COOKED_RABBIT = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6f).build();
    final static public FoodComponent COOKED_SALMON = new FoodComponent.Builder().nutrition(6).saturationModifier(0.8f).build();
    final static public FoodComponent COOKIE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1f).build();
    final static public FoodComponent DRIED_KELP = new FoodComponent.Builder().nutrition(1).saturationModifier(0.3f).build();
    final static public FoodComponent ENCHANTED_GOLDEN_APPLE = new FoodComponent.Builder().nutrition(4).saturationModifier(1.2f).alwaysEdible().build();
    final static public FoodComponent GOLDEN_APPLE = new FoodComponent.Builder().nutrition(4).saturationModifier(1.2f).alwaysEdible().build();
    final static public FoodComponent GOLDEN_CARROT = new FoodComponent.Builder().nutrition(6).saturationModifier(1.2f).build();
    final static public FoodComponent HONEY_BOTTLE = new FoodComponent.Builder().nutrition(6).saturationModifier(0.1f).alwaysEdible().build();
    final static public FoodComponent MELON_SLICE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3f).build();
    final static public FoodComponent MUSHROOM_STEW = FoodComponents.createStew(6).build();
    final static public FoodComponent MUTTON = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3f).build();
    final static public FoodComponent POISONOUS_POTATO = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3f).build();
    final static public FoodComponent PORKCHOP = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3f).build();
    final static public FoodComponent POTATO = new FoodComponent.Builder().nutrition(1).saturationModifier(0.3f).build();
    final static public FoodComponent PUFFERFISH = new FoodComponent.Builder().nutrition(1).saturationModifier(0.1f).build();
    final static public FoodComponent PUMPKIN_PIE = new FoodComponent.Builder().nutrition(8).saturationModifier(0.3f).build();
    final static public FoodComponent RABBIT = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3f).build();
    final static public FoodComponent RABBIT_STEW = FoodComponents.createStew(10).build();
    final static public FoodComponent ROTTEN_FLESH = new FoodComponent.Builder().nutrition(4).saturationModifier(0.1f).build();
    final static public FoodComponent SALMON = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1f).build();
    final static public FoodComponent SPIDER_EYE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.8f).build();
    final static public FoodComponent SUSPICIOUS_STEW = FoodComponents.createStew(6).alwaysEdible().build();
    final static public FoodComponent SWEET_BERRIES = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1f).build();
    final static public FoodComponent GLOW_BERRIES = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1f).build();
    final static public FoodComponent TROPICAL_FISH = new FoodComponent.Builder().nutrition(1).saturationModifier(0.1f).build();

    private static FoodComponent.Builder createStew(int nutrition) {
        return new FoodComponent.Builder().nutrition(nutrition).saturationModifier(0.6f);
    }
}

