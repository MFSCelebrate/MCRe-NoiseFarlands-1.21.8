/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;

@Environment(value=EnvType.CLIENT)
public class Pool
implements ObjectAllocator,
AutoCloseable {
    final private int lifespan;
    final private Deque<Entry<?>> entries = new ArrayDeque();

    public Pool(int lifespan) {
        this.lifespan = lifespan;
    }

    public void decrementLifespan() {
        Iterator<Entry<?>> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            Entry<?> entry = iterator.next();
            if (entry.lifespan-- != 0) continue;
            entry.close();
            iterator.remove();
        }
    }

    @Override
    public <T> T acquire(ClosableFactory<T> factory) {
        T object = this.acquireUnprepared(factory);
        factory.prepare(object);
        return object;
    }

    private <T> T acquireUnprepared(ClosableFactory<T> factory) {
        Iterator<Entry<?>> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            Entry<?> entry = iterator.next();
            if (!factory.equals(entry.factory)) continue;
            iterator.remove();
            return entry.object;
        }
        return factory.create();
    }

    @Override
    public <T> void release(ClosableFactory<T> factory, T value) {
        this.entries.addFirst(new Entry<T>(factory, value, this.lifespan));
    }

    public void clear() {
        this.entries.forEach(Entry::close);
        this.entries.clear();
    }

    @Override
    public void close() {
        this.clear();
    }

    @VisibleForTesting
    protected Collection<Entry<?>> getEntries() {
        return this.entries;
    }

    @Environment(value=EnvType.CLIENT)
    @VisibleForTesting
    protected static final class Entry<T>
    implements AutoCloseable {
        final ClosableFactory<T> factory;
        final T object;
        int lifespan;

        Entry(ClosableFactory<T> factory, T object, int lifespan) {
            this.factory = factory;
            this.object = object;
            this.lifespan = lifespan;
        }

        @Override
        public void close() {
            this.factory.close(this.object);
        }
    }
}

