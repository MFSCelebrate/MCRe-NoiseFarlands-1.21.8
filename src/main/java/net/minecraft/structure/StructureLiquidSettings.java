/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public final class StructureLiquidSettings
extends Enum<StructureLiquidSettings>
implements StringIdentifiable {
    final static public StructureLiquidSettings IGNORE_WATERLOGGING = new StructureLiquidSettings("ignore_waterlogging");
    final static public StructureLiquidSettings APPLY_WATERLOGGING = new StructureLiquidSettings("apply_waterlogging");
    static public Codec<StructureLiquidSettings> codec;
    final private String id;
    final static private StructureLiquidSettings[] field_52241;

    public static StructureLiquidSettings[] values() {
        return (StructureLiquidSettings[])field_52241.clone();
    }

    public static StructureLiquidSettings valueOf(String string) {
        return Enum.valueOf(StructureLiquidSettings.class, string);
    }

    private StructureLiquidSettings(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static StructureLiquidSettings[] method_61019() {
        return new StructureLiquidSettings[]{IGNORE_WATERLOGGING, APPLY_WATERLOGGING};
    }

    static {
        field_52241 = StructureLiquidSettings.method_61019();
        codec = StringIdentifiable.createBasicCodec(StructureLiquidSettings::values);
    }
}

