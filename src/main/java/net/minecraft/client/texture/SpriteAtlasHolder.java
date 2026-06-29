/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;

@Environment(value=EnvType.CLIENT)
public abstract class SpriteAtlasHolder
implements ResourceReloader,
AutoCloseable {
    final private SpriteAtlasTexture atlas;
    final private Identifier sourcePath;
    final private Set<ResourceMetadataSerializer<?>> metadataReaders;

    public SpriteAtlasHolder(TextureManager textureManager, Identifier atlasId, Identifier sourcePath) {
        this(textureManager, atlasId, sourcePath, SpriteLoader.METADATA_SERIALIZERS);
    }

    public SpriteAtlasHolder(TextureManager textureManager, Identifier atlasId, Identifier sourcePath, Set<ResourceMetadataSerializer<?>> metadataReaders) {
        this.sourcePath = sourcePath;
        this.atlas = new SpriteAtlasTexture(atlasId);
        textureManager.registerTexture(this.atlas.getId(), this.atlas);
        this.metadataReaders = metadataReaders;
    }

    protected Sprite getSprite(Identifier objectId) {
        return this.atlas.getSprite(objectId);
    }

    @Override
    public final CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
        return ((CompletableFuture)((CompletableFuture)SpriteLoader.fromAtlas(this.atlas).load(resourceManager, this.sourcePath, 0, executor, this.metadataReaders).thenCompose(SpriteLoader.StitchResult::whenComplete)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(this::afterReload, executor2);
    }

    private void afterReload(SpriteLoader.StitchResult stitchResult) {
        ScopedProfiler scopedProfiler = Profilers.get().scoped("upload");
        try {
            this.atlas.upload(stitchResult);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    @Override
    public void close() {
        this.atlas.clear();
    }
}

