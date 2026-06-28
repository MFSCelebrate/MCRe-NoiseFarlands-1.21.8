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
import net.minecraft.client.util.ClosableFactory;

@Environment(value=EnvType.CLIENT)
public interface ObjectAllocator {
    final static public ObjectAllocator TRIVIAL = new ObjectAllocator(){

        @Override
        public <T> T acquire(ClosableFactory<T> factory) {
            T object = factory.create();
            factory.prepare(object);
            return object;
        }

        @Override
        public <T> void release(ClosableFactory<T> factory, T value) {
            factory.close(value);
        }
    };

    public <T> T acquire(ClosableFactory<T> var1);

    public <T> void release(ClosableFactory<T> var1, T var2);
}

