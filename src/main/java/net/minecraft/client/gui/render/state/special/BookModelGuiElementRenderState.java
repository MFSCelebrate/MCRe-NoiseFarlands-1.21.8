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
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record BookModelGuiElementRenderState(BookModel bookModel, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    public BookModelGuiElementRenderState(BookModel model, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
        this(model, texture, open, flip, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BookModelGuiElementRenderState.class, "bookModel;texture;open;flip;x0;y0;x1;y1;scale;scissorArea;bounds", "bookModel", "texture", "open", "flip", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BookModelGuiElementRenderState.class, "bookModel;texture;open;flip;x0;y0;x1;y1;scale;scissorArea;bounds", "bookModel", "texture", "open", "flip", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BookModelGuiElementRenderState.class, "bookModel;texture;open;flip;x0;y0;x1;y1;scale;scissorArea;bounds", "bookModel", "texture", "open", "flip", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this, object);
    }
}

