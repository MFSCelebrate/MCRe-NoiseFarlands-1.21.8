/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record SingleAtlasSource(Identifier resourceId, Optional<Identifier> spriteId) implements AtlasSource
{
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public MapCodec<SingleAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("resource").forGetter(SingleAtlasSource::resourceId), (App)Identifier.CODEC.optionalFieldOf("sprite").forGetter(SingleAtlasSource::spriteId)).apply((Applicative)instance, SingleAtlasSource::new));

    public SingleAtlasSource(Identifier resourceId) {
        this(resourceId, Optional.empty());
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Identifier identifier = RESOURCE_FINDER.toResourcePath(this.resourceId);
        Optional<Resource> optional = resourceManager.getResource(identifier);
        if (optional.isPresent()) {
            regions.add(this.spriteId.orElse(this.resourceId), optional.get());
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)identifier);
        }
    }

    public MapCodec<SingleAtlasSource> getCodec() {
        return CODEC;
    }
}

