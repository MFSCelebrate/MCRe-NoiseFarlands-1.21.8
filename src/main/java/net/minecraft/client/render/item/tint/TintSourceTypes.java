/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.tint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.CustomModelDataTintSource;
import net.minecraft.client.render.item.tint.DyeTintSource;
import net.minecraft.client.render.item.tint.FireworkTintSource;
import net.minecraft.client.render.item.tint.GrassTintSource;
import net.minecraft.client.render.item.tint.MapColorTintSource;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.client.render.item.tint.TeamTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class TintSourceTypes {
    final static public Codecs.IdMapper<Identifier, MapCodec<? extends TintSource>> ID_MAPPER = new Codecs.IdMapper();
    final static public Codec<TintSource> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatch(TintSource::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put(Identifier.ofVanilla("custom_model_data"), CustomModelDataTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("constant"), ConstantTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("dye"), DyeTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("grass"), GrassTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("firework"), FireworkTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("potion"), PotionTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("map_color"), MapColorTintSource.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("team"), TeamTintSource.CODEC);
    }
}

