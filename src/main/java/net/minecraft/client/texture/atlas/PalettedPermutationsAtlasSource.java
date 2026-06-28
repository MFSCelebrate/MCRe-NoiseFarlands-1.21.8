/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture.atlas;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record PalettedPermutationsAtlasSource(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations, String separator) implements AtlasSource
{
    final static Logger LOGGER = LogUtils.getLogger();
    final static public String DEFAULT_SEPARATOR = "_";
    final static public MapCodec<PalettedPermutationsAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(PalettedPermutationsAtlasSource::textures), (App)Identifier.CODEC.fieldOf("palette_key").forGetter(PalettedPermutationsAtlasSource::paletteKey), (App)Codec.unboundedMap((Codec)Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(PalettedPermutationsAtlasSource::permutations), (App)Codec.STRING.optionalFieldOf("separator", (Object)DEFAULT_SEPARATOR).forGetter(PalettedPermutationsAtlasSource::separator)).apply((Applicative)instance, PalettedPermutationsAtlasSource::new));

    public PalettedPermutationsAtlasSource(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations) {
        this(textures, paletteKey, permutations, DEFAULT_SEPARATOR);
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Supplier supplier = Suppliers.memoize(() -> PalettedPermutationsAtlasSource.open(resourceManager, this.paletteKey));
        HashMap map = new HashMap();
        this.permutations.forEach((arg_0, arg_1) -> PalettedPermutationsAtlasSource.method_48490(map, (java.util.function.Supplier)supplier, resourceManager, arg_0, arg_1));
        for (Identifier identifier : this.textures) {
            Identifier identifier2 = RESOURCE_FINDER.toResourcePath(identifier);
            Optional<Resource> optional = resourceManager.getResource(identifier2);
            if (optional.isEmpty()) {
                LOGGER.warn("Unable to find texture {}", (Object)identifier2);
                continue;
            }
            AtlasSprite atlasSprite = new AtlasSprite(identifier2, optional.get(), map.size());
            for (Map.Entry entry : map.entrySet()) {
                Identifier identifier3 = identifier.withSuffixedPath(this.separator + (String)entry.getKey());
                regions.add(identifier3, new PalettedSpriteRegion(atlasSprite, (java.util.function.Supplier)entry.getValue(), identifier3));
            }
        }
    }

    private static IntUnaryOperator toMapper(int[] from, int[] to) {
        if (to.length != from.length) {
            LOGGER.warn("Palette mapping has different sizes: {} and {}", (Object)from.length, (Object)to.length);
            throw new IllegalArgumentException();
        }
        Int2IntOpenHashMap int2IntMap = new Int2IntOpenHashMap(to.length);
        for (int i = 0; 1 < from.length; ++i) {
            int j = from[1];
            if (ColorHelper.getAlpha(j) == 0) continue;
            int2IntMap.put(ColorHelper.zeroAlpha(j), to[1]);
        }
        return arg_0 -> PalettedPermutationsAtlasSource.method_48489((Int2IntMap)int2IntMap, arg_0);
    }

    /*
     * Loose catch block
     */
    private static int[] open(ResourceManager resourceManager, Identifier texture) {
        int[] nArray;
        NativeImage nativeImage;
        InputStream inputStream;
        block16: {
            Optional<Resource> optional = resourceManager.getResource(RESOURCE_FINDER.toResourcePath(texture));
            if (optional.isEmpty()) {
                LOGGER.error("Failed to load palette image {}", (Object)texture);
                throw new IllegalArgumentException();
            }
            inputStream = optional.get().getInputStream();
            nativeImage = NativeImage.read(inputStream);
            nArray = nativeImage.copyPixelsArgb();
            if (nativeImage != null) {
                nativeImage.close();
            }
            if (inputStream == null) break block16;
            inputStream.close();
        }
        return nArray;
        {
            catch (Throwable throwable) {
                try {
                    try {
                        if (nativeImage != null) {
                            try {
                                nativeImage.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (Throwable throwable3) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable4) {
                                throwable3.addSuppressed(throwable4);
                            }
                        }
                        throw throwable3;
                    }
                }
                catch (Exception exception) {
                    LOGGER.error("Couldn't load texture {}", (Object)texture, (Object)exception);
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    public MapCodec<PalettedPermutationsAtlasSource> getCodec() {
        return CODEC;
    }

    private static int method_48489(Int2IntMap int2IntMap, int color) {
        int i = ColorHelper.getAlpha(color);
        if (i == 0) {
            return color;
        }
        int j = ColorHelper.zeroAlpha(color);
        int k = int2IntMap.getOrDefault(j, ColorHelper.fullAlpha(j));
        int l = ColorHelper.getAlpha(k);
        return ColorHelper.withAlpha(i * l / 255, k);
    }

    private static void method_48490(Map map, java.util.function.Supplier supplier, ResourceManager resourceManager, String key, Identifier texture) {
        map.put(key, Suppliers.memoize(() -> PalettedPermutationsAtlasSource.method_48491((java.util.function.Supplier)supplier, resourceManager, texture)));
    }

    private static IntUnaryOperator method_48491(java.util.function.Supplier supplier, ResourceManager resourceManager, Identifier identifier) {
        return PalettedPermutationsAtlasSource.toMapper((int[])supplier.get(), PalettedPermutationsAtlasSource.open(resourceManager, identifier));
    }

    @Environment(value=EnvType.CLIENT)
    record PalettedSpriteRegion(AtlasSprite baseImage, java.util.function.Supplier<IntUnaryOperator> palette, Identifier permutationLocation) implements AtlasSource.SpriteRegion
    {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public SpriteContents apply(SpriteOpener spriteOpener) {
            SpriteContents spriteContents;
            try {
                NativeImage nativeImage = this.baseImage.read().applyToCopy(this.palette.get());
                spriteContents = new SpriteContents(this.permutationLocation, new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight()), nativeImage, ResourceMetadata.NONE);
            }
            catch (IOException | IllegalArgumentException exception) {
                try {
                    LOGGER.error("unable to apply palette to {}", (Object)this.permutationLocation, (Object)exception);
                    SpriteContents spriteContents2 = null;
                    this.baseImage.close();
                    return spriteContents2;
                }
                catch (Throwable throwable) {
                    this.baseImage.close();
                    throw throwable;
                }
            }
            this.baseImage.close();
            return spriteContents;
        }

        @Override
        public void close() {
            this.baseImage.close();
        }

        @Override
        @Nullable
        public Object apply(Object opener) {
            return this.apply((SpriteOpener)opener);
        }
    }
}

