/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ReferenceFont(Identifier id) implements FontLoader
{
    final static public MapCodec<ReferenceFont> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("id").forGetter(ReferenceFont::id)).apply((Applicative)instance, ReferenceFont::new));

    @Override
    public FontType getType() {
        return FontType.REFERENCE;
    }

    @Override
    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        return Either.right((Object)new FontLoader.Reference(this.id));
    }
}

