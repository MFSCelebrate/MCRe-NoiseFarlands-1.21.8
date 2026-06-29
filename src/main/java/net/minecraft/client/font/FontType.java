/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.ReferenceFont;
import net.minecraft.client.font.SpaceFont;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.client.font.UnihexFont;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public final class FontType
extends Enum<FontType>
implements StringIdentifiable {
    final static public FontType BITMAP = new FontType("bitmap", BitmapFont.Loader.CODEC);
    final static public FontType TTF = new FontType("ttf", TrueTypeFontLoader.CODEC);
    final static public FontType SPACE = new FontType("space", SpaceFont.Loader.CODEC);
    final static public FontType UNIHEX = new FontType("unihex", UnihexFont.Loader.CODEC);
    final static public FontType REFERENCE = new FontType("reference", ReferenceFont.CODEC);
    final static public Codec<FontType> CODEC;
    final private String id;
    final private MapCodec<? extends FontLoader> loaderCodec;
    final static private FontType[] field_2316;

    public static FontType[] values() {
        return (FontType[])field_2316.clone();
    }

    public static FontType valueOf(String string) {
        return Enum.valueOf(FontType.class, string);
    }

    private FontType(String id, MapCodec<? extends FontLoader> loaderCodec) {
        this.id = id;
        this.loaderCodec = loaderCodec;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public MapCodec<? extends FontLoader> getLoaderCodec() {
        return this.loaderCodec;
    }

    private static FontType[] method_36876() {
        return new FontType[]{BITMAP, TTF, SPACE, UNIHEX, REFERENCE};
    }

    static {
        field_2316 = FontType.method_36876();
        CODEC = StringIdentifiable.createCodec(FontType::values);
    }
}

