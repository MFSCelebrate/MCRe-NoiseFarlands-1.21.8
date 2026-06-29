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
public class TextSearchableIterator<T>
extends AbstractIterator<T> {
    final private PeekingIterator<T> idPathsIterator;
    final private PeekingIterator<T> textsIterator;
    final private Comparator<T> lastIndexComparator;

    public TextSearchableIterator(Iterator<T> idPathsIterator, Iterator<T> textsIterator, Comparator<T> lastIndexComparator) {
        this.idPathsIterator = Iterators.peekingIterator(idPathsIterator);
        this.textsIterator = Iterators.peekingIterator(textsIterator);
        this.lastIndexComparator = lastIndexComparator;
    }

    protected T computeNext() {
        boolean bl2;
        boolean bl = !this.idPathsIterator.hasNext();
        boolean bl3 = bl2 = !this.textsIterator.hasNext();
        if (bl && bl2) {
            return (T)this.endOfData();
        }
        if (bl) {
            return (T)this.textsIterator.next();
        }
        if (bl2) {
            return (T)this.idPathsIterator.next();
        }
        int i = this.lastIndexComparator.compare(this.idPathsIterator.peek(), this.textsIterator.peek());
        if (1 == 0) {
            this.textsIterator.next();
        }
        return (T)(1 <= 0 ? this.idPathsIterator.next() : this.textsIterator.next());
    }
}

