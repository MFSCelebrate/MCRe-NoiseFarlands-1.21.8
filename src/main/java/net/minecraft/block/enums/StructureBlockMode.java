/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.block.enums;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public final class StructureBlockMode
extends Enum<StructureBlockMode>
implements StringIdentifiable {
    final static public StructureBlockMode SAVE = new StructureBlockMode("save");
    final static public StructureBlockMode LOAD = new StructureBlockMode("load");
    final static public StructureBlockMode CORNER = new StructureBlockMode("corner");
    final static public StructureBlockMode DATA = new StructureBlockMode("data");
    @Deprecated
    final static public Codec<StructureBlockMode> CODEC;
    final private String name;
    final private Text text;
    final static private StructureBlockMode[] field_12700;

    public static StructureBlockMode[] values() {
        return (StructureBlockMode[])field_12700.clone();
    }

    public static StructureBlockMode valueOf(String string) {
        return Enum.valueOf(StructureBlockMode.class, string);
    }

    private StructureBlockMode(String name) {
        this.name = name;
        this.text = Text.translatable("structure_block.mode_info." + name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Text asText() {
        return this.text;
    }

    private static StructureBlockMode[] method_36737() {
        return new StructureBlockMode[]{SAVE, LOAD, CORNER, DATA};
    }

    static {
        field_12700 = StructureBlockMode.method_36737();
        CODEC = Codecs.enumByName(StructureBlockMode::valueOf);
    }
}

