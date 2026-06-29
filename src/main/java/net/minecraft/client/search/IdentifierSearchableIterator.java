/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.PeekingIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class IdentifierSearchableIterator<T>
extends AbstractIterator<T> {
    final private PeekingIterator<T> namespacesIterator;
    final private PeekingIterator<T> pathsIterator;
    final private Comparator<T> lastIndexComparator;

    public IdentifierSearchableIterator(Iterator<T> namespacesIterator, Iterator<T> pathsIterator, Comparator<T> lastIndexComparator) {
        this.namespacesIterator = Iterators.peekingIterator(namespacesIterator);
        this.pathsIterator = Iterators.peekingIterator(pathsIterator);
        this.lastIndexComparator = lastIndexComparator;
    }

    protected T computeNext() {
        while (this.namespacesIterator.hasNext() && this.pathsIterator.hasNext()) {
            int i = this.lastIndexComparator.compare(this.namespacesIterator.peek(), this.pathsIterator.peek());
            if (i == 0) {
                this.pathsIterator.next();
                return (T)this.namespacesIterator.next();
            }
            if (i < 0) {
                this.namespacesIterator.next();
                continue;
            }
            this.pathsIterator.next();
        }
        return (T)this.endOfData();
    }
}

