/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record WindowSettings(int width, int height, OptionalInt fullscreenWidth, OptionalInt fullscreenHeight, boolean fullscreen) {
    public WindowSettings withDimensions(int width, int height) {
        return new WindowSettings(width, height, this.fullscreenWidth, this.fullscreenHeight, this.fullscreen);
    }

    public WindowSettings withFullscreen(boolean fullscreen) {
        return new WindowSettings(this.width, this.height, this.fullscreenWidth, this.fullscreenHeight, fullscreen);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{WindowSettings.class, "width;height;fullscreenWidth;fullscreenHeight;isFullscreen", "width", "height", "fullscreenWidth", "fullscreenHeight", "fullscreen"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WindowSettings.class, "width;height;fullscreenWidth;fullscreenHeight;isFullscreen", "width", "height", "fullscreenWidth", "fullscreenHeight", "fullscreen"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WindowSettings.class, "width;height;fullscreenWidth;fullscreenHeight;isFullscreen", "width", "height", "fullscreenWidth", "fullscreenHeight", "fullscreen"}, this, object);
    }
}

