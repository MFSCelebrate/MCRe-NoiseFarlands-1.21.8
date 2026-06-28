/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 */
package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

@Deprecated
public class Lazy<T> {
    final private Supplier<T> supplier = Suppliers.memoize(delegate::get);

    public Lazy(Supplier<T> delegate) {
    }

    public T get() {
        return this.supplier.get();
    }
}

