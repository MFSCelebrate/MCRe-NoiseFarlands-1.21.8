/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.BlockEntry;

@Environment(value=EnvType.CLIENT)
public record FilterAtlasSource(BlockEntry pattern) implements AtlasSource
{
    final static public MapCodec<FilterAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockEntry.CODEC.fieldOf("pattern").forGetter(FilterAtlasSource::pattern)).apply((Applicative)instance, FilterAtlasSource::new));

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        regions.removeIf(this.pattern.getIdentifierPredicate());
    }

    public MapCodec<FilterAtlasSource> getCodec() {
        return CODEC;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FilterAtlasSource.class, "filter", "pattern"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FilterAtlasSource.class, "filter", "pattern"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FilterAtlasSource.class, "filter", "pattern"}, this, object);
    }
}

