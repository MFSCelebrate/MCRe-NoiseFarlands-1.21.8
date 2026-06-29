/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record Defines(Map<String, String> values, Set<String> flags) {
    final static public Defines EMPTY = new Defines(Map.of(), Set.of());
    final static public Codec<Defines> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.STRING).optionalFieldOf("values", Map.of()).forGetter(Defines::values), (App)Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(Defines::flags)).apply((Applicative)instance, Defines::new));

    public static Builder builder() {
        return new Builder();
    }

    public Defines withMerged(Defines other) {
        if (this.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        ImmutableMap.Builder builder = ImmutableMap.builderWithExpectedSize((int)(this.values.size() + other.values.size()));
        builder.putAll(this.values);
        builder.putAll(other.values);
        ImmutableSet.Builder builder2 = ImmutableSet.builderWithExpectedSize((int)(this.flags.size() + other.flags.size()));
        builder2.addAll(this.flags);
        builder2.addAll(other.flags);
        return new Defines((Map<String, String>)builder.buildKeepingLast(), (Set<String>)builder2.build());
    }

    public String toSource() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : this.values.entrySet()) {
            String string = entry.getKey();
            String string2 = entry.getValue();
            stringBuilder.append("#define ").append(string).append(" ").append(string2).append('\n');
        }
        for (String string3 : this.flags) {
            stringBuilder.append("#define ").append(string3).append('\n');
        }
        return stringBuilder.toString();
    }

    public boolean isEmpty() {
        return this.values.isEmpty() && this.flags.isEmpty();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final private ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
        final private ImmutableSet.Builder<String> flags = ImmutableSet.builder();

        Builder() {
        }

        public Builder define(String key, String value) {
            if (value.isBlank()) {
                throw new IllegalArgumentException("Cannot define empty string");
            }
            this.values.put((Object)key, (Object)Builder.escapeLinebreak(value));
            return this;
        }

        private static String escapeLinebreak(String string) {
            return string.replaceAll("\n", "\\\\\n");
        }

        public Builder define(String key, float value) {
            this.values.put((Object)key, (Object)String.valueOf(value));
            return this;
        }

        public Builder define(String name, int value) {
            this.values.put((Object)name, (Object)String.valueOf(value));
            return this;
        }

        public Builder flag(String flag) {
            this.flags.add((Object)flag);
            return this;
        }

        public Defines build() {
            return new Defines((Map<String, String>)this.values.build(), (Set<String>)this.flags.build());
        }
    }
}

