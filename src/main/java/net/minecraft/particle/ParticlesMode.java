/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.particle;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public final class ParticlesMode
extends Enum<ParticlesMode>
implements TranslatableOption {
    final static public ParticlesMode ALL = new ParticlesMode(0, "options.particles.all");
    final static public ParticlesMode DECREASED = new ParticlesMode(1, "options.particles.decreased");
    final static public ParticlesMode MINIMAL = new ParticlesMode(2, "options.particles.minimal");
    final static private IntFunction<ParticlesMode> BY_ID;
    final private int id;
    final private String translationKey;
    final static private ParticlesMode[] field_18203;

    public static ParticlesMode[] values() {
        return (ParticlesMode[])field_18203.clone();
    }

    public static ParticlesMode valueOf(String string) {
        return Enum.valueOf(ParticlesMode.class, string);
    }

    private ParticlesMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ParticlesMode byId(int id) {
        return BY_ID.apply(id);
    }

    private static ParticlesMode[] method_36865() {
        return new ParticlesMode[]{ALL, DECREASED, MINIMAL};
    }

    static {
        field_18203 = ParticlesMode.method_36865();
        BY_ID = ValueLists.createIndexToValueFunction(ParticlesMode::getId, ParticlesMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

