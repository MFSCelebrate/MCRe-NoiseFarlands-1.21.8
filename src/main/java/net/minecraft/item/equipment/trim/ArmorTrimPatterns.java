/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.equipment.trim;

import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ArmorTrimPatterns {
    final static public RegistryKey<ArmorTrimPattern> SENTRY = ArmorTrimPatterns.of("sentry");
    final static public RegistryKey<ArmorTrimPattern> DUNE = ArmorTrimPatterns.of("dune");
    final static public RegistryKey<ArmorTrimPattern> COAST = ArmorTrimPatterns.of("coast");
    final static public RegistryKey<ArmorTrimPattern> WILD = ArmorTrimPatterns.of("wild");
    final static public RegistryKey<ArmorTrimPattern> WARD = ArmorTrimPatterns.of("ward");
    final static public RegistryKey<ArmorTrimPattern> EYE = ArmorTrimPatterns.of("eye");
    final static public RegistryKey<ArmorTrimPattern> VEX = ArmorTrimPatterns.of("vex");
    final static public RegistryKey<ArmorTrimPattern> TIDE = ArmorTrimPatterns.of("tide");
    final static public RegistryKey<ArmorTrimPattern> SNOUT = ArmorTrimPatterns.of("snout");
    final static public RegistryKey<ArmorTrimPattern> RIB = ArmorTrimPatterns.of("rib");
    final static public RegistryKey<ArmorTrimPattern> SPIRE = ArmorTrimPatterns.of("spire");
    final static public RegistryKey<ArmorTrimPattern> WAYFINDER = ArmorTrimPatterns.of("wayfinder");
    final static public RegistryKey<ArmorTrimPattern> SHAPER = ArmorTrimPatterns.of("shaper");
    final static public RegistryKey<ArmorTrimPattern> SILENCE = ArmorTrimPatterns.of("silence");
    final static public RegistryKey<ArmorTrimPattern> RAISER = ArmorTrimPatterns.of("raiser");
    final static public RegistryKey<ArmorTrimPattern> HOST = ArmorTrimPatterns.of("host");
    final static public RegistryKey<ArmorTrimPattern> FLOW = ArmorTrimPatterns.of("flow");
    final static public RegistryKey<ArmorTrimPattern> BOLT = ArmorTrimPatterns.of("bolt");

    public static void bootstrap(Registerable<ArmorTrimPattern> registry) {
        ArmorTrimPatterns.register(registry, SENTRY);
        ArmorTrimPatterns.register(registry, DUNE);
        ArmorTrimPatterns.register(registry, COAST);
        ArmorTrimPatterns.register(registry, WILD);
        ArmorTrimPatterns.register(registry, WARD);
        ArmorTrimPatterns.register(registry, EYE);
        ArmorTrimPatterns.register(registry, VEX);
        ArmorTrimPatterns.register(registry, TIDE);
        ArmorTrimPatterns.register(registry, SNOUT);
        ArmorTrimPatterns.register(registry, RIB);
        ArmorTrimPatterns.register(registry, SPIRE);
        ArmorTrimPatterns.register(registry, WAYFINDER);
        ArmorTrimPatterns.register(registry, SHAPER);
        ArmorTrimPatterns.register(registry, SILENCE);
        ArmorTrimPatterns.register(registry, RAISER);
        ArmorTrimPatterns.register(registry, HOST);
        ArmorTrimPatterns.register(registry, FLOW);
        ArmorTrimPatterns.register(registry, BOLT);
    }

    public static void register(Registerable<ArmorTrimPattern> registry, RegistryKey<ArmorTrimPattern> key) {
        ArmorTrimPattern armorTrimPattern = new ArmorTrimPattern(ArmorTrimPatterns.getId(key), Text.translatable(Util.createTranslationKey("trim_pattern", key.getValue())), false);
        registry.register(key, armorTrimPattern);
    }

    private static RegistryKey<ArmorTrimPattern> of(String id) {
        return RegistryKey.of(RegistryKeys.TRIM_PATTERN, Identifier.ofVanilla(id));
    }

    public static Identifier getId(RegistryKey<ArmorTrimPattern> key) {
        return key.getValue();
    }
}

