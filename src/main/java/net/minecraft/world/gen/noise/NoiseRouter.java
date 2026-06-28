/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.noise;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
    final static public Codec<NoiseRouter> CODEC = RecordCodecBuilder.create(instance -> instance.group(NoiseRouter.field("barrier", NoiseRouter::barrierNoise), NoiseRouter.field("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise), NoiseRouter.field("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise), NoiseRouter.field("lava", NoiseRouter::lavaNoise), NoiseRouter.field("temperature", NoiseRouter::temperature), NoiseRouter.field("vegetation", NoiseRouter::vegetation), NoiseRouter.field("continents", NoiseRouter::continents), NoiseRouter.field("erosion", NoiseRouter::erosion), NoiseRouter.field("depth", NoiseRouter::depth), NoiseRouter.field("ridges", NoiseRouter::ridges), NoiseRouter.field("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness), NoiseRouter.field("final_density", NoiseRouter::finalDensity), NoiseRouter.field("vein_toggle", NoiseRouter::veinToggle), NoiseRouter.field("vein_ridged", NoiseRouter::veinRidged), NoiseRouter.field("vein_gap", NoiseRouter::veinGap)).apply((Applicative)instance, NoiseRouter::new));

    private static RecordCodecBuilder<NoiseRouter, DensityFunction> field(String name, Function<NoiseRouter, DensityFunction> getter) {
        return DensityFunction.FUNCTION_CODEC.fieldOf(name).forGetter(getter);
    }

    public NoiseRouter apply(DensityFunction.DensityFunctionVisitor visitor) {
        return new NoiseRouter(this.barrierNoise.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.fluidLevelFloodednessNoise.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.fluidLevelSpreadNoise.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.lavaNoise.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.temperature.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.vegetation.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.continents.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.erosion.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.depth.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.ridges.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.initialDensityWithoutJaggedness.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.finalDensity.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.veinToggle.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.veinRidged.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor), this.veinGap.net_minecraft_world_gen_densityfunction_DensityFunction_apply(visitor));
    }
}

