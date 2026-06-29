/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Range;

public final class ResourcePackCompatibility
extends Enum<ResourcePackCompatibility> {
    final static public ResourcePackCompatibility TOO_OLD = new ResourcePackCompatibility("old");
    final static public ResourcePackCompatibility TOO_NEW = new ResourcePackCompatibility("new");
    final static public ResourcePackCompatibility COMPATIBLE = new ResourcePackCompatibility("compatible");
    final private Text notification;
    final private Text confirmMessage;
    final static private ResourcePackCompatibility[] field_14221;

    public static ResourcePackCompatibility[] values() {
        return (ResourcePackCompatibility[])field_14221.clone();
    }

    public static ResourcePackCompatibility valueOf(String string) {
        return Enum.valueOf(ResourcePackCompatibility.class, string);
    }

    private ResourcePackCompatibility(String translationSuffix) {
        this.notification = Text.translatable("pack.incompatible." + translationSuffix).formatted(Formatting.GRAY);
        this.confirmMessage = Text.translatable("pack.incompatible.confirm." + translationSuffix);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public static ResourcePackCompatibility from(Range<Integer> range, int current) {
        if (range.maxInclusive() < current) {
            return TOO_OLD;
        }
        if (current < range.minInclusive()) {
            return TOO_NEW;
        }
        return COMPATIBLE;
    }

    public Text getNotification() {
        return this.notification;
    }

    public Text getConfirmMessage() {
        return this.confirmMessage;
    }

    private static ResourcePackCompatibility[] method_36584() {
        return new ResourcePackCompatibility[]{TOO_OLD, TOO_NEW, COMPATIBLE};
    }

    static {
        field_14221 = ResourcePackCompatibility.method_36584();
    }
}

