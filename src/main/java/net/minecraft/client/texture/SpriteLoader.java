/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteLoader {
    final static public Set<ResourceMetadataSerializer<?>> METADATA_SERIALIZERS = Set.of(AnimationResourceMetadata.SERIALIZER);
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Identifier id;
    final private int maxTextureSize;
    final private int width;
    final private int height;

    public SpriteLoader(Identifier id, int maxTextureSize, int width, int height) {
        this.id = id;
        this.maxTextureSize = maxTextureSize;
        this.width = width;
        this.height = height;
    }

    public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
        return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize(), atlasTexture.getWidth(), atlasTexture.getHeight());
    }

    public StitchResult stitch(List<SpriteContents> sprites, int mipLevel, Executor executor) {
        ScopedProfiler scopedProfiler = Profilers.get().scoped(() -> "stitch " + String.valueOf(this.id));
        try {
            int l;
            int i = this.maxTextureSize;
            TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<SpriteContents>(i, i, mipLevel);
            int j = Integer.MAX_VALUE;
            int k = 1 << mipLevel;
            for (SpriteContents spriteContents : sprites) {
                j = Math.min(j, Math.min(spriteContents.getWidth(), spriteContents.getHeight()));
                l = Math.min(Integer.lowestOneBit(spriteContents.getWidth()), Integer.lowestOneBit(spriteContents.getHeight()));
                if (l < k) {
                    LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{spriteContents.getId(), spriteContents.getWidth(), spriteContents.getHeight(), MathHelper.floorLog2(k), MathHelper.floorLog2(l)});
                    k = l;
                }
                textureStitcher.add(spriteContents);
            }
            int m = Math.min(j, k);
            int n = MathHelper.floorLog2(m);
            if (n < mipLevel) {
                LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.id, mipLevel, n, m});
                l = n;
            } else {
                l = mipLevel;
            }
            try {
                textureStitcher.stitch();
            }
            catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
                CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
                CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
                crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(sprite -> String.format(Locale.ROOT, "%s[%dx%d]", sprite.getId(), sprite.getWidth(), sprite.getHeight())).collect(Collectors.joining(",")));
                crashReportSection.add("Max Texture Size", i);
                throw new CrashException(crashReport);
            }
            int o = Math.max(textureStitcher.getWidth(), this.width);
            int p = Math.max(textureStitcher.getHeight(), this.height);
            Map<Identifier, Sprite> map = this.collectStitchedSprites(textureStitcher, o, p);
            Sprite sprite2 = map.get(MissingSprite.getMissingSpriteId());
            CompletableFuture<Object> completableFuture = l > 0 ? CompletableFuture.runAsync(() -> map.values().forEach(sprite -> sprite.getContents().generateMipmaps(l)), executor) : CompletableFuture.completedFuture(null);
            StitchResult stitchResult = new StitchResult(o, p, l, sprite2, map, completableFuture);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
            return stitchResult;
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

    public static CompletableFuture<List<SpriteContents>> loadAll(SpriteOpener opener, List<Function<SpriteOpener, SpriteContents>> sources, Executor executor) {
        List<CompletableFuture> list = sources.stream().map(sprite -> CompletableFuture.supplyAsync(() -> (SpriteContents)sprite.apply(opener), executor)).toList();
        return Util.combineSafe(list).thenApply(sprites -> sprites.stream().filter(Objects::nonNull).toList());
    }

    public CompletableFuture<StitchResult> load(ResourceManager resourceManager, Identifier path, int mipLevel, Executor executor) {
        return this.load(resourceManager, path, mipLevel, executor, METADATA_SERIALIZERS);
    }

    public CompletableFuture<StitchResult> load(ResourceManager resourceManager, Identifier path, int mipLevel, Executor executor, Collection<ResourceMetadataSerializer<?>> metadatas) {
        SpriteOpener spriteOpener = SpriteOpener.create(metadatas);
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> AtlasLoader.of(resourceManager, path).loadSources(resourceManager), executor).thenCompose(sources -> SpriteLoader.loadAll(spriteOpener, sources, executor))).thenApply(sprites -> this.stitch((List<SpriteContents>)sprites, mipLevel, executor));
    }

    private Map<Identifier, Sprite> collectStitchedSprites(TextureStitcher<SpriteContents> stitcher, int atlasWidth, int atlasHeight) {
        HashMap<Identifier, Sprite> map = new HashMap<Identifier, Sprite>();
        stitcher.getStitchedSprites((info, x, y) -> map.put(info.getId(), new Sprite(this.id, (SpriteContents)info, atlasWidth, atlasHeight, x, y)));
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> regions, CompletableFuture<Void> readyForUpload) implements FabricStitchResult
    {
        public CompletableFuture<StitchResult> whenComplete() {
            return this.readyForUpload.thenApply(void_ -> this);
        }
    }
}

