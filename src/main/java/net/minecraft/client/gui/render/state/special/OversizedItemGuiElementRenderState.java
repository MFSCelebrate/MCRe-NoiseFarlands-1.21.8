/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(value=EnvType.CLIENT)
public record OversizedItemGuiElementRenderState(ItemGuiElementRenderState guiItemRenderState, int x1, int y1, int x2, int y2) implements SpecialGuiElementRenderState
{
    @Override
    public float scale() {
        return 16.0f;
    }

    @Override
    public Matrix3x2f pose() {
        return this.guiItemRenderState.pose();
    }

    @Override
    @Nullable
    public ScreenRect scissorArea() {
        return this.guiItemRenderState.scissorArea();
    }

    @Override
    @Nullable
    public ScreenRect bounds() {
        return this.guiItemRenderState.bounds();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OversizedItemGuiElementRenderState.class, "guiItemRenderState;x0;y0;x1;y1", "guiItemRenderState", "x1", "y1", "x2", "y2"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OversizedItemGuiElementRenderState.class, "guiItemRenderState;x0;y0;x1;y1", "guiItemRenderState", "x1", "y1", "x2", "y2"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OversizedItemGuiElementRenderState.class, "guiItemRenderState;x0;y0;x1;y1", "guiItemRenderState", "x1", "y1", "x2", "y2"}, this, object);
    }
}

