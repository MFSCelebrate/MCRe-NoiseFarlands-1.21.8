/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
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
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
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
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record UnstitchAtlasSource(Identifier resource, List<Region> regions, double divisorX, double divisorY) implements AtlasSource
{
    final static Logger LOGGER = LogUtils.getLogger();
    final static public MapCodec<UnstitchAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("resource").forGetter(UnstitchAtlasSource::resource), (App)Codecs.nonEmptyList(Region.CODEC.listOf()).fieldOf("regions").forGetter(UnstitchAtlasSource::regions), (App)Codec.DOUBLE.optionalFieldOf("divisor_x", (Object)1.0).forGetter(UnstitchAtlasSource::divisorX), (App)Codec.DOUBLE.optionalFieldOf("divisor_y", (Object)1.0).forGetter(UnstitchAtlasSource::divisorY)).apply((Applicative)instance, UnstitchAtlasSource::new));

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Identifier identifier = RESOURCE_FINDER.toResourcePath(this.resource);
        Optional<Resource> optional = resourceManager.getResource(identifier);
        if (optional.isPresent()) {
            AtlasSprite atlasSprite = new AtlasSprite(identifier, optional.get(), this.regions.size());
            for (Region region : this.regions) {
                regions.add(region.sprite, new SpriteRegion(atlasSprite, region, this.divisorX, this.divisorY));
            }
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)identifier);
        }
    }

    public MapCodec<UnstitchAtlasSource> getCodec() {
        return CODEC;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnstitchAtlasSource.class, "resource;regions;xDivisor;yDivisor", "resource", "regions", "divisorX", "divisorY"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnstitchAtlasSource.class, "resource;regions;xDivisor;yDivisor", "resource", "regions", "divisorX", "divisorY"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnstitchAtlasSource.class, "resource;regions;xDivisor;yDivisor", "resource", "regions", "divisorX", "divisorY"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Region
    extends Record {
        final Identifier sprite;
        final double x;
        final double y;
        final double width;
        final double height;
        final static public Codec<Region> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("sprite").forGetter(Region::sprite), (App)Codec.DOUBLE.fieldOf("x").forGetter(Region::x), (App)Codec.DOUBLE.fieldOf("y").forGetter(Region::y), (App)Codec.DOUBLE.fieldOf("width").forGetter(Region::width), (App)Codec.DOUBLE.fieldOf("height").forGetter(Region::height)).apply((Applicative)instance, Region::new));

        public Region(Identifier identifier, double d, double e, double f, double g) {
            this.sprite = identifier;
            this.x = d;
            this.y = e;
            this.width = f;
            this.height = g;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this, object);
        }

        public Identifier sprite() {
            return this.sprite;
        }

        public double x() {
            return this.x;
        }

        public double y() {
            return this.y;
        }

        public double width() {
            return this.width;
        }

        public double height() {
            return this.height;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class SpriteRegion
    implements AtlasSource.SpriteRegion {
        final private AtlasSprite sprite;
        final private Region region;
        final private double divisorX;
        final private double divisorY;

        SpriteRegion(AtlasSprite sprite, Region region, double divisorX, double divisorY) {
            this.sprite = sprite;
            this.region = region;
            this.divisorX = divisorX;
            this.divisorY = divisorY;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SpriteContents apply(SpriteOpener spriteOpener) {
            try {
                NativeImage nativeImage = this.sprite.read();
                double d = (double)nativeImage.getWidth() / this.divisorX;
                double e = (double)nativeImage.getHeight() / this.divisorY;
                int i = MathHelper.floor(this.region.x * d);
                int j = MathHelper.floor(this.region.y * e);
                int k = MathHelper.floor(this.region.width * d);
                int l = MathHelper.floor(this.region.height * e);
                NativeImage nativeImage2 = new NativeImage(NativeImage.Format.RGBA, k, l, false);
                nativeImage.copyRect(nativeImage2, i, j, 0, 0, k, l, false, false);
                SpriteContents spriteContents = new SpriteContents(this.region.sprite, new SpriteDimensions(k, l), nativeImage2, ResourceMetadata.NONE);
                this.sprite.close();
                return spriteContents;
            }
            catch (Exception exception) {
                try {
                    LOGGER.error("Failed to unstitch region {}", (Object)this.region.sprite, (Object)exception);
                    this.sprite.close();
                }
                catch (Throwable throwable) {
                    this.sprite.close();
                    throw throwable;
                }
            }
            return MissingSprite.createSpriteContents();
        }

        @Override
        public void close() {
            this.sprite.close();
        }

        @Override
        public Object apply(Object opener) {
            return this.apply((SpriteOpener)opener);
        }
    }
}

