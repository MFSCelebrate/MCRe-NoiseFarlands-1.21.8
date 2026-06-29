/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.component;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentType;
import org.jetbrains.annotations.Nullable;

public interface ComponentsAccess {
    @Nullable
    public <T> T get(ComponentType<? extends T> var1);

    default public <T> T getOrDefault(ComponentType<? extends T> type, T fallback) {
        T object = this.get(type);
        return object != null ? object : fallback;
    }

    @Nullable
    default public <T> Component<T> getTyped(ComponentType<T> type) {
        T object = this.get(type);
        return object != null ? new Component<T>(type, object) : null;
    }
}

