/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class RenderPipeline {
    final private Identifier location;
    final private Identifier vertexShader;
    final private Identifier fragmentShader;
    final private Defines shaderDefines;
    final private List<String> samplers;
    final private List<UniformDescription> uniforms;
    final private DepthTestFunction depthTestFunction;
    final private PolygonMode polygonMode;
    final private boolean cull;
    final private LogicOp colorLogic;
    final private Optional<BlendFunction> blendFunction;
    final private boolean writeColor;
    final private boolean writeAlpha;
    final private boolean writeDepth;
    final private VertexFormat vertexFormat;
    final private VertexFormat.DrawMode vertexFormatMode;
    final private float depthBiasScaleFactor;
    final private float depthBiasConstant;
    final private int sortKey;
    static private int sortKeySeed;

    protected RenderPipeline(Identifier location, Identifier vertexShader, Identifier fragmentShader, Defines shaderDefines, List<String> samplers, List<UniformDescription> uniforms, Optional<BlendFunction> blendFunction, DepthTestFunction depthTestFunction, PolygonMode polygonMode, boolean cull, boolean writeColor, boolean writeAlpha, boolean writeDepth, LogicOp colorLogic, VertexFormat vertexFormat, VertexFormat.DrawMode vertexFormatMode, float depthBiasScaleFactor, float depthBiasConstant, int sortKey) {
        this.location = location;
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderDefines = shaderDefines;
        this.samplers = samplers;
        this.uniforms = uniforms;
        this.depthTestFunction = depthTestFunction;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.blendFunction = blendFunction;
        this.writeColor = writeColor;
        this.writeAlpha = writeAlpha;
        this.writeDepth = writeDepth;
        this.colorLogic = colorLogic;
        this.vertexFormat = vertexFormat;
        this.vertexFormatMode = vertexFormatMode;
        this.depthBiasScaleFactor = depthBiasScaleFactor;
        this.depthBiasConstant = depthBiasConstant;
        this.sortKey = sortKey;
    }

    public int getSortKey() {
        return this.sortKey;
    }

    public static void updateSortKeySeed() {
        sortKeySeed = Math.round(100000.0f * (float)Math.random());
    }

    public String toString() {
        return this.location.toString();
    }

    public DepthTestFunction getDepthTestFunction() {
        return this.depthTestFunction;
    }

    public PolygonMode getPolygonMode() {
        return this.polygonMode;
    }

    public boolean isCull() {
        return this.cull;
    }

    public LogicOp getColorLogic() {
        return this.colorLogic;
    }

    public Optional<BlendFunction> getBlendFunction() {
        return this.blendFunction;
    }

    public boolean isWriteColor() {
        return this.writeColor;
    }

    public boolean isWriteAlpha() {
        return this.writeAlpha;
    }

    public boolean isWriteDepth() {
        return this.writeDepth;
    }

    public float getDepthBiasScaleFactor() {
        return this.depthBiasScaleFactor;
    }

    public float getDepthBiasConstant() {
        return this.depthBiasConstant;
    }

    public Identifier getLocation() {
        return this.location;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public VertexFormat.DrawMode getVertexFormatMode() {
        return this.vertexFormatMode;
    }

    public Identifier getVertexShader() {
        return this.vertexShader;
    }

    public Identifier getFragmentShader() {
        return this.fragmentShader;
    }

    public Defines getShaderDefines() {
        return this.shaderDefines;
    }

    public List<String> getSamplers() {
        return this.samplers;
    }

    public List<UniformDescription> getUniforms() {
        return this.uniforms;
    }

    public boolean wantsDepthTexture() {
        return this.depthTestFunction != DepthTestFunction.NO_DEPTH_TEST || this.depthBiasConstant != 0.0f || this.depthBiasScaleFactor != 0.0f || this.writeDepth;
    }

    public static Builder builder(Snippet ... snippets) {
        Builder builder = new Builder();
        for (Snippet snippet : snippets) {
            builder.withSnippet(snippet);
        }
        return builder;
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static class Builder {
        static private int nextPipelineSortKey;
        private Optional<Identifier> location = Optional.empty();
        private Optional<Identifier> fragmentShader = Optional.empty();
        private Optional<Identifier> vertexShader = Optional.empty();
        private Optional<Defines.Builder> definesBuilder = Optional.empty();
        private Optional<List<String>> samplers = Optional.empty();
        private Optional<List<UniformDescription>> uniforms = Optional.empty();
        private Optional<DepthTestFunction> depthTestFunction = Optional.empty();
        private Optional<PolygonMode> polygonMode = Optional.empty();
        private Optional<Boolean> cull = Optional.empty();
        private Optional<Boolean> writeColor = Optional.empty();
        private Optional<Boolean> writeAlpha = Optional.empty();
        private Optional<Boolean> writeDepth = Optional.empty();
        private Optional<LogicOp> colorLogic = Optional.empty();
        private Optional<BlendFunction> blendFunction = Optional.empty();
        private Optional<VertexFormat> vertexFormat = Optional.empty();
        private Optional<VertexFormat.DrawMode> vertexFormatMode = Optional.empty();
        private float depthBiasScaleFactor;
        private float depthBiasConstant;

        Builder() {
        }

        public Builder withLocation(String location) {
            this.location = Optional.of(Identifier.ofVanilla(location));
            return this;
        }

        public Builder withLocation(Identifier location) {
            this.location = Optional.of(location);
            return this;
        }

        public Builder withFragmentShader(String fragmentShader) {
            this.fragmentShader = Optional.of(Identifier.ofVanilla(fragmentShader));
            return this;
        }

        public Builder withFragmentShader(Identifier fragmentShader) {
            this.fragmentShader = Optional.of(fragmentShader);
            return this;
        }

        public Builder withVertexShader(String string) {
            this.vertexShader = Optional.of(Identifier.ofVanilla(string));
            return this;
        }

        public Builder withVertexShader(Identifier vertexShader) {
            this.vertexShader = Optional.of(vertexShader);
            return this;
        }

        public Builder withShaderDefine(String flag) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(Defines.builder());
            }
            this.definesBuilder.get().flag(flag);
            return this;
        }

        public Builder withShaderDefine(String name, int value) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(Defines.builder());
            }
            this.definesBuilder.get().define(name, value);
            return this;
        }

        public Builder withShaderDefine(String name, float value) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(Defines.builder());
            }
            this.definesBuilder.get().define(name, value);
            return this;
        }

        public Builder withSampler(String sampler) {
            if (this.samplers.isEmpty()) {
                this.samplers = Optional.of(new ArrayList());
            }
            this.samplers.get().add(sampler);
            return this;
        }

        public Builder withUniform(String name, UniformType type) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList());
            }
            if (type == UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
            }
            this.uniforms.get().add(new UniformDescription(name, type));
            return this;
        }

        public Builder withUniform(String name, UniformType type, TextureFormat format) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList());
            }
            if (type != UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Only texel buffer can specify texture format");
            }
            this.uniforms.get().add(new UniformDescription(name, format));
            return this;
        }

        public Builder withDepthTestFunction(DepthTestFunction depthTestFunction) {
            this.depthTestFunction = Optional.of(depthTestFunction);
            return this;
        }

        public Builder withPolygonMode(PolygonMode polygonMode) {
            this.polygonMode = Optional.of(polygonMode);
            return this;
        }

        public Builder withCull(boolean cull) {
            this.cull = Optional.of(cull);
            return this;
        }

        public Builder withBlend(BlendFunction blendFunction) {
            this.blendFunction = Optional.of(blendFunction);
            return this;
        }

        public Builder withoutBlend() {
            this.blendFunction = Optional.empty();
            return this;
        }

        public Builder withColorWrite(boolean writeColor) {
            this.writeColor = Optional.of(writeColor);
            this.writeAlpha = Optional.of(writeColor);
            return this;
        }

        public Builder withColorWrite(boolean writeColor, boolean writeAlpha) {
            this.writeColor = Optional.of(writeColor);
            this.writeAlpha = Optional.of(writeAlpha);
            return this;
        }

        public Builder withDepthWrite(boolean writeDepth) {
            this.writeDepth = Optional.of(writeDepth);
            return this;
        }

        @Deprecated
        public Builder withColorLogic(LogicOp colorLogic) {
            this.colorLogic = Optional.of(colorLogic);
            return this;
        }

        public Builder withVertexFormat(VertexFormat vertexFormat, VertexFormat.DrawMode vertexFormatMode) {
            this.vertexFormat = Optional.of(vertexFormat);
            this.vertexFormatMode = Optional.of(vertexFormatMode);
            return this;
        }

        public Builder withDepthBias(float depthBiasScaleFactor, float depthBiasConstant) {
            this.depthBiasScaleFactor = depthBiasScaleFactor;
            this.depthBiasConstant = depthBiasConstant;
            return this;
        }

        void withSnippet(Snippet snippet) {
            if (snippet.vertexShader.isPresent()) {
                this.vertexShader = snippet.vertexShader;
            }
            if (snippet.fragmentShader.isPresent()) {
                this.fragmentShader = snippet.fragmentShader;
            }
            if (snippet.shaderDefines.isPresent()) {
                if (this.definesBuilder.isEmpty()) {
                    this.definesBuilder = Optional.of(Defines.builder());
                }
                Defines defines = snippet.shaderDefines.get();
                for (Map.Entry<String, String> entry : defines.values().entrySet()) {
                    this.definesBuilder.get().define(entry.getKey(), entry.getValue());
                }
                for (String string : defines.flags()) {
                    this.definesBuilder.get().flag(string);
                }
            }
            snippet.samplers.ifPresent(samplers -> {
                if (this.samplers.isPresent()) {
                    this.samplers.get().addAll((Collection<String>)samplers);
                } else {
                    this.samplers = Optional.of(new ArrayList(samplers));
                }
            });
            snippet.uniforms.ifPresent(uniforms -> {
                if (this.uniforms.isPresent()) {
                    this.uniforms.get().addAll((Collection<UniformDescription>)uniforms);
                } else {
                    this.uniforms = Optional.of(new ArrayList(uniforms));
                }
            });
            if (snippet.depthTestFunction.isPresent()) {
                this.depthTestFunction = snippet.depthTestFunction;
            }
            if (snippet.cull.isPresent()) {
                this.cull = snippet.cull;
            }
            if (snippet.writeColor.isPresent()) {
                this.writeColor = snippet.writeColor;
            }
            if (snippet.writeAlpha.isPresent()) {
                this.writeAlpha = snippet.writeAlpha;
            }
            if (snippet.writeDepth.isPresent()) {
                this.writeDepth = snippet.writeDepth;
            }
            if (snippet.colorLogic.isPresent()) {
                this.colorLogic = snippet.colorLogic;
            }
            if (snippet.blendFunction.isPresent()) {
                this.blendFunction = snippet.blendFunction;
            }
            if (snippet.vertexFormat.isPresent()) {
                this.vertexFormat = snippet.vertexFormat;
            }
            if (snippet.vertexFormatMode.isPresent()) {
                this.vertexFormatMode = snippet.vertexFormatMode;
            }
        }

        public Snippet buildSnippet() {
            return new Snippet(this.vertexShader, this.fragmentShader, this.definesBuilder.map(Defines.Builder::build), this.samplers.map(Collections::unmodifiableList), this.uniforms.map(Collections::unmodifiableList), this.blendFunction, this.depthTestFunction, this.polygonMode, this.cull, this.writeColor, this.writeAlpha, this.writeDepth, this.colorLogic, this.vertexFormat, this.vertexFormatMode);
        }

        public RenderPipeline build() {
            if (this.location.isEmpty()) {
                throw new IllegalStateException("Missing location");
            }
            if (this.vertexShader.isEmpty()) {
                throw new IllegalStateException("Missing vertex shader");
            }
            if (this.fragmentShader.isEmpty()) {
                throw new IllegalStateException("Missing fragment shader");
            }
            if (this.vertexFormat.isEmpty()) {
                throw new IllegalStateException("Missing vertex buffer format");
            }
            if (this.vertexFormatMode.isEmpty()) {
                throw new IllegalStateException("Missing vertex mode");
            }
            return new RenderPipeline(this.location.get(), this.vertexShader.get(), this.fragmentShader.get(), this.definesBuilder.orElse(Defines.builder()).build(), List.copyOf(this.samplers.orElse(new ArrayList())), this.uniforms.orElse(Collections.emptyList()), this.blendFunction, this.depthTestFunction.orElse(DepthTestFunction.LEQUAL_DEPTH_TEST), this.polygonMode.orElse(PolygonMode.FILL), this.cull.orElse(true), this.writeColor.orElse(true), this.writeAlpha.orElse(true), this.writeDepth.orElse(true), this.colorLogic.orElse(LogicOp.NONE), this.vertexFormat.get(), this.vertexFormatMode.get(), this.depthBiasScaleFactor, this.depthBiasConstant, nextPipelineSortKey++);
        }
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static final class Snippet
    extends Record {
        final Optional<Identifier> vertexShader;
        final Optional<Identifier> fragmentShader;
        final Optional<Defines> shaderDefines;
        final Optional<List<String>> samplers;
        final Optional<List<UniformDescription>> uniforms;
        final Optional<BlendFunction> blendFunction;
        final Optional<DepthTestFunction> depthTestFunction;
        final private Optional<PolygonMode> polygonMode;
        final Optional<Boolean> cull;
        final Optional<Boolean> writeColor;
        final Optional<Boolean> writeAlpha;
        final Optional<Boolean> writeDepth;
        final Optional<LogicOp> colorLogic;
        final Optional<VertexFormat> vertexFormat;
        final Optional<VertexFormat.DrawMode> vertexFormatMode;

        public Snippet(Optional<Identifier> optional, Optional<Identifier> optional2, Optional<Defines> optional3, Optional<List<String>> optional4, Optional<List<UniformDescription>> optional5, Optional<BlendFunction> optional6, Optional<DepthTestFunction> optional7, Optional<PolygonMode> optional8, Optional<Boolean> optional9, Optional<Boolean> optional10, Optional<Boolean> optional11, Optional<Boolean> optional12, Optional<LogicOp> optional13, Optional<VertexFormat> optional14, Optional<VertexFormat.DrawMode> optional15) {
            this.vertexShader = optional;
            this.fragmentShader = optional2;
            this.shaderDefines = optional3;
            this.samplers = optional4;
            this.uniforms = optional5;
            this.blendFunction = optional6;
            this.depthTestFunction = optional7;
            this.polygonMode = optional8;
            this.cull = optional9;
            this.writeColor = optional10;
            this.writeAlpha = optional11;
            this.writeDepth = optional12;
            this.colorLogic = optional13;
            this.vertexFormat = optional14;
            this.vertexFormatMode = optional15;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this, object);
        }

        public Optional<Identifier> vertexShader() {
            return this.vertexShader;
        }

        public Optional<Identifier> fragmentShader() {
            return this.fragmentShader;
        }

        public Optional<Defines> shaderDefines() {
            return this.shaderDefines;
        }

        public Optional<List<String>> samplers() {
            return this.samplers;
        }

        public Optional<List<UniformDescription>> uniforms() {
            return this.uniforms;
        }

        public Optional<BlendFunction> blendFunction() {
            return this.blendFunction;
        }

        public Optional<DepthTestFunction> depthTestFunction() {
            return this.depthTestFunction;
        }

        public Optional<PolygonMode> polygonMode() {
            return this.polygonMode;
        }

        public Optional<Boolean> cull() {
            return this.cull;
        }

        public Optional<Boolean> writeColor() {
            return this.writeColor;
        }

        public Optional<Boolean> writeAlpha() {
            return this.writeAlpha;
        }

        public Optional<Boolean> writeDepth() {
            return this.writeDepth;
        }

        public Optional<LogicOp> colorLogic() {
            return this.colorLogic;
        }

        public Optional<VertexFormat> vertexFormat() {
            return this.vertexFormat;
        }

        public Optional<VertexFormat.DrawMode> vertexFormatMode() {
            return this.vertexFormatMode;
        }
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public record UniformDescription(String name, UniformType type, @Nullable TextureFormat textureFormat) {
        public UniformDescription(String name, UniformType type) {
            this(name, type, null);
            if (type == UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Texel buffer needs a texture format");
            }
        }

        public UniformDescription(String name, TextureFormat format) {
            this(name, UniformType.TEXEL_BUFFER, format);
        }
    }
}

