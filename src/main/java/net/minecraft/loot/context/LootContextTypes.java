/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextType;

public class LootContextTypes {
    final static private BiMap<Identifier, ContextType> MAP = HashBiMap.create();
    final static public Codec<ContextType> CODEC = Identifier.CODEC.comapFlatMap(id -> Optional.ofNullable((ContextType)MAP.get(id)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + String.valueOf(id) + "'")), arg_0 -> MAP.inverse().get(arg_0));
    final static public ContextType EMPTY = LootContextTypes.register("empty", builder -> {});
    final static public ContextType CHEST = LootContextTypes.register("chest", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY));
    final static public ContextType COMMAND = LootContextTypes.register("command", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY));
    final static public ContextType SELECTOR = LootContextTypes.register("selector", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY));
    final static public ContextType FISHING = LootContextTypes.register("fishing", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY));
    final static public ContextType ENTITY = LootContextTypes.register("entity", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.DAMAGE_SOURCE).allow(LootContextParameters.ATTACKING_ENTITY).allow(LootContextParameters.DIRECT_ATTACKING_ENTITY).allow(LootContextParameters.LAST_DAMAGE_PLAYER));
    final static public ContextType EQUIPMENT = LootContextTypes.register("equipment", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY));
    final static public ContextType ARCHAEOLOGY = LootContextTypes.register("archaeology", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.TOOL));
    final static public ContextType GIFT = LootContextTypes.register("gift", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY));
    final static public ContextType BARTER = LootContextTypes.register("barter", builder -> builder.require(LootContextParameters.THIS_ENTITY));
    final static public ContextType VAULT = LootContextTypes.register("vault", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.TOOL));
    final static public ContextType ADVANCEMENT_REWARD = LootContextTypes.register("advancement_reward", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN));
    final static public ContextType ADVANCEMENT_ENTITY = LootContextTypes.register("advancement_entity", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN));
    final static public ContextType ADVANCEMENT_LOCATION = LootContextTypes.register("advancement_location", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).require(LootContextParameters.BLOCK_STATE));
    final static public ContextType BLOCK_USE = LootContextTypes.register("block_use", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE));
    final static public ContextType GENERIC = LootContextTypes.register("generic", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.LAST_DAMAGE_PLAYER).require(LootContextParameters.DAMAGE_SOURCE).require(LootContextParameters.ATTACKING_ENTITY).require(LootContextParameters.DIRECT_ATTACKING_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE).require(LootContextParameters.BLOCK_ENTITY).require(LootContextParameters.TOOL).require(LootContextParameters.EXPLOSION_RADIUS));
    final static public ContextType BLOCK = LootContextTypes.register("block", builder -> builder.require(LootContextParameters.BLOCK_STATE).require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.BLOCK_ENTITY).allow(LootContextParameters.EXPLOSION_RADIUS));
    final static public ContextType SHEARING = LootContextTypes.register("shearing", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.TOOL));
    final static public ContextType ENCHANTED_DAMAGE = LootContextTypes.register("enchanted_damage", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN).require(LootContextParameters.DAMAGE_SOURCE).allow(LootContextParameters.DIRECT_ATTACKING_ENTITY).allow(LootContextParameters.ATTACKING_ENTITY));
    final static public ContextType ENCHANTED_ITEM = LootContextTypes.register("enchanted_item", builder -> builder.require(LootContextParameters.TOOL).require(LootContextParameters.ENCHANTMENT_LEVEL));
    final static public ContextType ENCHANTED_LOCATION = LootContextTypes.register("enchanted_location", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN).require(LootContextParameters.ENCHANTMENT_ACTIVE));
    final static public ContextType ENCHANTED_ENTITY = LootContextTypes.register("enchanted_entity", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN));
    final static public ContextType HIT_BLOCK = LootContextTypes.register("hit_block", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE));

    private static ContextType register(String name, Consumer<ContextType.Builder> type) {
        ContextType.Builder builder = new ContextType.Builder();
        type.accept(builder);
        ContextType contextType = builder.build();
        Identifier identifier = Identifier.ofVanilla(name);
        ContextType contextType2 = (ContextType)MAP.put((Object)identifier, (Object)contextType);
        if (contextType2 != null) {
            throw new IllegalStateException("Loot table parameter set " + String.valueOf(identifier) + " is already registered");
        }
        return contextType;
    }
}

