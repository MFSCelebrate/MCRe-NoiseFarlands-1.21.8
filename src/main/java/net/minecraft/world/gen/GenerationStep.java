/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public class GenerationStep {

    public static final class Feature
    extends Enum<Feature>
    implements StringIdentifiable {
        final static public Feature RAW_GENERATION = new Feature("raw_generation");
        final static public Feature LAKES = new Feature("lakes");
        final static public Feature LOCAL_MODIFICATIONS = new Feature("local_modifications");
        final static public Feature UNDERGROUND_STRUCTURES = new Feature("underground_structures");
        final static public Feature SURFACE_STRUCTURES = new Feature("surface_structures");
        final static public Feature STRONGHOLDS = new Feature("strongholds");
        final static public Feature UNDERGROUND_ORES = new Feature("underground_ores");
        final static public Feature UNDERGROUND_DECORATION = new Feature("underground_decoration");
        final static public Feature FLUID_SPRINGS = new Feature("fluid_springs");
        final static public Feature VEGETAL_DECORATION = new Feature("vegetal_decoration");
        final static public Feature TOP_LAYER_MODIFICATION = new Feature("top_layer_modification");
        final static public Codec<Feature> CODEC;
        final private String name;
        final static private Feature[] field_13181;

        public static Feature[] values() {
            return (Feature[])field_13181.clone();
        }

        public static Feature valueOf(String string) {
            return Enum.valueOf(Feature.class, string);
        }

        private Feature(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Feature[] method_36751() {
            return new Feature[]{RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, FLUID_SPRINGS, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION};
        }

        static {
            field_13181 = Feature.method_36751();
            CODEC = StringIdentifiable.createCodec(Feature::values);
        }
    }
}

