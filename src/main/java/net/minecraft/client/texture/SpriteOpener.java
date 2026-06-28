/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface SpriteOpener {
    final static public Logger LOGGER = LogUtils.getLogger();

    public static SpriteOpener create(Collection<ResourceMetadataSerializer<?>> metadatas) {
        return (id, resource) -> {
            SpriteDimensions spriteDimensions;
            NativeImage nativeImage;
            ResourceMetadata resourceMetadata;
            block10: {
                try {
                    resourceMetadata = resource.getMetadata().copy(metadatas);
                }
                catch (Exception exception) {
                    LOGGER.error("Unable to parse metadata from {}", (Object)id, (Object)exception);
                    return null;
                }
                try (InputStream inputStream = resource.getInputStream();){
                    nativeImage = NativeImage.read(inputStream);
                    if (inputStream == null) break block10;
                }
                catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {}", (Object)id, (Object)iOException);
                    return null;
                }
            }
            Optional<AnimationResourceMetadata> optional = resourceMetadata.decode(AnimationResourceMetadata.SERIALIZER);
            if (!optional.isPresent()) {
                spriteDimensions = new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight());
                return new SpriteContents(id, spriteDimensions, nativeImage, resourceMetadata);
            }
            spriteDimensions = optional.get().getSize(nativeImage.getWidth(), nativeImage.getHeight());
            if (MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width())) {
                if (MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) return new SpriteContents(id, spriteDimensions, nativeImage, resourceMetadata);
            }
            LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{id, nativeImage.getWidth(), nativeImage.getHeight(), spriteDimensions.width(), spriteDimensions.height()});
            nativeImage.close();
            return null;
        };
    }

    @Nullable
    public SpriteContents loadSprite(Identifier var1, Resource var2);
}

