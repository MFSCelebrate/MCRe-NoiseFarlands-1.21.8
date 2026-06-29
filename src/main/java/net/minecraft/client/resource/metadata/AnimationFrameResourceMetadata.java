/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record AnimationFrameResourceMetadata(int index, Optional<Integer> time) {
    final static public Codec<AnimationFrameResourceMetadata> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(AnimationFrameResourceMetadata::index), (App)Codecs.POSITIVE_INT.optionalFieldOf("time").forGetter(AnimationFrameResourceMetadata::time)).apply((Applicative)instance, AnimationFrameResourceMetadata::new));
    final static public Codec<AnimationFrameResourceMetadata> CODEC = Codec.either(Codecs.NON_NEGATIVE_INT, BASE_CODEC).xmap(either -> (AnimationFrameResourceMetadata)either.map(AnimationFrameResourceMetadata::new, metadata -> metadata), metadatax -> metadatax.time.isPresent() ? Either.right((Object)metadatax) : Either.left((Object)metadatax.index));

    public AnimationFrameResourceMetadata(int index) {
        this(index, Optional.empty());
    }

    public int getTime(int defaultTime) {
        return this.time.orElse(defaultTime);
    }
}

