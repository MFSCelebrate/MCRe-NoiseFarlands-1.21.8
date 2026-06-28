/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

public record SaveVersion(int id, String series) {
    final static public String MAIN_SERIES = "main";

    public boolean isNotMainSeries() {
        return !this.series.equals(MAIN_SERIES);
    }

    public boolean isAvailableTo(SaveVersion other) {
        return this.series().equals(other.series());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveVersion.class, "version;series", "id", "series"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveVersion.class, "version;series", "id", "series"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveVersion.class, "version;series", "id", "series"}, this, object);
    }
}

