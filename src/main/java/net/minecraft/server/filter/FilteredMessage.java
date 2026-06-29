/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.filter;

import java.util.Objects;
import net.minecraft.network.message.FilterMask;
import org.jetbrains.annotations.Nullable;

public record FilteredMessage(String raw, FilterMask mask) {
    final static public FilteredMessage EMPTY = FilteredMessage.permitted("");

    public static FilteredMessage permitted(String raw) {
        return new FilteredMessage(raw, FilterMask.PASS_THROUGH);
    }

    public static FilteredMessage censored(String raw) {
        return new FilteredMessage(raw, FilterMask.FULLY_FILTERED);
    }

    @Nullable
    public String filter() {
        return this.mask.filter(this.raw);
    }

    public String getString() {
        return Objects.requireNonNullElse(this.filter(), "");
    }

    public boolean isFiltered() {
        return !this.mask.isPassThrough();
    }
}

