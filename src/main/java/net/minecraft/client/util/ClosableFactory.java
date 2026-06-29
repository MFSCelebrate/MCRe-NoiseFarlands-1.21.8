/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface ClosableFactory<T> {
    public T create();

    default public void prepare(T value) {
    }

    public void close(T var1);

    default public boolean equals(ClosableFactory<?> factory) {
        return this.equals((Object)factory);
    }
}

