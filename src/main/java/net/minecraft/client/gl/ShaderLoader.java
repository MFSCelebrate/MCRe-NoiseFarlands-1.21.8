/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlImportProcessor;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ShaderLoader
extends SinglePreparationResourceReloader<Definitions>
implements AutoCloseable {
    final static Logger LOGGER = LogUtils.getLogger();
    final static public int field_53936 = 32768;
    final static public String SHADERS_PATH = "shaders";
    final static private String INCLUDE_PATH = "shaders/include/";
    final static private ResourceFinder POST_EFFECT_FINDER = ResourceFinder.json("post_effect");
    final TextureManager textureManager;
    final private Consumer<Exception> onError;
    private Cache cache = new Cache(Definitions.EMPTY);
    final ProjectionMatrix2 projectionMatrix = new ProjectionMatrix2("post", 0.1f, 1000.0f, false);

    public ShaderLoader(TextureManager textureManager, Consumer<Exception> onError) {
        this.textureManager = textureManager;
        this.onError = onError;
    }

    @Override
    protected Definitions net_minecraft_client_gl_ShaderLoader$Definitions_prepare(ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        Map<Identifier, Resource> map = resourceManager.findResources(SHADERS_PATH, ShaderLoader::isShaderSource);
        for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
            Identifier identifier = entry.getKey();
            ShaderType shaderType = ShaderType.byLocation(identifier);
            if (shaderType == null) continue;
            ShaderLoader.loadShaderSource(identifier, entry.getValue(), shaderType, map, (ImmutableMap.Builder<ShaderSourceKey, String>)builder);
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        for (Map.Entry<Identifier, Resource> entry2 : POST_EFFECT_FINDER.findResources(resourceManager).entrySet()) {
            ShaderLoader.loadPostEffect(entry2.getKey(), entry2.getValue(), (ImmutableMap.Builder<Identifier, PostEffectPipeline>)builder2);
        }
        return new Definitions((Map<ShaderSourceKey, String>)builder.build(), (Map<Identifier, PostEffectPipeline>)builder2.build());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void loadShaderSource(Identifier id, Resource resource, ShaderType type, Map<Identifier, Resource> allResources, ImmutableMap.Builder<ShaderSourceKey, String> builder) {
        Identifier identifier = type.idConverter().toResourceId(id);
        GlImportProcessor glImportProcessor = ShaderLoader.createImportProcessor(allResources, id);
        try {
            try (BufferedReader reader = resource.getReader();){
                String string = IOUtils.toString((Reader)reader);
                builder.put((Object)new ShaderSourceKey(identifier, type), (Object)String.join((CharSequence)"", glImportProcessor.readSource(string)));
                if (reader == null) return;
            }
            return;
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to load shader source at {}", (Object)id, (Object)iOException);
        }
    }

    private static GlImportProcessor createImportProcessor(final Map<Identifier, Resource> allResources, Identifier id) {
        final Identifier identifier = id.withPath(PathUtil::getPosixFullPath);
        return new GlImportProcessor(){
            final private Set<Identifier> processed = new ObjectArraySet();

            @Override
            public String loadImport(boolean inline, String name) {
                String string;
                block11: {
                    Identifier identifier2;
                    try {
                        identifier2 = inline ? identifier.withPath(path -> PathUtil.normalizeToPosix(path + name)) : Identifier.of(name).withPrefixedPath(ShaderLoader.INCLUDE_PATH);
                    }
                    catch (InvalidIdentifierException invalidIdentifierException) {
                        LOGGER.error("Malformed GLSL import {}: {}", (Object)name, (Object)invalidIdentifierException.getMessage());
                        return "#error " + invalidIdentifierException.getMessage();
                    }
                    if (!this.processed.add(identifier2)) {
                        return null;
                    }
                    BufferedReader reader = ((Resource)allResources.get(identifier2)).getReader();
                    try {
                        string = IOUtils.toString((Reader)reader);
                        if (reader == null) break block11;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    ((Reader)reader).close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (IOException iOException) {
                            LOGGER.error("Could not open GLSL import {}: {}", (Object)identifier2, (Object)iOException.getMessage());
                            return "#error " + iOException.getMessage();
                        }
                    }
                    ((Reader)reader).close();
                }
                return string;
            }
        };
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void loadPostEffect(Identifier id, Resource resource, ImmutableMap.Builder<Identifier, PostEffectPipeline> builder) {
        Identifier identifier = POST_EFFECT_FINDER.toResourceId(id);
        try {
            try (BufferedReader reader = resource.getReader();){
                JsonElement jsonElement = StrictJsonParser.parse(reader);
                builder.put((Object)identifier, (Object)((PostEffectPipeline)PostEffectPipeline.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonSyntaxException::new)));
                if (reader == null) return;
            }
            return;
        }
        catch (JsonParseException | IOException exception) {
            LOGGER.error("Failed to parse post chain at {}", (Object)id, (Object)exception);
        }
    }

    private static boolean isShaderSource(Identifier id) {
        return ShaderType.byLocation(id) != null || id.getPath().endsWith(".glsl");
    }

    @Override
    protected void apply(Definitions definitions, ResourceManager resourceManager, Profiler profiler) {
        Cache cache = new Cache(definitions);
        HashSet<RenderPipeline> set = new HashSet<RenderPipeline>(RenderPipelines.getAll());
        ArrayList<Identifier> list = new ArrayList<Identifier>();
        GpuDevice gpuDevice = RenderSystem.getDevice();
        gpuDevice.clearPipelineCache();
        for (RenderPipeline renderPipeline : set) {
            CompiledRenderPipeline compiledRenderPipeline = gpuDevice.com_mojang_blaze3d_pipeline_CompiledRenderPipeline_precompilePipeline(renderPipeline, cache::getSource);
            if (compiledRenderPipeline.isValid()) continue;
            list.add(renderPipeline.getLocation());
        }
        if (!list.isEmpty()) {
            gpuDevice.clearPipelineCache();
            throw new RuntimeException("Failed to load required shader programs:\n" + list.stream().map(identifier -> " - " + String.valueOf(identifier)).collect(Collectors.joining("\n")));
        }
        this.cache.close();
        this.cache = cache;
    }

    @Override
    public String getName() {
        return "Shader Loader";
    }

    private void handleError(Exception exception) {
        if (this.cache.errorHandled) {
            return;
        }
        this.onError.accept(exception);
        this.cache.errorHandled = true;
    }

    @Nullable
    public PostEffectProcessor loadPostEffect(Identifier id, Set<Identifier> availableExternalTargets) {
        try {
            return this.cache.getOrLoadProcessor(id, availableExternalTargets);
        }
        catch (LoadException loadException) {
            LOGGER.error("Failed to load post chain: {}", (Object)id, (Object)loadException);
            this.cache.postEffectProcessors.put(id, Optional.empty());
            this.handleError(loadException);
            return null;
        }
    }

    @Override
    public void close() {
        this.cache.close();
        this.projectionMatrix.close();
    }

    public String getSource(Identifier id, ShaderType type) {
        return this.cache.getSource(id, type);
    }

    @Override
    protected Object java_lang_Object_prepare(ResourceManager manager, Profiler profiler) {
        return this.net_minecraft_client_gl_ShaderLoader$Definitions_prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    class Cache
    implements AutoCloseable {
        final private Definitions definitions;
        final Map<Identifier, Optional<PostEffectProcessor>> postEffectProcessors = new HashMap<Identifier, Optional<PostEffectProcessor>>();
        boolean errorHandled;

        Cache(Definitions definitions) {
            this.definitions = definitions;
        }

        @Nullable
        public PostEffectProcessor getOrLoadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws LoadException {
            Optional<PostEffectProcessor> optional = this.postEffectProcessors.get(id);
            if (optional != null) {
                return optional.orElse(null);
            }
            PostEffectProcessor postEffectProcessor = this.loadProcessor(id, availableExternalTargets);
            this.postEffectProcessors.put(id, Optional.of(postEffectProcessor));
            return postEffectProcessor;
        }

        private PostEffectProcessor loadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws LoadException {
            PostEffectPipeline postEffectPipeline = this.definitions.postChains.get(id);
            if (postEffectPipeline == null) {
                throw new LoadException("Could not find post chain with id: " + String.valueOf(id));
            }
            return PostEffectProcessor.parseEffect(postEffectPipeline, ShaderLoader.this.textureManager, availableExternalTargets, id, ShaderLoader.this.projectionMatrix);
        }

        @Override
        public void close() {
            this.postEffectProcessors.values().forEach(processor -> processor.ifPresent(PostEffectProcessor::close));
            this.postEffectProcessors.clear();
        }

        public String getSource(Identifier id, ShaderType type) {
            return this.definitions.shaderSources.get(new ShaderSourceKey(id, type));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Definitions
    extends Record {
        final Map<ShaderSourceKey, String> shaderSources;
        final Map<Identifier, PostEffectPipeline> postChains;
        final static public Definitions EMPTY = new Definitions(Map.of(), Map.of());

        public Definitions(Map<ShaderSourceKey, String> map, Map<Identifier, PostEffectPipeline> map2) {
            this.shaderSources = map;
            this.postChains = map2;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Definitions.class, "shaderSources;postChains", "shaderSources", "postChains"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Definitions.class, "shaderSources;postChains", "shaderSources", "postChains"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Definitions.class, "shaderSources;postChains", "shaderSources", "postChains"}, this, object);
        }

        public Map<ShaderSourceKey, String> shaderSources() {
            return this.shaderSources;
        }

        public Map<Identifier, PostEffectPipeline> postChains() {
            return this.postChains;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record ShaderSourceKey(Identifier id, ShaderType type) {
        @Override
        public String toString() {
            return String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LoadException
    extends Exception {
        public LoadException(String message) {
            super(message);
        }
    }
}

