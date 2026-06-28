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
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record PlayerSkinGuiElementRenderState(PlayerEntityModel playerModel, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    public PlayerSkinGuiElementRenderState(PlayerEntityModel model, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
        this(model, texture, xRotation, yRotation, yPivot, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerSkinGuiElementRenderState.class, "playerModel;texture;rotationX;rotationY;pivotY;x0;y0;x1;y1;scale;scissorArea;bounds", "playerModel", "texture", "xRotation", "yRotation", "yPivot", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerSkinGuiElementRenderState.class, "playerModel;texture;rotationX;rotationY;pivotY;x0;y0;x1;y1;scale;scissorArea;bounds", "playerModel", "texture", "xRotation", "yRotation", "yPivot", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerSkinGuiElementRenderState.class, "playerModel;texture;rotationX;rotationY;pivotY;x0;y0;x1;y1;scale;scissorArea;bounds", "playerModel", "texture", "xRotation", "yRotation", "yPivot", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this, object);
    }
}

