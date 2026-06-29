/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PostEffectProcessor
implements AutoCloseable {
    final static public Identifier MAIN = Identifier.ofVanilla("main");
    final private List<PostEffectPass> passes;
    final private Map<Identifier, PostEffectPipeline.Targets> internalTargets;
    final private Set<Identifier> externalTargets;
    final private Map<Identifier, Framebuffer> framebuffers = new HashMap<Identifier, Framebuffer>();
    final private ProjectionMatrix2 projectionMatrix;

    private PostEffectProcessor(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets, ProjectionMatrix2 projectionMatrix) {
        this.passes = passes;
        this.internalTargets = internalTargets;
        this.externalTargets = externalTargets;
        this.projectionMatrix = projectionMatrix;
    }

    public static PostEffectProcessor parseEffect(PostEffectPipeline pipeline, TextureManager textureManager, Set<Identifier> availableExternalTargets, Identifier id, ProjectionMatrix2 projectionMatrix) throws ShaderLoader.LoadException {
        Stream stream = pipeline.passes().stream().flatMap(PostEffectPipeline.Pass::streamTargets);
        Set<Identifier> set = stream.filter(target -> !pipeline.internalTargets().containsKey(target)).collect(Collectors.toSet());
        Sets.SetView set2 = Sets.difference(set, availableExternalTargets);
        if (!set2.isEmpty()) {
            throw new ShaderLoader.LoadException("Referenced external targets are not available in this context: " + String.valueOf(set2));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < pipeline.passes().size(); ++i) {
            PostEffectPipeline.Pass pass = pipeline.passes().get(i);
            builder.add((Object)PostEffectProcessor.parsePass(textureManager, pass, id.withSuffixedPath("/" + i)));
        }
        return new PostEffectProcessor((List<PostEffectPass>)builder.build(), pipeline.internalTargets(), set, projectionMatrix);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    private static PostEffectPass parsePass(TextureManager textureManager, PostEffectPipeline.Pass pass, Identifier id) throws ShaderLoader.LoadException {
        RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET).withFragmentShader(pass.fragmentShaderId()).withVertexShader(pass.vertexShaderId()).withLocation(id);
        for (PostEffectPipeline.Input input : pass.inputs()) {
            builder.withSampler(input.samplerName() + "Sampler");
        }
        builder.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);
        for (String string : pass.uniforms().keySet()) {
            builder.withUniform(string, UniformType.UNIFORM_BUFFER);
        }
        RenderPipeline renderPipeline = builder.build();
        ArrayList<PostEffectPass.Sampler> list = new ArrayList<PostEffectPass.Sampler>();
        Iterator<PostEffectPipeline.Input> iterator = pass.inputs().iterator();
        block9: while (true) {
            PostEffectPipeline.Input input;
            if (!iterator.hasNext()) {
                return new PostEffectPass(renderPipeline, pass.outputTarget(), pass.uniforms(), list);
            }
            PostEffectPipeline.Input input2 = iterator.next();
            Objects.requireNonNull(input2);
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PostEffectPipeline.TextureSampler.class, PostEffectPipeline.TargetSampler.class}, (Object)input, n)) {
                case 0: {
                    int n2;
                    Object object;
                    PostEffectPipeline.TextureSampler textureSampler = (PostEffectPipeline.TextureSampler)input;
                    Object string2 = object = textureSampler.samplerName();
                    Object identifier = object = textureSampler.location();
                    int i = n2 = textureSampler.width();
                    int j = n2 = textureSampler.height();
                    int bl2 = n2 = (int)(textureSampler.bilinear() ? 1 : 0);
                    AbstractTexture abstractTexture = textureManager.getTexture(((Identifier)identifier).withPath(name -> "textures/effect/" + name + ".png"));
                    abstractTexture.setFilter(bl2 != 0, false);
                    list.add(new PostEffectPass.TextureSampler((String)string2, abstractTexture, i, j));
                    continue block9;
                }
                case 1: {
                    Object object = (PostEffectPipeline.TargetSampler)input;
                    try {
                        boolean bl;
                        Object object2 = ((PostEffectPipeline.TargetSampler)object).samplerName();
                        String string3 = object2;
                        Object identifier2 = object2 = ((PostEffectPipeline.TargetSampler)object).targetId();
                        boolean bl2 = bl = ((PostEffectPipeline.TargetSampler)object).useDepthBuffer();
                        boolean bl3 = bl = ((PostEffectPipeline.TargetSampler)object).bilinear();
                        list.add(new PostEffectPass.TargetSampler(string3, (Identifier)identifier2, bl2, bl3));
                    }
                    catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                    continue block9;
                }
            }
            break;
        }
        throw new MatchException(null, null);
    }

    public void render(FrameGraphBuilder builder, int textureWidth, int textureHeight, FramebufferSet framebufferSet) {
        GpuBufferSlice gpuBufferSlice = this.projectionMatrix.set(textureWidth, textureHeight);
        HashMap<Identifier, Handle<Framebuffer>> map = new HashMap<Identifier, Handle<Framebuffer>>(this.internalTargets.size() + this.externalTargets.size());
        for (Identifier identifier : this.externalTargets) {
            map.put(identifier, framebufferSet.getOrThrow(identifier));
        }
        for (Map.Entry entry : this.internalTargets.entrySet()) {
            Identifier identifier2 = (Identifier)entry.getKey();
            PostEffectPipeline.Targets targets = (PostEffectPipeline.Targets)entry.getValue();
            SimpleFramebufferFactory simpleFramebufferFactory = new SimpleFramebufferFactory(targets.width().orElse(textureWidth), targets.height().orElse(textureHeight), true, targets.clearColor());
            if (targets.persistent()) {
                Framebuffer framebuffer = this.createFramebuffer(identifier2, simpleFramebufferFactory);
                map.put(identifier2, builder.createObjectNode(identifier2.toString(), framebuffer));
                continue;
            }
            map.put(identifier2, builder.createResourceHandle(identifier2.toString(), simpleFramebufferFactory));
        }
        for (PostEffectPass postEffectPass : this.passes) {
            postEffectPass.render(builder, map, gpuBufferSlice);
        }
        for (Identifier identifier : this.externalTargets) {
            framebufferSet.set(identifier, (Handle)map.get(identifier));
        }
    }

    @Deprecated
    public void render(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
        FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
        FramebufferSet framebufferSet = FramebufferSet.singleton(MAIN, frameGraphBuilder.createObjectNode("main", framebuffer));
        this.render(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
        frameGraphBuilder.run(objectAllocator);
    }

    private Framebuffer createFramebuffer(Identifier id, SimpleFramebufferFactory factory) {
        Framebuffer framebuffer = this.framebuffers.get(id);
        if (framebuffer == null || framebuffer.textureWidth != factory.width() || framebuffer.textureHeight != factory.height()) {
            if (framebuffer != null) {
                framebuffer.delete();
            }
            framebuffer = factory.net_minecraft_client_gl_Framebuffer_create();
            factory.prepare(framebuffer);
            this.framebuffers.put(id, framebuffer);
        }
        return framebuffer;
    }

    @Override
    public void close() {
        this.framebuffers.values().forEach(Framebuffer::delete);
        this.framebuffers.clear();
        for (PostEffectPass postEffectPass : this.passes) {
            postEffectPass.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface FramebufferSet {
        public static FramebufferSet singleton(final Identifier id, final Handle<Framebuffer> framebuffer) {
            return new FramebufferSet(){
                private Handle<Framebuffer> framebuffer;
                {
                    this.framebuffer = framebuffer;
                }

                @Override
                public void set(Identifier id2, Handle<Framebuffer> framebuffer2) {
                    if (!id2.equals(id)) {
                        throw new IllegalArgumentException("No target with id " + String.valueOf(id2));
                    }
                    this.framebuffer = framebuffer2;
                }

                @Override
                @Nullable
                public Handle<Framebuffer> get(Identifier id2) {
                    return id2.equals(id) ? this.framebuffer : null;
                }
            };
        }

        public void set(Identifier var1, Handle<Framebuffer> var2);

        @Nullable
        public Handle<Framebuffer> get(Identifier var1);

        default public Handle<Framebuffer> getOrThrow(Identifier id) {
            Handle<Framebuffer> handle = this.get(id);
            if (handle == null) {
                throw new IllegalArgumentException("Missing target with id " + String.valueOf(id));
            }
            return handle;
        }
    }
}

