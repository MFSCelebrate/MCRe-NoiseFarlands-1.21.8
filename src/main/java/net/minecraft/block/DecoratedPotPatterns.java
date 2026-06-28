/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DecoratedPotPatterns {
    final static public RegistryKey<DecoratedPotPattern> BLANK = DecoratedPotPatterns.of("blank");
    final static public RegistryKey<DecoratedPotPattern> ANGLER = DecoratedPotPatterns.of("angler");
    final static public RegistryKey<DecoratedPotPattern> ARCHER = DecoratedPotPatterns.of("archer");
    final static public RegistryKey<DecoratedPotPattern> ARMS_UP = DecoratedPotPatterns.of("arms_up");
    final static public RegistryKey<DecoratedPotPattern> BLADE = DecoratedPotPatterns.of("blade");
    final static public RegistryKey<DecoratedPotPattern> BREWER = DecoratedPotPatterns.of("brewer");
    final static public RegistryKey<DecoratedPotPattern> BURN = DecoratedPotPatterns.of("burn");
    final static public RegistryKey<DecoratedPotPattern> DANGER = DecoratedPotPatterns.of("danger");
    final static public RegistryKey<DecoratedPotPattern> EXPLORER = DecoratedPotPatterns.of("explorer");
    final static public RegistryKey<DecoratedPotPattern> FLOW = DecoratedPotPatterns.of("flow");
    final static public RegistryKey<DecoratedPotPattern> FRIEND = DecoratedPotPatterns.of("friend");
    final static public RegistryKey<DecoratedPotPattern> GUSTER = DecoratedPotPatterns.of("guster");
    final static public RegistryKey<DecoratedPotPattern> HEART = DecoratedPotPatterns.of("heart");
    final static public RegistryKey<DecoratedPotPattern> HEARTBREAK = DecoratedPotPatterns.of("heartbreak");
    final static public RegistryKey<DecoratedPotPattern> HOWL = DecoratedPotPatterns.of("howl");
    final static public RegistryKey<DecoratedPotPattern> MINER = DecoratedPotPatterns.of("miner");
    final static public RegistryKey<DecoratedPotPattern> MOURNER = DecoratedPotPatterns.of("mourner");
    final static public RegistryKey<DecoratedPotPattern> PLENTY = DecoratedPotPatterns.of("plenty");
    final static public RegistryKey<DecoratedPotPattern> PRIZE = DecoratedPotPatterns.of("prize");
    final static public RegistryKey<DecoratedPotPattern> SCRAPE = DecoratedPotPatterns.of("scrape");
    final static public RegistryKey<DecoratedPotPattern> SHEAF = DecoratedPotPatterns.of("sheaf");
    final static public RegistryKey<DecoratedPotPattern> SHELTER = DecoratedPotPatterns.of("shelter");
    final static public RegistryKey<DecoratedPotPattern> SKULL = DecoratedPotPatterns.of("skull");
    final static public RegistryKey<DecoratedPotPattern> SNORT = DecoratedPotPatterns.of("snort");
    final static private Map<Item, RegistryKey<DecoratedPotPattern>> SHERD_TO_PATTERN = Map.ofEntries(Map.entry(Items.BRICK, BLANK), Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER), Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER), Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP), Map.entry(Items.BLADE_POTTERY_SHERD, BLADE), Map.entry(Items.BREWER_POTTERY_SHERD, BREWER), Map.entry(Items.BURN_POTTERY_SHERD, BURN), Map.entry(Items.DANGER_POTTERY_SHERD, DANGER), Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER), Map.entry(Items.FLOW_POTTERY_SHERD, FLOW), Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND), Map.entry(Items.GUSTER_POTTERY_SHERD, GUSTER), Map.entry(Items.HEART_POTTERY_SHERD, HEART), Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK), Map.entry(Items.HOWL_POTTERY_SHERD, HOWL), Map.entry(Items.MINER_POTTERY_SHERD, MINER), Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER), Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY), Map.entry(Items.PRIZE_POTTERY_SHERD, PRIZE), Map.entry(Items.SCRAPE_POTTERY_SHERD, SCRAPE), Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF), Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER), Map.entry(Items.SKULL_POTTERY_SHERD, SKULL), Map.entry(Items.SNORT_POTTERY_SHERD, SNORT));

    @Nullable
    public static RegistryKey<DecoratedPotPattern> fromSherd(Item sherd) {
        return SHERD_TO_PATTERN.get(sherd);
    }

    private static RegistryKey<DecoratedPotPattern> of(String path) {
        return RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, Identifier.ofVanilla(path));
    }

    public static DecoratedPotPattern registerAndGetDefault(Registry<DecoratedPotPattern> registry) {
        DecoratedPotPatterns.register(registry, ANGLER, "angler_pottery_pattern");
        DecoratedPotPatterns.register(registry, ARCHER, "archer_pottery_pattern");
        DecoratedPotPatterns.register(registry, ARMS_UP, "arms_up_pottery_pattern");
        DecoratedPotPatterns.register(registry, BLADE, "blade_pottery_pattern");
        DecoratedPotPatterns.register(registry, BREWER, "brewer_pottery_pattern");
        DecoratedPotPatterns.register(registry, BURN, "burn_pottery_pattern");
        DecoratedPotPatterns.register(registry, DANGER, "danger_pottery_pattern");
        DecoratedPotPatterns.register(registry, EXPLORER, "explorer_pottery_pattern");
        DecoratedPotPatterns.register(registry, FLOW, "flow_pottery_pattern");
        DecoratedPotPatterns.register(registry, FRIEND, "friend_pottery_pattern");
        DecoratedPotPatterns.register(registry, GUSTER, "guster_pottery_pattern");
        DecoratedPotPatterns.register(registry, HEART, "heart_pottery_pattern");
        DecoratedPotPatterns.register(registry, HEARTBREAK, "heartbreak_pottery_pattern");
        DecoratedPotPatterns.register(registry, HOWL, "howl_pottery_pattern");
        DecoratedPotPatterns.register(registry, MINER, "miner_pottery_pattern");
        DecoratedPotPatterns.register(registry, MOURNER, "mourner_pottery_pattern");
        DecoratedPotPatterns.register(registry, PLENTY, "plenty_pottery_pattern");
        DecoratedPotPatterns.register(registry, PRIZE, "prize_pottery_pattern");
        DecoratedPotPatterns.register(registry, SCRAPE, "scrape_pottery_pattern");
        DecoratedPotPatterns.register(registry, SHEAF, "sheaf_pottery_pattern");
        DecoratedPotPatterns.register(registry, SHELTER, "shelter_pottery_pattern");
        DecoratedPotPatterns.register(registry, SKULL, "skull_pottery_pattern");
        DecoratedPotPatterns.register(registry, SNORT, "snort_pottery_pattern");
        return DecoratedPotPatterns.register(registry, BLANK, "decorated_pot_side");
    }

    private static DecoratedPotPattern register(Registry<DecoratedPotPattern> registry, RegistryKey<DecoratedPotPattern> key, String id) {
        return Registry.register(registry, key, new DecoratedPotPattern(Identifier.ofVanilla(id)));
    }
}

