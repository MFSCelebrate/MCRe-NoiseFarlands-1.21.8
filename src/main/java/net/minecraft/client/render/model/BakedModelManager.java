/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.item.ItemAssetsLoader;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.ModelGrouper;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.GeneratedItemModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.atlas.Atlases;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
implements ResourceReloader,
AutoCloseable,
FabricBakedModelManager {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private ResourceFinder MODELS_FINDER = ResourceFinder.json("models");
    final static private Map<Identifier, Identifier> LAYERS_TO_LOADERS = Map.of(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Atlases.BANNER_PATTERNS, TexturedRenderLayers.BEDS_ATLAS_TEXTURE, Atlases.BEDS, TexturedRenderLayers.CHEST_ATLAS_TEXTURE, Atlases.CHESTS, TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Atlases.SHIELD_PATTERNS, TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, Atlases.SIGNS, TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, Atlases.SHULKER_BOXES, TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE, Atlases.ARMOR_TRIMS, TexturedRenderLayers.DECORATED_POT_ATLAS_TEXTURE, Atlases.DECORATED_POT, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Atlases.BLOCKS);
    private Map<Identifier, ItemModel> bakedItemModels = Map.of();
    private Map<Identifier, ItemAsset.Properties> itemProperties = Map.of();
    final private SpriteAtlasManager atlasManager;
    final private BlockModels blockModelCache;
    final private BlockColors colorMap;
    private LoadedEntityModels entityModels = LoadedEntityModels.EMPTY;
    private LoadedBlockEntityModels blockEntityModels = LoadedBlockEntityModels.EMPTY;
    private int mipmapLevels;
    private ModelBaker.BlockItemModels missingModels;
    private Object2IntMap<BlockState> modelGroups = Object2IntMaps.emptyMap();

    public BakedModelManager(TextureManager textureManager, BlockColors colorMap, int mipmapLevels) {
        this.colorMap = colorMap;
        this.mipmapLevels = mipmapLevels;
        this.blockModelCache = new BlockModels(this);
        this.atlasManager = new SpriteAtlasManager(LAYERS_TO_LOADERS, textureManager);
    }

    public BlockStateModel getMissingModel() {
        return this.missingModels.block();
    }

    public ItemModel getItemModel(Identifier id) {
        return this.bakedItemModels.getOrDefault(id, this.missingModels.item());
    }

    public ItemAsset.Properties getItemProperties(Identifier id) {
        return this.itemProperties.getOrDefault(id, ItemAsset.Properties.DEFAULT);
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    public final CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
        CompletableFuture<LoadedEntityModels> completableFuture = CompletableFuture.supplyAsync(LoadedEntityModels::copy, executor);
        CompletionStage completableFuture2 = completableFuture.thenApplyAsync(LoadedBlockEntityModels::fromModels, executor);
        CompletableFuture<Map<Identifier, UnbakedModel>> completableFuture3 = BakedModelManager.reloadModels(resourceManager, executor);
        CompletableFuture<BlockStatesLoader.LoadedModels> completableFuture4 = BlockStatesLoader.load(resourceManager, executor);
        CompletableFuture<ItemAssetsLoader.Result> completableFuture5 = ItemAssetsLoader.load(resourceManager, executor);
        CompletionStage completableFuture6 = CompletableFuture.allOf(completableFuture3, completableFuture4, completableFuture5).thenApplyAsync(async -> BakedModelManager.collect((Map)completableFuture3.join(), (BlockStatesLoader.LoadedModels)completableFuture4.join(), (ItemAssetsLoader.Result)completableFuture5.join()), executor);
        CompletionStage completableFuture7 = completableFuture4.thenApplyAsync(definition -> BakedModelManager.group(this.colorMap, definition), executor);
        Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>> map = this.atlasManager.reload(resourceManager, this.mipmapLevels, executor);
        return ((CompletableFuture)((CompletableFuture)((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])Stream.concat(map.values().stream(), Stream.of(completableFuture6, completableFuture7, completableFuture4, completableFuture5, completableFuture, completableFuture2, completableFuture3)).toArray(CompletableFuture[]::new)).thenComposeAsync(arg_0 -> BakedModelManager.method_65753(map, (CompletableFuture)completableFuture6, (CompletableFuture)completableFuture7, completableFuture3, completableFuture, completableFuture4, completableFuture5, (CompletableFuture)completableFuture2, executor, arg_0), executor)).thenCompose(result -> result.readyForUpload.thenApply(void_ -> result))).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(bakingResult -> this.upload((BakingResult)bakingResult, Profilers.get()), executor2);
    }

    private static CompletableFuture<Map<Identifier, UnbakedModel>> reloadModels(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> MODELS_FINDER.findResources(resourceManager), executor).thenCompose(models2 -> {
            ArrayList<CompletableFuture<Pair>> list = new ArrayList<CompletableFuture<Pair>>(models2.size());
            for (Map.Entry entry : models2.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    Pair pair;
                    block8: {
                        Identifier identifier = MODELS_FINDER.toResourceId((Identifier)entry.getKey());
                        BufferedReader reader = ((Resource)entry.getValue()).getReader();
                        try {
                            pair = Pair.of((Object)identifier, (Object)JsonUnbakedModel.deserialize(reader));
                            if (reader == null) break block8;
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
                            catch (Exception exception) {
                                LOGGER.error("Failed to load model {}", entry.getKey(), (Object)exception);
                                return null;
                            }
                        }
                        ((Reader)reader).close();
                    }
                    return pair;
                }, executor));
            }
            return Util.combineSafe(list).thenApply(models -> models.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private static Models collect(Map<Identifier, UnbakedModel> modelMap, BlockStatesLoader.LoadedModels stateDefinition, ItemAssetsLoader.Result result) {
        ScopedProfiler scopedProfiler = Profilers.get().scoped("dependencies");
        try {
            ReferencedModelsCollector referencedModelsCollector = new ReferencedModelsCollector(modelMap, MissingModel.create());
            referencedModelsCollector.addSpecialModel(GeneratedItemModel.GENERATED, new GeneratedItemModel());
            stateDefinition.models().values().forEach(referencedModelsCollector::resolve);
            result.contents().values().forEach(asset -> referencedModelsCollector.resolve(asset.model()));
            Models models = new Models(referencedModelsCollector.getMissingModel(), referencedModelsCollector.collectModels());
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
            return models;
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    private static CompletableFuture<BakingResult> bake(final Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasMap, ModelBaker baker, Object2IntMap<BlockState> blockStates, LoadedEntityModels entityModels, LoadedBlockEntityModels blockEntityModels, Executor executor) {
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf((CompletableFuture[])atlasMap.values().stream().map(SpriteAtlasManager.AtlasPreparation::whenComplete).toArray(CompletableFuture[]::new));
        final Multimap multimap = Multimaps.synchronizedMultimap((Multimap)HashMultimap.create());
        final Multimap multimap2 = Multimaps.synchronizedMultimap((Multimap)HashMultimap.create());
        return baker.bake(new ErrorCollectingSpriteGetter(){
            final private Sprite missingSprite;
            {
                this.missingSprite = ((SpriteAtlasManager.AtlasPreparation)atlasMap.get(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).getMissingSprite();
            }

            @Override
            public Sprite get(SpriteIdentifier id, SimpleModel model) {
                SpriteAtlasManager.AtlasPreparation atlasPreparation = (SpriteAtlasManager.AtlasPreparation)atlasMap.get(id.getAtlasId());
                Sprite sprite = atlasPreparation.getSprite(id.getTextureId());
                if (sprite != null) {
                    return sprite;
                }
                multimap.put((Object)model.name(), (Object)id);
                return atlasPreparation.getMissingSprite();
            }

            @Override
            public Sprite getMissing(String name, SimpleModel model) {
                multimap2.put((Object)model.name(), (Object)name);
                return this.missingSprite;
            }
        }, executor).thenApply(models -> {
            multimap.asMap().forEach((modelName, sprites) -> LOGGER.warn("Missing textures in model {}:\n{}", modelName, (Object)sprites.stream().sorted(SpriteIdentifier.COMPARATOR).map(spriteId -> "    " + String.valueOf(spriteId.getAtlasId()) + ":" + String.valueOf(spriteId.getTextureId())).collect(Collectors.joining("\n"))));
            multimap2.asMap().forEach((modelName, textureIds) -> LOGGER.warn("Missing texture references in model {}:\n{}", modelName, (Object)textureIds.stream().sorted().map(string -> "    " + string).collect(Collectors.joining("\n"))));
            Map<BlockState, BlockStateModel> map2 = BakedModelManager.toStateMap(models.blockStateModels(), models.missingModels().block());
            return new BakingResult((ModelBaker.BakedModels)models, blockStates, map2, atlasMap, entityModels, blockEntityModels, completableFuture);
        });
    }

    private static Map<BlockState, BlockStateModel> toStateMap(Map<BlockState, BlockStateModel> blockStateModels, BlockStateModel missingModel) {
        ScopedProfiler scopedProfiler = Profilers.get().scoped("block state dispatch");
        try {
            IdentityHashMap<BlockState, BlockStateModel> map = new IdentityHashMap<BlockState, BlockStateModel>(blockStateModels);
            for (Block block : Registries.BLOCK) {
                block.getStateManager().getStates().forEach(state -> {
                    if (blockStateModels.putIfAbsent((BlockState)state, missingModel) == null) {
                        LOGGER.warn("Missing model for variant: '{}'", state);
                    }
                });
            }
            IdentityHashMap<BlockState, BlockStateModel> identityHashMap = map;
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
            return identityHashMap;
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    private static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.LoadedModels definition) {
        ScopedProfiler scopedProfiler = Profilers.get().scoped("block groups");
        try {
            Object2IntMap<BlockState> object2IntMap = ModelGrouper.group(colors, definition);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
            return object2IntMap;
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    private void upload(BakingResult bakingResult, Profiler profiler) {
        profiler.push("upload");
        bakingResult.atlasPreparations.values().forEach(SpriteAtlasManager.AtlasPreparation::upload);
        ModelBaker.BakedModels bakedModels = bakingResult.bakedModels;
        this.bakedItemModels = bakedModels.itemStackModels();
        this.itemProperties = bakedModels.itemProperties();
        this.modelGroups = bakingResult.modelGroups;
        this.missingModels = bakedModels.missingModels();
        profiler.swap("cache");
        this.blockModelCache.setModels(bakingResult.modelCache);
        this.blockEntityModels = bakingResult.specialBlockModelRenderer;
        this.entityModels = bakingResult.entityModelSet;
        profiler.pop();
    }

    public boolean shouldRerender(BlockState from, BlockState to) {
        int j;
        if (from == to) {
            return false;
        }
        int i = this.modelGroups.getInt((Object)from);
        if (1 != -1 && 1 == (j = this.modelGroups.getInt((Object)to))) {
            FluidState fluidState2;
            FluidState fluidState = from.getFluidState();
            return fluidState != (fluidState2 = to.getFluidState());
        }
        return true;
    }

    public SpriteAtlasTexture getAtlas(Identifier id) {
        return this.atlasManager.getAtlas(id);
    }

    @Override
    public void close() {
        this.atlasManager.close();
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.mipmapLevels = mipmapLevels;
    }

    public Supplier<LoadedBlockEntityModels> getBlockEntityModelsSupplier() {
        return () -> this.blockEntityModels;
    }

    public Supplier<LoadedEntityModels> getEntityModelsSupplier() {
        return () -> this.entityModels;
    }

    private static CompletionStage method_65753(Map map, CompletableFuture completableFuture, CompletableFuture completableFuture2, CompletableFuture completableFuture3, CompletableFuture completableFuture4, CompletableFuture completableFuture5, CompletableFuture completableFuture6, CompletableFuture completableFuture7, Executor executor, Void async) {
        Map<Identifier, SpriteAtlasManager.AtlasPreparation> map2 = Util.transformMapValues(map, CompletableFuture::join);
        Models models = (Models)completableFuture.join();
        Object2IntMap object2IntMap = (Object2IntMap)completableFuture2.join();
        Sets.SetView set = Sets.difference(((Map)completableFuture3.join()).keySet(), models.models.keySet());
        if (!set.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", (Object)set.stream().sorted().map(id -> "\t" + String.valueOf(id) + "\n").collect(Collectors.joining()));
        }
        ModelBaker modelBaker = new ModelBaker((LoadedEntityModels)completableFuture4.join(), ((BlockStatesLoader.LoadedModels)completableFuture5.join()).models(), ((ItemAssetsLoader.Result)completableFuture6.join()).contents(), models.models(), models.missing());
        return BakedModelManager.bake(map2, modelBaker, (Object2IntMap<BlockState>)object2IntMap, (LoadedEntityModels)completableFuture4.join(), (LoadedBlockEntityModels)completableFuture7.join(), executor);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Models
    extends Record {
        final private BakedSimpleModel missing;
        final Map<Identifier, BakedSimpleModel> models;

        Models(BakedSimpleModel bakedSimpleModel, Map<Identifier, BakedSimpleModel> map) {
            this.missing = bakedSimpleModel;
            this.models = map;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Models.class, "missing;models", "missing", "models"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Models.class, "missing;models", "missing", "models"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Models.class, "missing;models", "missing", "models"}, this, object);
        }

        public BakedSimpleModel missing() {
            return this.missing;
        }

        public Map<Identifier, BakedSimpleModel> models() {
            return this.models;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class BakingResult
    extends Record {
        final ModelBaker.BakedModels bakedModels;
        final Object2IntMap<BlockState> modelGroups;
        final Map<BlockState, BlockStateModel> modelCache;
        final Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasPreparations;
        final LoadedEntityModels entityModelSet;
        final LoadedBlockEntityModels specialBlockModelRenderer;
        final CompletableFuture<Void> readyForUpload;

        BakingResult(ModelBaker.BakedModels bakedModels, Object2IntMap<BlockState> object2IntMap, Map<BlockState, BlockStateModel> map, Map<Identifier, SpriteAtlasManager.AtlasPreparation> map2, LoadedEntityModels loadedEntityModels, LoadedBlockEntityModels loadedBlockEntityModels, CompletableFuture<Void> completableFuture) {
            this.bakedModels = bakedModels;
            this.modelGroups = object2IntMap;
            this.modelCache = map;
            this.atlasPreparations = map2;
            this.entityModelSet = loadedEntityModels;
            this.specialBlockModelRenderer = loadedBlockEntityModels;
            this.readyForUpload = completableFuture;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakingResult.class, "bakedModels;modelGroups;modelCache;atlasPreparations;entityModelSet;specialBlockModelRenderer;readyForUpload", "bakedModels", "modelGroups", "modelCache", "atlasPreparations", "entityModelSet", "specialBlockModelRenderer", "readyForUpload"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakingResult.class, "bakedModels;modelGroups;modelCache;atlasPreparations;entityModelSet;specialBlockModelRenderer;readyForUpload", "bakedModels", "modelGroups", "modelCache", "atlasPreparations", "entityModelSet", "specialBlockModelRenderer", "readyForUpload"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakingResult.class, "bakedModels;modelGroups;modelCache;atlasPreparations;entityModelSet;specialBlockModelRenderer;readyForUpload", "bakedModels", "modelGroups", "modelCache", "atlasPreparations", "entityModelSet", "specialBlockModelRenderer", "readyForUpload"}, this, object);
        }

        public ModelBaker.BakedModels bakedModels() {
            return this.bakedModels;
        }

        public Object2IntMap<BlockState> modelGroups() {
            return this.modelGroups;
        }

        public Map<BlockState, BlockStateModel> modelCache() {
            return this.modelCache;
        }

        public Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasPreparations() {
            return this.atlasPreparations;
        }

        public LoadedEntityModels entityModelSet() {
            return this.entityModelSet;
        }

        public LoadedBlockEntityModels specialBlockModelRenderer() {
            return this.specialBlockModelRenderer;
        }

        public CompletableFuture<Void> readyForUpload() {
            return this.readyForUpload;
        }
    }
}

