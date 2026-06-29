/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class OceanRuinStructure
extends Structure {
    final static public MapCodec<OceanRuinStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(OceanRuinStructure.configCodecBuilder(instance), (App)BiomeTemperature.CODEC.fieldOf("biome_temp").forGetter(structure -> structure.biomeTemperature), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("large_probability").forGetter(structure -> Float.valueOf(structure.largeProbability)), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("cluster_probability").forGetter(structure -> Float.valueOf(structure.clusterProbability))).apply((Applicative)instance, OceanRuinStructure::new));
    final public BiomeTemperature biomeTemperature;
    final public float largeProbability;
    final public float clusterProbability;

    public OceanRuinStructure(Structure.Config config, BiomeTemperature biomeTemperature, float largeProbability, float clusterProbability) {
        super(config);
        this.biomeTemperature = biomeTemperature;
        this.largeProbability = largeProbability;
        this.clusterProbability = clusterProbability;
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        return OceanRuinStructure.getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, collector -> this.addPieces((StructurePiecesCollector)collector, context));
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(context.random());
        OceanRuinGenerator.addPieces(context.structureTemplateManager(), blockPos, blockRotation, collector, context.random(), this);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.OCEAN_RUIN;
    }

    public static final class BiomeTemperature
    extends Enum<BiomeTemperature>
    implements StringIdentifiable {
        final static public BiomeTemperature WARM = new BiomeTemperature("warm");
        final static public BiomeTemperature COLD = new BiomeTemperature("cold");
        final static public Codec<BiomeTemperature> CODEC;
        @Deprecated
        final static public Codec<BiomeTemperature> ENUM_NAME_CODEC;
        final private String name;
        final static private BiomeTemperature[] field_14531;

        public static BiomeTemperature[] values() {
            return (BiomeTemperature[])field_14531.clone();
        }

        public static BiomeTemperature valueOf(String string) {
            return Enum.valueOf(BiomeTemperature.class, string);
        }

        private BiomeTemperature(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static BiomeTemperature[] method_36760() {
            return new BiomeTemperature[]{WARM, COLD};
        }

        static {
            field_14531 = BiomeTemperature.method_36760();
            CODEC = StringIdentifiable.createCodec(BiomeTemperature::values);
            ENUM_NAME_CODEC = Codecs.enumByName(BiomeTemperature::valueOf);
        }
    }
}

