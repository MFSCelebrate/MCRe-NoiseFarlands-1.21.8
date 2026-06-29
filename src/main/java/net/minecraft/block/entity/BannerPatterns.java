/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BannerPatterns {
    final static public RegistryKey<BannerPattern> BASE = BannerPatterns.of("base");
    final static public RegistryKey<BannerPattern> SQUARE_BOTTOM_LEFT = BannerPatterns.of("square_bottom_left");
    final static public RegistryKey<BannerPattern> SQUARE_BOTTOM_RIGHT = BannerPatterns.of("square_bottom_right");
    final static public RegistryKey<BannerPattern> SQUARE_TOP_LEFT = BannerPatterns.of("square_top_left");
    final static public RegistryKey<BannerPattern> SQUARE_TOP_RIGHT = BannerPatterns.of("square_top_right");
    final static public RegistryKey<BannerPattern> STRIPE_BOTTOM = BannerPatterns.of("stripe_bottom");
    final static public RegistryKey<BannerPattern> STRIPE_TOP = BannerPatterns.of("stripe_top");
    final static public RegistryKey<BannerPattern> STRIPE_LEFT = BannerPatterns.of("stripe_left");
    final static public RegistryKey<BannerPattern> STRIPE_RIGHT = BannerPatterns.of("stripe_right");
    final static public RegistryKey<BannerPattern> STRIPE_CENTER = BannerPatterns.of("stripe_center");
    final static public RegistryKey<BannerPattern> STRIPE_MIDDLE = BannerPatterns.of("stripe_middle");
    final static public RegistryKey<BannerPattern> STRIPE_DOWNRIGHT = BannerPatterns.of("stripe_downright");
    final static public RegistryKey<BannerPattern> STRIPE_DOWNLEFT = BannerPatterns.of("stripe_downleft");
    final static public RegistryKey<BannerPattern> SMALL_STRIPES = BannerPatterns.of("small_stripes");
    final static public RegistryKey<BannerPattern> CROSS = BannerPatterns.of("cross");
    final static public RegistryKey<BannerPattern> STRAIGHT_CROSS = BannerPatterns.of("straight_cross");
    final static public RegistryKey<BannerPattern> TRIANGLE_BOTTOM = BannerPatterns.of("triangle_bottom");
    final static public RegistryKey<BannerPattern> TRIANGLE_TOP = BannerPatterns.of("triangle_top");
    final static public RegistryKey<BannerPattern> TRIANGLES_BOTTOM = BannerPatterns.of("triangles_bottom");
    final static public RegistryKey<BannerPattern> TRIANGLES_TOP = BannerPatterns.of("triangles_top");
    final static public RegistryKey<BannerPattern> DIAGONAL_LEFT = BannerPatterns.of("diagonal_left");
    final static public RegistryKey<BannerPattern> DIAGONAL_UP_RIGHT = BannerPatterns.of("diagonal_up_right");
    final static public RegistryKey<BannerPattern> DIAGONAL_UP_LEFT = BannerPatterns.of("diagonal_up_left");
    final static public RegistryKey<BannerPattern> DIAGONAL_RIGHT = BannerPatterns.of("diagonal_right");
    final static public RegistryKey<BannerPattern> CIRCLE = BannerPatterns.of("circle");
    final static public RegistryKey<BannerPattern> RHOMBUS = BannerPatterns.of("rhombus");
    final static public RegistryKey<BannerPattern> HALF_VERTICAL = BannerPatterns.of("half_vertical");
    final static public RegistryKey<BannerPattern> HALF_HORIZONTAL = BannerPatterns.of("half_horizontal");
    final static public RegistryKey<BannerPattern> HALF_VERTICAL_RIGHT = BannerPatterns.of("half_vertical_right");
    final static public RegistryKey<BannerPattern> HALF_HORIZONTAL_BOTTOM = BannerPatterns.of("half_horizontal_bottom");
    final static public RegistryKey<BannerPattern> BORDER = BannerPatterns.of("border");
    final static public RegistryKey<BannerPattern> CURLY_BORDER = BannerPatterns.of("curly_border");
    final static public RegistryKey<BannerPattern> GRADIENT = BannerPatterns.of("gradient");
    final static public RegistryKey<BannerPattern> GRADIENT_UP = BannerPatterns.of("gradient_up");
    final static public RegistryKey<BannerPattern> BRICKS = BannerPatterns.of("bricks");
    final static public RegistryKey<BannerPattern> GLOBE = BannerPatterns.of("globe");
    final static public RegistryKey<BannerPattern> CREEPER = BannerPatterns.of("creeper");
    final static public RegistryKey<BannerPattern> SKULL = BannerPatterns.of("skull");
    final static public RegistryKey<BannerPattern> FLOWER = BannerPatterns.of("flower");
    final static public RegistryKey<BannerPattern> MOJANG = BannerPatterns.of("mojang");
    final static public RegistryKey<BannerPattern> PIGLIN = BannerPatterns.of("piglin");
    final static public RegistryKey<BannerPattern> FLOW = BannerPatterns.of("flow");
    final static public RegistryKey<BannerPattern> GUSTER = BannerPatterns.of("guster");

    private static RegistryKey<BannerPattern> of(String id) {
        return RegistryKey.of(RegistryKeys.BANNER_PATTERN, Identifier.ofVanilla(id));
    }

    public static void bootstrap(Registerable<BannerPattern> registry) {
        BannerPatterns.register(registry, BASE);
        BannerPatterns.register(registry, SQUARE_BOTTOM_LEFT);
        BannerPatterns.register(registry, SQUARE_BOTTOM_RIGHT);
        BannerPatterns.register(registry, SQUARE_TOP_LEFT);
        BannerPatterns.register(registry, SQUARE_TOP_RIGHT);
        BannerPatterns.register(registry, STRIPE_BOTTOM);
        BannerPatterns.register(registry, STRIPE_TOP);
        BannerPatterns.register(registry, STRIPE_LEFT);
        BannerPatterns.register(registry, STRIPE_RIGHT);
        BannerPatterns.register(registry, STRIPE_CENTER);
        BannerPatterns.register(registry, STRIPE_MIDDLE);
        BannerPatterns.register(registry, STRIPE_DOWNRIGHT);
        BannerPatterns.register(registry, STRIPE_DOWNLEFT);
        BannerPatterns.register(registry, SMALL_STRIPES);
        BannerPatterns.register(registry, CROSS);
        BannerPatterns.register(registry, STRAIGHT_CROSS);
        BannerPatterns.register(registry, TRIANGLE_BOTTOM);
        BannerPatterns.register(registry, TRIANGLE_TOP);
        BannerPatterns.register(registry, TRIANGLES_BOTTOM);
        BannerPatterns.register(registry, TRIANGLES_TOP);
        BannerPatterns.register(registry, DIAGONAL_LEFT);
        BannerPatterns.register(registry, DIAGONAL_UP_RIGHT);
        BannerPatterns.register(registry, DIAGONAL_UP_LEFT);
        BannerPatterns.register(registry, DIAGONAL_RIGHT);
        BannerPatterns.register(registry, CIRCLE);
        BannerPatterns.register(registry, RHOMBUS);
        BannerPatterns.register(registry, HALF_VERTICAL);
        BannerPatterns.register(registry, HALF_HORIZONTAL);
        BannerPatterns.register(registry, HALF_VERTICAL_RIGHT);
        BannerPatterns.register(registry, HALF_HORIZONTAL_BOTTOM);
        BannerPatterns.register(registry, BORDER);
        BannerPatterns.register(registry, GRADIENT);
        BannerPatterns.register(registry, GRADIENT_UP);
        BannerPatterns.register(registry, BRICKS);
        BannerPatterns.register(registry, CURLY_BORDER);
        BannerPatterns.register(registry, GLOBE);
        BannerPatterns.register(registry, CREEPER);
        BannerPatterns.register(registry, SKULL);
        BannerPatterns.register(registry, FLOWER);
        BannerPatterns.register(registry, MOJANG);
        BannerPatterns.register(registry, PIGLIN);
        BannerPatterns.register(registry, FLOW);
        BannerPatterns.register(registry, GUSTER);
    }

    public static void register(Registerable<BannerPattern> registry, RegistryKey<BannerPattern> key) {
        registry.register(key, new BannerPattern(key.getValue(), "block.minecraft.banner." + key.getValue().toShortTranslationKey()));
    }
}

