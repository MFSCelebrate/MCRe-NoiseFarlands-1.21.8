/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

@Environment(value=EnvType.CLIENT)
public final class InactivityFpsLimit
extends Enum<InactivityFpsLimit>
implements TranslatableOption,
StringIdentifiable {
    final static public InactivityFpsLimit MINIMIZED = new InactivityFpsLimit(0, "minimized", "options.inactivityFpsLimit.minimized");
    final static public InactivityFpsLimit AFK = new InactivityFpsLimit(1, "afk", "options.inactivityFpsLimit.afk");
    final static public Codec<InactivityFpsLimit> Codec;
    final private int ordinal;
    final private String name;
    final private String translationKey;
    final static private InactivityFpsLimit[] field_52749;

    public static InactivityFpsLimit[] values() {
        return (InactivityFpsLimit[])field_52749.clone();
    }

    public static InactivityFpsLimit valueOf(String string) {
        return Enum.valueOf(InactivityFpsLimit.class, string);
    }

    private InactivityFpsLimit(int ordinal, String name, String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.ordinal;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static InactivityFpsLimit[] method_61961() {
        return new InactivityFpsLimit[]{MINIMIZED, AFK};
    }

    static {
        field_52749 = InactivityFpsLimit.method_61961();
        Codec = StringIdentifiable.createCodec(InactivityFpsLimit::values);
    }
}

