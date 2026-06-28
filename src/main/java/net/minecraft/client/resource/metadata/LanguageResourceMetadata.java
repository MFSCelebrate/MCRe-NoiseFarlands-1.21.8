/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(value=EnvType.CLIENT)
public record LanguageResourceMetadata(Map<String, LanguageDefinition> definitions) {
    final static public Codec<String> LANGUAGE_CODE_CODEC = Codec.string(1, 16);
    final static public Codec<LanguageResourceMetadata> CODEC = Codec.unboundedMap(LANGUAGE_CODE_CODEC, LanguageDefinition.CODEC).xmap(LanguageResourceMetadata::new, LanguageResourceMetadata::definitions);
    final static public ResourceMetadataSerializer<LanguageResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<LanguageResourceMetadata>("language", CODEC);

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LanguageResourceMetadata.class, "languages", "definitions"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LanguageResourceMetadata.class, "languages", "definitions"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LanguageResourceMetadata.class, "languages", "definitions"}, this, object);
    }
}

