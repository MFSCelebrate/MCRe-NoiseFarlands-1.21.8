/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;

public sealed interface AbstractNbtList
extends Iterable<NbtElement>,
NbtElement
permits NbtList, NbtByteArray, NbtIntArray, NbtLongArray {
    public void clear();

    public boolean setElement(int var1, NbtElement var2);

    public boolean addElement(int var1, NbtElement var2);

    public NbtElement net_minecraft_nbt_NbtElement_method_10536(int var1);

    public NbtElement net_minecraft_nbt_NbtElement_method_10534(int var1);

    public int size();

    default public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    default public Iterator<NbtElement> iterator() {
        return new Iterator<NbtElement>(){
            private int current;

            @Override
            public boolean hasNext() {
                return this.current < AbstractNbtList.this.size();
            }

            @Override
            public NbtElement net_minecraft_nbt_NbtElement_next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return AbstractNbtList.this.net_minecraft_nbt_NbtElement_method_10534(this.current++);
            }

            @Override
            public Object java_lang_Object_next() {
                return this.net_minecraft_nbt_NbtElement_next();
            }
        };
    }

    default public Stream<NbtElement> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}

