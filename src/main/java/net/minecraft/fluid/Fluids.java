/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.fluid;

import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Fluids {
    final static public Fluid EMPTY = Fluids.register("empty", new EmptyFluid());
    final static public FlowableFluid FLOWING_WATER = Fluids.register("flowing_water", new WaterFluid.Flowing());
    final static public FlowableFluid WATER = Fluids.register("water", new WaterFluid.Still());
    final static public FlowableFluid FLOWING_LAVA = Fluids.register("flowing_lava", new LavaFluid.Flowing());
    final static public FlowableFluid LAVA = Fluids.register("lava", new LavaFluid.Still());

    private static <T extends Fluid> T register(String id, T value) {
        return (T)Registry.register(Registries.FLUID, id, value);
    }

    static {
        for (Fluid fluid : Registries.FLUID) {
            for (FluidState fluidState : fluid.getStateManager().getStates()) {
                Fluid.STATE_IDS.add(fluidState);
            }
        }
    }
}

