/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.resource.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.JsonHelper;

public interface ResourceMetadata {
    final static public ResourceMetadata NONE = new ResourceMetadata(){

        @Override
        public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
            return Optional.empty();
        }
    };
    final static public InputSupplier<ResourceMetadata> NONE_SUPPLIER = () -> NONE;

    public static ResourceMetadata create(InputStream stream) throws IOException {
        ResourceMetadata resourceMetadata;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));){
            final JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
            resourceMetadata = new ResourceMetadata(){

                @Override
                public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
                    String string = serializer.name();
                    if (jsonObject.has(string)) {
                        Object object = serializer.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject.get(string)).getOrThrow(JsonParseException::new);
                        return Optional.of(object);
                    }
                    return Optional.empty();
                }
            };
        }
        return resourceMetadata;
    }

    public <T> Optional<T> decode(ResourceMetadataSerializer<T> var1);

    default public ResourceMetadata copy(Collection<ResourceMetadataSerializer<?>> serializers) {
        Builder builder = new Builder();
        for (ResourceMetadataSerializer<?> resourceMetadataSerializer : serializers) {
            this.decodeAndAdd(builder, resourceMetadataSerializer);
        }
        return builder.build();
    }

    private <T> void decodeAndAdd(Builder builder, ResourceMetadataSerializer<T> serializer) {
        this.decode(serializer).ifPresent(value -> builder.add(serializer, value));
    }

    public static class Builder {
        final private ImmutableMap.Builder<ResourceMetadataSerializer<?>, Object> values = ImmutableMap.builder();

        public <T> Builder add(ResourceMetadataSerializer<T> serializer, T value) {
            this.values.put(serializer, value);
            return this;
        }

        public ResourceMetadata build() {
            final ImmutableMap immutableMap = this.values.build();
            if (immutableMap.isEmpty()) {
                return NONE;
            }
            return new ResourceMetadata(){

                @Override
                public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
                    return Optional.ofNullable(immutableMap.get(serializer));
                }
            };
        }
    }
}

