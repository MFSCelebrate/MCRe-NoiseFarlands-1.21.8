/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.sprite.FabricAtlasPreparation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricAtlasPreparation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasManager
implements AutoCloseable {
    final private Map<Identifier, Atlas> atlases;

    public SpriteAtlasManager(Map<Identifier, Identifier> loaders, TextureManager textureManager) {
        this.atlases = loaders.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture((Identifier)entry.getKey());
            textureManager.registerTexture((Identifier)entry.getKey(), spriteAtlasTexture);
            return new Atlas(spriteAtlasTexture, (Identifier)entry.getValue());
        }));
    }

    public SpriteAtlasTexture getAtlas(Identifier id) {
        return this.atlases.get(id).atlas();
    }

    @Override
    public void close() {
        this.atlases.values().forEach(Atlas::close);
        this.atlases.clear();
    }

    public Map<Identifier, CompletableFuture<AtlasPreparation>> reload(ResourceManager resourceManager, int mipmapLevels, Executor executor) {
        return Util.transformMapValues(this.atlases, atlas -> SpriteLoader.fromAtlas(atlas.atlas).load(resourceManager, atlas.atlasInfoLocation, mipmapLevels, executor).thenApply(stitchResult -> new AtlasPreparation(atlas.atlas, (SpriteLoader.StitchResult)stitchResult)));
    }

    @Environment(value=EnvType.CLIENT)
    static final class Atlas
    extends Record
    implements AutoCloseable {
        final SpriteAtlasTexture atlas;
        final Identifier atlasInfoLocation;

        Atlas(SpriteAtlasTexture spriteAtlasTexture, Identifier identifier) {
            this.atlas = spriteAtlasTexture;
            this.atlasInfoLocation = identifier;
        }

        @Override
        public void close() {
            this.atlas.clear();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Atlas.class, "atlas;atlasInfoLocation", "atlas", "atlasInfoLocation"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Atlas.class, "atlas;atlasInfoLocation", "atlas", "atlasInfoLocation"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Atlas.class, "atlas;atlasInfoLocation", "atlas", "atlasInfoLocation"}, this, object);
        }

        public SpriteAtlasTexture atlas() {
            return this.atlas;
        }

        public Identifier atlasInfoLocation() {
            return this.atlasInfoLocation;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class AtlasPreparation
    implements FabricAtlasPreparation {
        final private SpriteAtlasTexture atlasTexture;
        final private SpriteLoader.StitchResult stitchResult;

        public AtlasPreparation(SpriteAtlasTexture atlasTexture, SpriteLoader.StitchResult stitchResult) {
            this.atlasTexture = atlasTexture;
            this.stitchResult = stitchResult;
        }

        @Nullable
        public Sprite getSprite(Identifier id) {
            return this.stitchResult.regions().get(id);
        }

        public Sprite getMissingSprite() {
            return this.stitchResult.missing();
        }

        public CompletableFuture<Void> whenComplete() {
            return this.stitchResult.readyForUpload();
        }

        public void upload() {
            this.atlasTexture.upload(this.stitchResult);
        }
    }
}

