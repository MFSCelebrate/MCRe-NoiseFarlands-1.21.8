/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration.painting;

import java.util.Optional;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PaintingVariants {
    final static public RegistryKey<PaintingVariant> KEBAB = PaintingVariants.of("kebab");
    final static public RegistryKey<PaintingVariant> AZTEC = PaintingVariants.of("aztec");
    final static public RegistryKey<PaintingVariant> ALBAN = PaintingVariants.of("alban");
    final static public RegistryKey<PaintingVariant> AZTEC2 = PaintingVariants.of("aztec2");
    final static public RegistryKey<PaintingVariant> BOMB = PaintingVariants.of("bomb");
    final static public RegistryKey<PaintingVariant> PLANT = PaintingVariants.of("plant");
    final static public RegistryKey<PaintingVariant> WASTELAND = PaintingVariants.of("wasteland");
    final static public RegistryKey<PaintingVariant> POOL = PaintingVariants.of("pool");
    final static public RegistryKey<PaintingVariant> COURBET = PaintingVariants.of("courbet");
    final static public RegistryKey<PaintingVariant> SEA = PaintingVariants.of("sea");
    final static public RegistryKey<PaintingVariant> SUNSET = PaintingVariants.of("sunset");
    final static public RegistryKey<PaintingVariant> CREEBET = PaintingVariants.of("creebet");
    final static public RegistryKey<PaintingVariant> WANDERER = PaintingVariants.of("wanderer");
    final static public RegistryKey<PaintingVariant> GRAHAM = PaintingVariants.of("graham");
    final static public RegistryKey<PaintingVariant> MATCH = PaintingVariants.of("match");
    final static public RegistryKey<PaintingVariant> BUST = PaintingVariants.of("bust");
    final static public RegistryKey<PaintingVariant> STAGE = PaintingVariants.of("stage");
    final static public RegistryKey<PaintingVariant> VOID = PaintingVariants.of("void");
    final static public RegistryKey<PaintingVariant> SKULL_AND_ROSES = PaintingVariants.of("skull_and_roses");
    final static public RegistryKey<PaintingVariant> WITHER = PaintingVariants.of("wither");
    final static public RegistryKey<PaintingVariant> FIGHTERS = PaintingVariants.of("fighters");
    final static public RegistryKey<PaintingVariant> POINTER = PaintingVariants.of("pointer");
    final static public RegistryKey<PaintingVariant> PIGSCENE = PaintingVariants.of("pigscene");
    final static public RegistryKey<PaintingVariant> BURNING_SKULL = PaintingVariants.of("burning_skull");
    final static public RegistryKey<PaintingVariant> SKELETON = PaintingVariants.of("skeleton");
    final static public RegistryKey<PaintingVariant> DONKEY_KONG = PaintingVariants.of("donkey_kong");
    final static public RegistryKey<PaintingVariant> EARTH = PaintingVariants.of("earth");
    final static public RegistryKey<PaintingVariant> WIND = PaintingVariants.of("wind");
    final static public RegistryKey<PaintingVariant> WATER = PaintingVariants.of("water");
    final static public RegistryKey<PaintingVariant> FIRE = PaintingVariants.of("fire");
    final static public RegistryKey<PaintingVariant> BAROQUE = PaintingVariants.of("baroque");
    final static public RegistryKey<PaintingVariant> HUMBLE = PaintingVariants.of("humble");
    final static public RegistryKey<PaintingVariant> MEDITATIVE = PaintingVariants.of("meditative");
    final static public RegistryKey<PaintingVariant> PRAIRIE_RIDE = PaintingVariants.of("prairie_ride");
    final static public RegistryKey<PaintingVariant> UNPACKED = PaintingVariants.of("unpacked");
    final static public RegistryKey<PaintingVariant> BACKYARD = PaintingVariants.of("backyard");
    final static public RegistryKey<PaintingVariant> BOUQUET = PaintingVariants.of("bouquet");
    final static public RegistryKey<PaintingVariant> CAVEBIRD = PaintingVariants.of("cavebird");
    final static public RegistryKey<PaintingVariant> CHANGING = PaintingVariants.of("changing");
    final static public RegistryKey<PaintingVariant> COTAN = PaintingVariants.of("cotan");
    final static public RegistryKey<PaintingVariant> ENDBOSS = PaintingVariants.of("endboss");
    final static public RegistryKey<PaintingVariant> FERN = PaintingVariants.of("fern");
    final static public RegistryKey<PaintingVariant> FINDING = PaintingVariants.of("finding");
    final static public RegistryKey<PaintingVariant> LOWMIST = PaintingVariants.of("lowmist");
    final static public RegistryKey<PaintingVariant> ORB = PaintingVariants.of("orb");
    final static public RegistryKey<PaintingVariant> OWLEMONS = PaintingVariants.of("owlemons");
    final static public RegistryKey<PaintingVariant> PASSAGE = PaintingVariants.of("passage");
    final static public RegistryKey<PaintingVariant> POND = PaintingVariants.of("pond");
    final static public RegistryKey<PaintingVariant> SUNFLOWERS = PaintingVariants.of("sunflowers");
    final static public RegistryKey<PaintingVariant> TIDES = PaintingVariants.of("tides");
    final static public RegistryKey<PaintingVariant> DENNIS = PaintingVariants.of("dennis");

    public static void bootstrap(Registerable<PaintingVariant> registry) {
        PaintingVariants.register(registry, KEBAB, 1, 1);
        PaintingVariants.register(registry, AZTEC, 1, 1);
        PaintingVariants.register(registry, ALBAN, 1, 1);
        PaintingVariants.register(registry, AZTEC2, 1, 1);
        PaintingVariants.register(registry, BOMB, 1, 1);
        PaintingVariants.register(registry, PLANT, 1, 1);
        PaintingVariants.register(registry, WASTELAND, 1, 1);
        PaintingVariants.register(registry, POOL, 2, 1);
        PaintingVariants.register(registry, COURBET, 2, 1);
        PaintingVariants.register(registry, SEA, 2, 1);
        PaintingVariants.register(registry, SUNSET, 2, 1);
        PaintingVariants.register(registry, CREEBET, 2, 1);
        PaintingVariants.register(registry, WANDERER, 1, 2);
        PaintingVariants.register(registry, GRAHAM, 1, 2);
        PaintingVariants.register(registry, MATCH, 2, 2);
        PaintingVariants.register(registry, BUST, 2, 2);
        PaintingVariants.register(registry, STAGE, 2, 2);
        PaintingVariants.register(registry, VOID, 2, 2);
        PaintingVariants.register(registry, SKULL_AND_ROSES, 2, 2);
        PaintingVariants.register(registry, WITHER, 2, 2, false);
        PaintingVariants.register(registry, FIGHTERS, 4, 2);
        PaintingVariants.register(registry, POINTER, 4, 4);
        PaintingVariants.register(registry, PIGSCENE, 4, 4);
        PaintingVariants.register(registry, BURNING_SKULL, 4, 4);
        PaintingVariants.register(registry, SKELETON, 4, 3);
        PaintingVariants.register(registry, EARTH, 2, 2, false);
        PaintingVariants.register(registry, WIND, 2, 2, false);
        PaintingVariants.register(registry, WATER, 2, 2, false);
        PaintingVariants.register(registry, FIRE, 2, 2, false);
        PaintingVariants.register(registry, DONKEY_KONG, 4, 3);
        PaintingVariants.register(registry, BAROQUE, 2, 2);
        PaintingVariants.register(registry, HUMBLE, 2, 2);
        PaintingVariants.register(registry, MEDITATIVE, 1, 1);
        PaintingVariants.register(registry, PRAIRIE_RIDE, 1, 2);
        PaintingVariants.register(registry, UNPACKED, 4, 4);
        PaintingVariants.register(registry, BACKYARD, 3, 4);
        PaintingVariants.register(registry, BOUQUET, 3, 3);
        PaintingVariants.register(registry, CAVEBIRD, 3, 3);
        PaintingVariants.register(registry, CHANGING, 4, 2);
        PaintingVariants.register(registry, COTAN, 3, 3);
        PaintingVariants.register(registry, ENDBOSS, 3, 3);
        PaintingVariants.register(registry, FERN, 3, 3);
        PaintingVariants.register(registry, FINDING, 4, 2);
        PaintingVariants.register(registry, LOWMIST, 4, 2);
        PaintingVariants.register(registry, ORB, 4, 4);
        PaintingVariants.register(registry, OWLEMONS, 3, 3);
        PaintingVariants.register(registry, PASSAGE, 4, 2);
        PaintingVariants.register(registry, POND, 3, 4);
        PaintingVariants.register(registry, SUNFLOWERS, 3, 3);
        PaintingVariants.register(registry, TIDES, 3, 3);
        PaintingVariants.register(registry, DENNIS, 3, 3);
    }

    private static void register(Registerable<PaintingVariant> registry, RegistryKey<PaintingVariant> key, int width, int height) {
        PaintingVariants.register(registry, key, width, height, true);
    }

    private static void register(Registerable<PaintingVariant> registry, RegistryKey<PaintingVariant> key, int width, int height, boolean hasAuthor) {
        registry.register(key, new PaintingVariant(width, height, key.getValue(), Optional.of(Text.translatable(key.getValue().toTranslationKey("painting", "title")).formatted(Formatting.YELLOW)), hasAuthor ? Optional.of(Text.translatable(key.getValue().toTranslationKey("painting", "author")).formatted(Formatting.GRAY)) : Optional.empty()));
    }

    private static RegistryKey<PaintingVariant> of(String id) {
        return RegistryKey.of(RegistryKeys.PAINTING_VARIANT, Identifier.ofVanilla(id));
    }
}

