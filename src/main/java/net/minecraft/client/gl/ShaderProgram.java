/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 *  org.lwjgl.opengl.GL31
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.UniformType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.lwjgl.opengl.GL31;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ShaderProgram
implements AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    static public Set<String> PREDEFINED_UNIFORMS = Sets.newHashSet((Object[])new String[]{"Projection", "Lighting", "Fog", "Globals"});
    static public ShaderProgram INVALID = new ShaderProgram(-1, "invalid");
    final private Map<String, GlUniform> uniformsByName = new HashMap<String, GlUniform>();
    final private int glRef;
    final private String debugLabel;

    private ShaderProgram(int glRef, String debugLabel) {
        this.glRef = glRef;
        this.debugLabel = debugLabel;
    }

    public static ShaderProgram create(CompiledShader vertexShader, CompiledShader fragmentShader, VertexFormat format, String name) throws ShaderLoader.LoadException {
        String string2;
        int i = GlStateManager.glCreateProgram();
        if (i <= 0) {
            throw new ShaderLoader.LoadException("Could not create shader program (returned program ID " + i + ")");
        }
        int j = 0;
        for (String string2 : format.getElementAttributeNames()) {
            GlStateManager._glBindAttribLocation(i, j, string2);
            ++j;
        }
        GlStateManager.glAttachShader(i, vertexShader.getHandle());
        GlStateManager.glAttachShader(i, fragmentShader.getHandle());
        GlStateManager.glLinkProgram(i);
        int k = GlStateManager.glGetProgrami(i, GlConst.GL_LINK_STATUS);
        string2 = GlStateManager.glGetProgramInfoLog(i, 32768);
        if (k == 0 || string2.contains("Failed for unknown reason")) {
            throw new ShaderLoader.LoadException("Error encountered when linking program containing VS " + String.valueOf(vertexShader.getId()) + " and FS " + String.valueOf(fragmentShader.getId()) + ". Log output: " + string2);
        }
        if (!string2.isEmpty()) {
            LOGGER.info("Info log when linking program containing VS {} and FS {}. Log output: {}", new Object[]{vertexShader.getId(), fragmentShader.getId(), string2});
        }
        return new ShaderProgram(i, name);
    }

    public void set(List<RenderPipeline.UniformDescription> uniforms, List<String> samplers) {
        int i = 0;
        int j = 0;
        for (RenderPipeline.UniformDescription uniformDescription : uniforms) {
            String string = uniformDescription.name();
            GlUniform.TexelBuffer glUniform = switch (uniformDescription.type()) {
                default -> throw new MatchException(null, null);
                case UniformType.UNIFORM_BUFFER -> {
                    int k = GL31.glGetUniformBlockIndex((int)this.glRef, (CharSequence)string);
                    if (k == -1) {
                        yield null;
                    }
                    int l = i++;
                    GL31.glUniformBlockBinding((int)this.glRef, (int)k, (int)l);
                    yield new GlUniform.UniformBuffer(l);
                }
                case UniformType.TEXEL_BUFFER -> {
                    int k = GlStateManager._glGetUniformLocation(this.glRef, string);
                    if (k == -1) {
                        LOGGER.warn("{} shader program does not use utb {} defined in the pipeline. This might be a bug.", (Object)this.debugLabel, (Object)string);
                        yield null;
                    }
                    int l = j++;
                    yield new GlUniform.TexelBuffer(k, l, Objects.requireNonNull(uniformDescription.textureFormat()));
                }
            };
            if (glUniform == null) continue;
            this.uniformsByName.put(string, glUniform);
        }
        for (String string2 : samplers) {
            int m = GlStateManager._glGetUniformLocation(this.glRef, string2);
            if (m == -1) {
                LOGGER.warn("{} shader program does not use sampler {} defined in the pipeline. This might be a bug.", (Object)this.debugLabel, (Object)string2);
                continue;
            }
            int n = j++;
            this.uniformsByName.put(string2, new GlUniform.Sampler(m, n));
        }
        int o = GlStateManager.glGetProgrami(this.glRef, GL31.GL_ACTIVE_UNIFORM_BLOCKS);
        for (int p = 0; p < o; ++p) {
            String string = GL31.glGetActiveUniformBlockName((int)this.glRef, (int)p);
            if (this.uniformsByName.containsKey(string)) continue;
            if (!samplers.contains(string) && PREDEFINED_UNIFORMS.contains(string)) {
                int n = i++;
                GL31.glUniformBlockBinding((int)this.glRef, (int)p, (int)n);
                this.uniformsByName.put(string, new GlUniform.UniformBuffer(n));
                continue;
            }
            LOGGER.warn("Found unknown and unsupported uniform {} in {}", (Object)string, (Object)this.debugLabel);
        }
    }

    @Override
    public void close() {
        this.uniformsByName.values().forEach(GlUniform::close);
        GlStateManager.glDeleteProgram(this.glRef);
    }

    @Nullable
    public GlUniform getUniform(String name) {
        RenderSystem.assertOnRenderThread();
        return this.uniformsByName.get(name);
    }

    @VisibleForTesting
    public int getGlRef() {
        return this.glRef;
    }

    public String toString() {
        return this.debugLabel;
    }

    public String getDebugLabel() {
        return this.debugLabel;
    }

    public Map<String, GlUniform> getUniforms() {
        return this.uniformsByName;
    }
}

