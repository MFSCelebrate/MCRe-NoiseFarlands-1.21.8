/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;

@Environment(value=EnvType.CLIENT)
public final class RealmsWorldGeneratorType
extends Enum<RealmsWorldGeneratorType> {
    final static public RealmsWorldGeneratorType DEFAULT = new RealmsWorldGeneratorType(0, WorldPresets.DEFAULT);
    final static public RealmsWorldGeneratorType FLAT = new RealmsWorldGeneratorType(1, WorldPresets.FLAT);
    final static public RealmsWorldGeneratorType LARGE_BIOMES = new RealmsWorldGeneratorType(2, WorldPresets.LARGE_BIOMES);
    final static public RealmsWorldGeneratorType AMPLIFIED = new RealmsWorldGeneratorType(3, WorldPresets.AMPLIFIED);
    final private int id;
    final private Text text;
    final static private RealmsWorldGeneratorType[] field_27950;

    public static RealmsWorldGeneratorType[] values() {
        return (RealmsWorldGeneratorType[])field_27950.clone();
    }

    public static RealmsWorldGeneratorType valueOf(String string) {
        return Enum.valueOf(RealmsWorldGeneratorType.class, string);
    }

    private RealmsWorldGeneratorType(int id, RegistryKey<WorldPreset> presetKey) {
        this.id = id;
        this.text = Text.translatable(presetKey.getValue().toTranslationKey("generator"));
    }

    public Text getText() {
        return this.text;
    }

    public int getId() {
        return this.id;
    }

    private static RealmsWorldGeneratorType[] method_36856() {
        return new RealmsWorldGeneratorType[]{DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED};
    }

    static {
        field_27950 = RealmsWorldGeneratorType.method_36856();
    }
}

