/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.util.profiler.ProfilerTiming;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ProfilerChartGuiElementRenderState(List<ProfilerTiming> chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    public ProfilerChartGuiElementRenderState(List<ProfilerTiming> chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
        this(chartData, x1, y1, x2, y2, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
    }

    @Override
    public float scale() {
        return 1.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProfilerChartGuiElementRenderState.class, "chartData;x0;y0;x1;y1;scissorArea;bounds", "chartData", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProfilerChartGuiElementRenderState.class, "chartData;x0;y0;x1;y1;scissorArea;bounds", "chartData", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProfilerChartGuiElementRenderState.class, "chartData;x0;y0;x1;y1;scissorArea;bounds", "chartData", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this, object);
    }
}

