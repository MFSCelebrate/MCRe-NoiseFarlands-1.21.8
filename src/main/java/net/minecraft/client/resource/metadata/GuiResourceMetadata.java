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
import net.minecraft.client.texture.Scaling;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(value=EnvType.CLIENT)
public record GuiResourceMetadata(Scaling scaling) {
    final static public GuiResourceMetadata DEFAULT = new GuiResourceMetadata(Scaling.STRETCH);
    final static public Codec<GuiResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Scaling.CODEC.optionalFieldOf("scaling", (Object)Scaling.STRETCH).forGetter(GuiResourceMetadata::scaling)).apply((Applicative)instance, GuiResourceMetadata::new));
    final static public ResourceMetadataSerializer<GuiResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<GuiResourceMetadata>("gui", CODEC);
}

