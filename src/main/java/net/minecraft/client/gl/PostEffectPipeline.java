/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline(Map<Identifier, Targets> internalTargets, List<Pass> passes) {
    final static public Codec<PostEffectPipeline> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap(Identifier.CODEC, Targets.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostEffectPipeline::internalTargets), (App)Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostEffectPipeline::passes)).apply((Applicative)instance, PostEffectPipeline::new));

    @Environment(value=EnvType.CLIENT)
    public record Targets(Optional<Integer> width, Optional<Integer> height, boolean persistent, int clearColor) {
        final static public Codec<Targets> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.POSITIVE_INT.optionalFieldOf("width").forGetter(Targets::width), (App)Codecs.POSITIVE_INT.optionalFieldOf("height").forGetter(Targets::height), (App)Codec.BOOL.optionalFieldOf("persistent", (Object)false).forGetter(Targets::persistent), (App)Codecs.ARGB.optionalFieldOf("clear_color", (Object)0).forGetter(Targets::clearColor)).apply((Applicative)instance, Targets::new));
    }

    @Environment(value=EnvType.CLIENT)
    public record Pass(Identifier vertexShaderId, Identifier fragmentShaderId, List<Input> inputs, Identifier outputTarget, Map<String, List<UniformValue>> uniforms) {
        final static private Codec<List<Input>> INPUTS_CODEC = Input.CODEC.listOf().validate(inputs -> {
            ObjectArraySet set = new ObjectArraySet(inputs.size());
            for (Input input : inputs) {
                if (set.add(input.samplerName())) continue;
                return DataResult.error(() -> "Encountered repeated sampler name: " + input.samplerName());
            }
            return DataResult.success((Object)inputs);
        });
        final static private Codec<Map<String, List<UniformValue>>> UNIFORMS_CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)UniformValue.CODEC.listOf());
        final static public Codec<Pass> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("vertex_shader").forGetter(Pass::vertexShaderId), (App)Identifier.CODEC.fieldOf("fragment_shader").forGetter(Pass::fragmentShaderId), (App)INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(Pass::inputs), (App)Identifier.CODEC.fieldOf("output").forGetter(Pass::outputTarget), (App)UNIFORMS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(Pass::uniforms)).apply((Applicative)instance, Pass::new));

        public Stream<Identifier> streamTargets() {
            Stream stream = this.inputs.stream().flatMap(input -> input.getTargetId().stream());
            return Stream.concat(stream, Stream.of(this.outputTarget));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TargetSampler(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements Input
    {
        final static public Codec<TargetSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("sampler_name").forGetter(TargetSampler::samplerName), (App)Identifier.CODEC.fieldOf("target").forGetter(TargetSampler::targetId), (App)Codec.BOOL.optionalFieldOf("use_depth_buffer", (Object)false).forGetter(TargetSampler::useDepthBuffer), (App)Codec.BOOL.optionalFieldOf("bilinear", (Object)false).forGetter(TargetSampler::bilinear)).apply((Applicative)instance, TargetSampler::new));

        @Override
        public Set<Identifier> getTargetId() {
            return Set.of(this.targetId);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TextureSampler(String samplerName, Identifier location, int width, int height, boolean bilinear) implements Input
    {
        final static public Codec<TextureSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("sampler_name").forGetter(TextureSampler::samplerName), (App)Identifier.CODEC.fieldOf("location").forGetter(TextureSampler::location), (App)Codecs.POSITIVE_INT.fieldOf("width").forGetter(TextureSampler::width), (App)Codecs.POSITIVE_INT.fieldOf("height").forGetter(TextureSampler::height), (App)Codec.BOOL.optionalFieldOf("bilinear", (Object)false).forGetter(TextureSampler::bilinear)).apply((Applicative)instance, TextureSampler::new));

        @Override
        public Set<Identifier> getTargetId() {
            return Set.of();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static sealed interface Input
    permits TextureSampler, TargetSampler {
        final static public Codec<Input> CODEC = Codec.xor(TextureSampler.CODEC, TargetSampler.CODEC).xmap(either -> (Input)either.map(Function.identity(), Function.identity()), sampler -> {
            Input input = sampler;
            Objects.requireNonNull(input);
            Input input2 = input;
            int i = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{TextureSampler.class, TargetSampler.class}, (Object)input2, i)) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    TextureSampler textureSampler = (TextureSampler)input2;
                    yield Either.left((Object)textureSampler);
                }
                case 1 -> {
                    TargetSampler targetSampler = (TargetSampler)input2;
                    yield Either.right((Object)targetSampler);
                }
            };
        });

        public String samplerName();

        public Set<Identifier> getTargetId();
    }
}

