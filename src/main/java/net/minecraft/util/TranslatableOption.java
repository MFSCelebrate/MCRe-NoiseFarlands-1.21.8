/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.text.Text;

public interface TranslatableOption {
    public int getId();

    public String getTranslationKey();

    default public Text getText() {
        return Text.translatable(this.getTranslationKey());
    }
}

