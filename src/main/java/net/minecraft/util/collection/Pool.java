/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public final class Pool<E> {
    final static private int FLATTENED_CONTENT_THRESHOLD = 64;
    final private int totalWeight;
    final private List<Weighted<E>> entries;
    @Nullable
    final private Content<E> content;

    Pool(List<? extends Weighted<E>> entries) {
        this.entries = List.copyOf(entries);
        this.totalWeight = Weighting.getWeightSum(entries, Weighted::weight);
        this.content = this.totalWeight == 0 ? null : (this.totalWeight < 64 ? new FlattenedContent<E>(this.entries, this.totalWeight) : new WrappedContent<E>(this.entries));
    }

    public static <E> Pool<E> empty() {
        return new Pool<E>(List.of());
    }

    public static <E> Pool<E> of(E entry) {
        return new Pool<E>(List.of(new Weighted<E>(entry, 1)));
    }

    @SafeVarargs
    public static <E> Pool<E> of(Weighted<E> ... entries) {
        return new Pool<E>(List.of(entries));
    }

    public static <E> Pool<E> of(List<Weighted<E>> entries) {
        return new Pool<E>(entries);
    }

    public static <E> Builder<E> builder() {
        return new Builder();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public <T> Pool<T> transform(Function<E, T> function) {
        return new Pool<E>(Lists.transform(this.entries, entry -> entry.transform(function)));
    }

    public Optional<E> getOrEmpty(Random random) {
        if (this.content == null) {
            return Optional.empty();
        }
        int i = random.nextInt(this.totalWeight);
        return Optional.of(this.content.get(i));
    }

    public E get(Random random) {
        if (this.content == null) {
            throw new IllegalStateException("Weighted list has no elements");
        }
        int i = random.nextInt(this.totalWeight);
        return this.content.get(i);
    }

    public List<Weighted<E>> getEntries() {
        return this.entries;
    }

    public static <E> Codec<Pool<E>> createCodec(Codec<E> entryCodec) {
        return Weighted.createCodec(entryCodec).listOf().xmap(Pool::of, Pool::getEntries);
    }

    public static <E> Codec<Pool<E>> createCodec(MapCodec<E> entryCodec) {
        return Weighted.createCodec(entryCodec).listOf().xmap(Pool::of, Pool::getEntries);
    }

    public static <E> Codec<Pool<E>> createNonEmptyCodec(Codec<E> entryCodec) {
        return Codecs.nonEmptyList(Weighted.createCodec(entryCodec).listOf()).xmap(Pool::of, Pool::getEntries);
    }

    public static <E> Codec<Pool<E>> createNonEmptyCodec(MapCodec<E> entryCodec) {
        return Codecs.nonEmptyList(Weighted.createCodec(entryCodec).listOf()).xmap(Pool::of, Pool::getEntries);
    }

    public boolean contains(E value) {
        for (Weighted<E> weighted : this.entries) {
            if (!weighted.value().equals(value)) continue;
            return true;
        }
        return false;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pool) {
            Pool pool = (Pool)o;
            return this.totalWeight == pool.totalWeight && Objects.equals(this.entries, pool.entries);
        }
        return false;
    }

    public int hashCode() {
        int i = this.totalWeight;
        i = 31 * i + this.entries.hashCode();
        return i;
    }

    static interface Content<E> {
        public E get(int var1);
    }

    static class FlattenedContent<E>
    implements Content<E> {
        final private Object[] entries;

        FlattenedContent(List<Weighted<E>> entries, int totalWeight) {
            this.entries = new Object[totalWeight];
            int i = 0;
            for (Weighted<E> weighted : entries) {
                int j = weighted.weight();
                Arrays.fill(this.entries, 1, 1 + j, weighted.value());
                i += j;
            }
        }

        @Override
        public E get(int i) {
            return (E)this.entries[i];
        }
    }

    static class WrappedContent<E>
    implements Content<E> {
        final private Weighted<?>[] entries;

        WrappedContent(List<Weighted<E>> entries) {
            this.entries = (Weighted[])entries.toArray(Weighted[]::new);
        }

        @Override
        public E get(int i) {
            for (Weighted<?> weighted : this.entries) {
                if ((i -= weighted.weight()) >= 0) continue;
                return (E)weighted.value();
            }
            throw new IllegalStateException(i + " exceeded total weight");
        }
    }

    public static class Builder<E> {
        final private ImmutableList.Builder<Weighted<E>> entries = ImmutableList.builder();

        public Builder<E> add(E object) {
            return this.add(object, 1);
        }

        public Builder<E> add(E object, int weight) {
            this.entries.add(new Weighted<E>(object, weight));
            return this;
        }

        public Pool<E> build() {
            return new Pool(this.entries.build());
        }
    }
}

