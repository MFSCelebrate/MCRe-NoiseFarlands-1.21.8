/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;

public class TypeReferences {
    final static public DSL.TypeReference LEVEL = TypeReferences.create("level");
    final static public DSL.TypeReference LIGHTWEIGHT_LEVEL = TypeReferences.create("lightweight_level");
    final static public DSL.TypeReference PLAYER = TypeReferences.create("player");
    final static public DSL.TypeReference CHUNK = TypeReferences.create("chunk");
    final static public DSL.TypeReference HOTBAR = TypeReferences.create("hotbar");
    final static public DSL.TypeReference OPTIONS = TypeReferences.create("options");
    final static public DSL.TypeReference STRUCTURE = TypeReferences.create("structure");
    final static public DSL.TypeReference STATS = TypeReferences.create("stats");
    final static public DSL.TypeReference SAVED_DATA_COMMAND_STORAGE = TypeReferences.create("saved_data/command_storage");
    final static public DSL.TypeReference TICKETS_SAVED_DATA = TypeReferences.create("saved_data/tickets");
    final static public DSL.TypeReference SAVED_DATA_MAP_DATA = TypeReferences.create("saved_data/map_data");
    final static public DSL.TypeReference SAVED_DATA_IDCOUNTS = TypeReferences.create("saved_data/idcounts");
    final static public DSL.TypeReference SAVED_DATA_RAIDS = TypeReferences.create("saved_data/raids");
    final static public DSL.TypeReference SAVED_DATA_RANDOM_SEQUENCES = TypeReferences.create("saved_data/random_sequences");
    final static public DSL.TypeReference SAVED_DATA_STRUCTURE_FEATURE_INDICES = TypeReferences.create("saved_data/structure_feature_indices");
    final static public DSL.TypeReference SAVED_DATA_SCOREBOARD = TypeReferences.create("saved_data/scoreboard");
    final static public DSL.TypeReference ADVANCEMENTS = TypeReferences.create("advancements");
    final static public DSL.TypeReference POI_CHUNK = TypeReferences.create("poi_chunk");
    final static public DSL.TypeReference ENTITY_CHUNK = TypeReferences.create("entity_chunk");
    final static public DSL.TypeReference BLOCK_ENTITY = TypeReferences.create("block_entity");
    final static public DSL.TypeReference ITEM_STACK = TypeReferences.create("item_stack");
    final static public DSL.TypeReference BLOCK_STATE = TypeReferences.create("block_state");
    final static public DSL.TypeReference FLAT_BLOCK_STATE = TypeReferences.create("flat_block_state");
    final static public DSL.TypeReference DATA_COMPONENTS = TypeReferences.create("data_components");
    final static public DSL.TypeReference VILLAGER_TRADE = TypeReferences.create("villager_trade");
    final static public DSL.TypeReference PARTICLE = TypeReferences.create("particle");
    final static public DSL.TypeReference TEXT_COMPONENT = TypeReferences.create("text_component");
    final static public DSL.TypeReference ENTITY_EQUIPMENT = TypeReferences.create("entity_equipment");
    final static public DSL.TypeReference ENTITY_NAME = TypeReferences.create("entity_name");
    final static public DSL.TypeReference ENTITY_TREE = TypeReferences.create("entity_tree");
    final static public DSL.TypeReference ENTITY = TypeReferences.create("entity");
    final static public DSL.TypeReference BLOCK_NAME = TypeReferences.create("block_name");
    final static public DSL.TypeReference ITEM_NAME = TypeReferences.create("item_name");
    final static public DSL.TypeReference GAME_EVENT_NAME = TypeReferences.create("game_event_name");
    final static public DSL.TypeReference UNTAGGED_SPAWNER = TypeReferences.create("untagged_spawner");
    final static public DSL.TypeReference STRUCTURE_FEATURE = TypeReferences.create("structure_feature");
    final static public DSL.TypeReference OBJECTIVE = TypeReferences.create("objective");
    final static public DSL.TypeReference TEAM = TypeReferences.create("team");
    final static public DSL.TypeReference RECIPE = TypeReferences.create("recipe");
    final static public DSL.TypeReference BIOME = TypeReferences.create("biome");
    final static public DSL.TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = TypeReferences.create("multi_noise_biome_source_parameter_list");
    final static public DSL.TypeReference WORLD_GEN_SETTINGS = TypeReferences.create("world_gen_settings");

    public static DSL.TypeReference create(final String typeName) {
        return new DSL.TypeReference(){

            public String typeName() {
                return typeName;
            }

            public String toString() {
                return "@" + typeName;
            }
        };
    }
}

