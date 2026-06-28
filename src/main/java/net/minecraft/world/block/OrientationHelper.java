/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.block;

import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class OrientationHelper {
    @Nullable
    public static WireOrientation getEmissionOrientation(World world, @Nullable Direction up, @Nullable Direction front) {
        if (world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
            WireOrientation wireOrientation = WireOrientation.random(world.random).withSideBias(WireOrientation.SideBias.LEFT);
            if (front != null) {
                wireOrientation = wireOrientation.withUp(front);
            }
            if (up != null) {
                wireOrientation = wireOrientation.withFront(up);
            }
            return wireOrientation;
        }
        return null;
    }

    @Nullable
    public static WireOrientation withFrontNullable(@Nullable WireOrientation orientation, Direction direction) {
        return orientation == null ? null : orientation.withFront(direction);
    }
}

