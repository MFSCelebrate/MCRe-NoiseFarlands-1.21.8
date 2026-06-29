/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RecipePropertySet {
    final static public RegistryKey<? extends Registry<RecipePropertySet>> REGISTRY = RegistryKey.ofRegistry(Identifier.ofVanilla("recipe_property_set"));
    final static public RegistryKey<RecipePropertySet> SMITHING_BASE = RecipePropertySet.register("smithing_base");
    final static public RegistryKey<RecipePropertySet> SMITHING_TEMPLATE = RecipePropertySet.register("smithing_template");
    final static public RegistryKey<RecipePropertySet> SMITHING_ADDITION = RecipePropertySet.register("smithing_addition");
    final static public RegistryKey<RecipePropertySet> FURNACE_INPUT = RecipePropertySet.register("furnace_input");
    final static public RegistryKey<RecipePropertySet> BLAST_FURNACE_INPUT = RecipePropertySet.register("blast_furnace_input");
    final static public RegistryKey<RecipePropertySet> SMOKER_INPUT = RecipePropertySet.register("smoker_input");
    final static public RegistryKey<RecipePropertySet> CAMPFIRE_INPUT = RecipePropertySet.register("campfire_input");
    final static public PacketCodec<RegistryByteBuf, RecipePropertySet> PACKET_CODEC = Item.ENTRY_PACKET_CODEC.collect(PacketCodecs.toList()).xmap(items -> new RecipePropertySet(Set.copyOf(items)), set -> List.copyOf(set.usableItems));
    final static public RecipePropertySet EMPTY = new RecipePropertySet(Set.of());
    final private Set<RegistryEntry<Item>> usableItems;

    private RecipePropertySet(Set<RegistryEntry<Item>> usableItems) {
        this.usableItems = usableItems;
    }

    private static RegistryKey<RecipePropertySet> register(String id) {
        return RegistryKey.of(REGISTRY, Identifier.ofVanilla(id));
    }

    public boolean canUse(ItemStack stack) {
        return this.usableItems.contains(stack.getRegistryEntry());
    }

    static RecipePropertySet of(Collection<Ingredient> ingredients) {
        Set<RegistryEntry<Item>> set = ingredients.stream().flatMap(Ingredient::getMatchingItems).collect(Collectors.toUnmodifiableSet());
        return new RecipePropertySet(set);
    }
}

