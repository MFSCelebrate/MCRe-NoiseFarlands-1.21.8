/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record AnimationResourceMetadata(Optional<List<AnimationFrameResourceMetadata>> frames, Optional<Integer> width, Optional<Integer> height, int defaultFrameTime, boolean interpolate) {
    final static public Codec<AnimationResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)AnimationFrameResourceMetadata.CODEC.listOf().optionalFieldOf("frames").forGetter(AnimationResourceMetadata::frames), (App)Codecs.POSITIVE_INT.optionalFieldOf("width").forGetter(AnimationResourceMetadata::width), (App)Codecs.POSITIVE_INT.optionalFieldOf("height").forGetter(AnimationResourceMetadata::height), (App)Codecs.POSITIVE_INT.optionalFieldOf("frametime", (Object)1).forGetter(AnimationResourceMetadata::defaultFrameTime), (App)Codec.BOOL.optionalFieldOf("interpolate", (Object)false).forGetter(AnimationResourceMetadata::interpolate)).apply((Applicative)instance, AnimationResourceMetadata::new));
    final static public ResourceMetadataSerializer<AnimationResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<AnimationResourceMetadata>("animation", CODEC);

    public SpriteDimensions getSize(int defaultWidth, int defaultHeight) {
        if (this.width.isPresent()) {
            if (this.height.isPresent()) {
                return new SpriteDimensions(this.width.get(), this.height.get());
            }
            return new SpriteDimensions(this.width.get(), defaultHeight);
        }
        if (this.height.isPresent()) {
            return new SpriteDimensions(defaultWidth, this.height.get());
        }
        int i = Math.min(defaultWidth, defaultHeight);
        return new SpriteDimensions(1, 1);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AnimationResourceMetadata.class, "frames;frameWidth;frameHeight;defaultFrameTime;interpolatedFrames", "frames", "width", "height", "defaultFrameTime", "interpolate"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AnimationResourceMetadata.class, "frames;frameWidth;frameHeight;defaultFrameTime;interpolatedFrames", "frames", "width", "height", "defaultFrameTime", "interpolate"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AnimationResourceMetadata.class, "frames;frameWidth;frameHeight;defaultFrameTime;interpolatedFrames", "frames", "width", "height", "defaultFrameTime", "interpolate"}, this, object);
    }
}

