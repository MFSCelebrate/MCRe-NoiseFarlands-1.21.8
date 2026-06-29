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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record BannerResultGuiElementRenderState(ModelPart flag, DyeColor baseColor, BannerPatternsComponent resultBannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    public BannerResultGuiElementRenderState(ModelPart flag, DyeColor color, BannerPatternsComponent bannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
        this(flag, color, bannerPatterns, x1, y1, x2, y2, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
    }

    @Override
    public float scale() {
        return 16.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BannerResultGuiElementRenderState.class, "flag;baseColor;resultBannerPatterns;x0;y0;x1;y1;scissorArea;bounds", "flag", "baseColor", "resultBannerPatterns", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BannerResultGuiElementRenderState.class, "flag;baseColor;resultBannerPatterns;x0;y0;x1;y1;scissorArea;bounds", "flag", "baseColor", "resultBannerPatterns", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BannerResultGuiElementRenderState.class, "flag;baseColor;resultBannerPatterns;x0;y0;x1;y1;scissorArea;bounds", "flag", "baseColor", "resultBannerPatterns", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this, object);
    }
}

