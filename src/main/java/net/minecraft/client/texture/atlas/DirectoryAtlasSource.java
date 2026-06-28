/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record DirectoryAtlasSource(String sourcePath, String idPrefix) implements AtlasSource
{
    final static public MapCodec<DirectoryAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("source").forGetter(DirectoryAtlasSource::sourcePath), (App)Codec.STRING.fieldOf("prefix").forGetter(DirectoryAtlasSource::idPrefix)).apply((Applicative)instance, DirectoryAtlasSource::new));

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        ResourceFinder resourceFinder = new ResourceFinder("textures/" + this.sourcePath, ".png");
        resourceFinder.findResources(resourceManager).forEach((id, resource) -> {
            Identifier identifier = resourceFinder.toResourceId((Identifier)id).withPrefixedPath(this.idPrefix);
            regions.add(identifier, (Resource)resource);
        });
    }

    public MapCodec<DirectoryAtlasSource> getCodec() {
        return CODEC;
    }
}

