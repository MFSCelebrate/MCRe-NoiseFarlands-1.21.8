/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.GeometryBakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.AsyncHelper;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelBaker {
    final static public SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_0"));
    final static public SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_1"));
    final static public SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/lava_flow"));
    final static public SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_flow"));
    final static public SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_overlay"));
    final static public SpriteIdentifier BANNER_BASE = new SpriteIdentifier(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/banner_base"));
    final static public SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base"));
    final static public SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base_nopattern"));
    final static public int MAX_BLOCK_DESTRUCTION_STAGE = 10;
    final static public List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(stage -> Identifier.ofVanilla("block/destroy_stage_" + stage)).collect(Collectors.toList());
    final static public List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(id -> id.withPath(path -> "textures/" + path + ".png")).collect(Collectors.toList());
    final static public List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getBlockBreaking).collect(Collectors.toList());
    final static Logger LOGGER = LogUtils.getLogger();
    final private LoadedEntityModels entityModels;
    final private Map<BlockState, BlockStateModel.UnbakedGrouped> blockModels;
    final private Map<Identifier, ItemAsset> itemAssets;
    final Map<Identifier, BakedSimpleModel> simpleModels;
    final BakedSimpleModel missingModel;

    public ModelBaker(LoadedEntityModels entityModels, Map<BlockState, BlockStateModel.UnbakedGrouped> blockModels, Map<Identifier, ItemAsset> itemModels, Map<Identifier, BakedSimpleModel> simpleModels, BakedSimpleModel missingModel) {
        this.entityModels = entityModels;
        this.blockModels = blockModels;
        this.itemAssets = itemModels;
        this.simpleModels = simpleModels;
        this.missingModel = missingModel;
    }

    public CompletableFuture<BakedModels> bake(ErrorCollectingSpriteGetter spriteGetter, Executor executor) {
        BlockItemModels blockItemModels = BlockItemModels.bake(this.missingModel, spriteGetter);
        BakerImpl bakerImpl = new BakerImpl(spriteGetter);
        CompletableFuture<Map<BlockState, BlockStateModel>> completableFuture = AsyncHelper.mapValues(this.blockModels, (state, unbaked) -> {
            try {
                return unbaked.bake((BlockState)state, bakerImpl);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", state, (Object)exception);
                return null;
            }
        }, executor);
        CompletableFuture<Map<Identifier, ItemModel>> completableFuture2 = AsyncHelper.mapValues(this.itemAssets, (state, asset) -> {
            try {
                return asset.model().bake(new ItemModel.BakeContext(bakerImpl, this.entityModels, blockItemModels.item, asset.registrySwapper()));
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake item model: '{}'", state, (Object)exception);
                return null;
            }
        }, executor);
        HashMap map = new HashMap(this.itemAssets.size());
        this.itemAssets.forEach((id, asset) -> {
            ItemAsset.Properties properties = asset.properties();
            if (!properties.equals(ItemAsset.Properties.DEFAULT)) {
                map.put(id, properties);
            }
        });
        return completableFuture.thenCombine(completableFuture2, (blockStateModels, itemModels) -> new BakedModels(blockItemModels, (Map<BlockState, BlockStateModel>)blockStateModels, (Map<Identifier, ItemModel>)itemModels, map));
    }

    @Environment(value=EnvType.CLIENT)
    public static final class BlockItemModels
    extends Record {
        final private BlockStateModel block;
        final ItemModel item;

        public BlockItemModels(BlockStateModel blockStateModel, ItemModel itemModel) {
            this.block = blockStateModel;
            this.item = itemModel;
        }

        public static BlockItemModels bake(BakedSimpleModel model, final ErrorCollectingSpriteGetter errorCollectingSpriteGetter) {
            Baker baker = new Baker(){

                @Override
                public BakedSimpleModel getModel(Identifier id) {
                    throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(id));
                }

                @Override
                public <T> T compute(Baker.ResolvableCacheKey<T> key) {
                    return key.compute(this);
                }

                @Override
                public ErrorCollectingSpriteGetter getSpriteGetter() {
                    return errorCollectingSpriteGetter;
                }
            };
            ModelTextures modelTextures = model.getTextures();
            boolean bl = model.getAmbientOcclusion();
            boolean bl2 = model.getGuiLight().isSide();
            ModelTransformation modelTransformation = model.getTransformations();
            BakedGeometry bakedGeometry = model.bakeGeometry(modelTextures, baker, ModelRotation.X0_Y0);
            Sprite sprite = model.getParticleTexture(modelTextures, baker);
            SimpleBlockStateModel blockStateModel = new SimpleBlockStateModel(new GeometryBakedModel(bakedGeometry, bl, sprite));
            MissingItemModel itemModel = new MissingItemModel(bakedGeometry.getAllQuads(), new ModelSettings(bl2, sprite, modelTransformation));
            return new BlockItemModels(blockStateModel, itemModel);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockItemModels.class, "block;item", "block", "item"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockItemModels.class, "block;item", "block", "item"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockItemModels.class, "block;item", "block", "item"}, this, object);
        }

        public BlockStateModel block() {
            return this.block;
        }

        public ItemModel item() {
            return this.item;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BakerImpl
    implements Baker {
        final private ErrorCollectingSpriteGetter spriteGetter;
        final private Map<Baker.ResolvableCacheKey<Object>, Object> cache = new ConcurrentHashMap<Baker.ResolvableCacheKey<Object>, Object>();
        final private Function<Baker.ResolvableCacheKey<Object>, Object> cacheValueFunction = key -> key.compute(this);

        BakerImpl(ErrorCollectingSpriteGetter spriteGetter) {
            this.spriteGetter = spriteGetter;
        }

        @Override
        public ErrorCollectingSpriteGetter getSpriteGetter() {
            return this.spriteGetter;
        }

        @Override
        public BakedSimpleModel getModel(Identifier id) {
            BakedSimpleModel bakedSimpleModel = ModelBaker.this.simpleModels.get(id);
            if (bakedSimpleModel == null) {
                LOGGER.warn("Requested a model that was not discovered previously: {}", (Object)id);
                return ModelBaker.this.missingModel;
            }
            return bakedSimpleModel;
        }

        @Override
        public <T> T compute(Baker.ResolvableCacheKey<T> key) {
            return (T)this.cache.computeIfAbsent(key, this.cacheValueFunction);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BakedModels(BlockItemModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<Identifier, ItemModel> itemStackModels, Map<Identifier, ItemAsset.Properties> itemProperties) {
    }
}

