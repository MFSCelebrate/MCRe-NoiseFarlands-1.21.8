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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(value=EnvType.CLIENT)
public record TextureResourceMetadata(boolean blur, boolean clamp) {
    final static public boolean field_32980 = false;
    final static public boolean field_32981 = false;
    final static public Codec<TextureResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("blur", (Object)false).forGetter(TextureResourceMetadata::blur), (App)Codec.BOOL.optionalFieldOf("clamp", (Object)false).forGetter(TextureResourceMetadata::clamp)).apply((Applicative)instance, TextureResourceMetadata::new));
    final static public ResourceMetadataSerializer<TextureResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<TextureResourceMetadata>("texture", CODEC);
}

