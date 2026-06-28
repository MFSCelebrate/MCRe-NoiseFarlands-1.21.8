/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ModelProvider
implements DataProvider {
    final private DataOutput.PathResolver blockstatesPathResolver;
    final private DataOutput.PathResolver itemsPathResolver;
    final private DataOutput.PathResolver modelsPathResolver;

    public ModelProvider(DataOutput output) {
        this.blockstatesPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "blockstates");
        this.itemsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "items");
        this.modelsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        ItemAssets itemAssets = new ItemAssets();
        BlockStateSuppliers blockStateSuppliers = new BlockStateSuppliers();
        ModelSuppliers modelSuppliers = new ModelSuppliers();
        new BlockStateModelGenerator(blockStateSuppliers, itemAssets, modelSuppliers).register();
        new ItemModelGenerator(itemAssets, modelSuppliers).register();
        blockStateSuppliers.validate();
        itemAssets.resolveAndValidate();
        return CompletableFuture.allOf(blockStateSuppliers.writeAllToPath(writer, this.blockstatesPathResolver), modelSuppliers.writeAllToPath(writer, this.modelsPathResolver), itemAssets.writeAllToPath(writer, this.itemsPathResolver));
    }

    @Override
    public String getName() {
        return "Model Definitions";
    }

    @Environment(value=EnvType.CLIENT)
    static class ItemAssets
    implements ItemModelOutput {
        final private Map<Item, ItemAsset> itemAssets = new HashMap<Item, ItemAsset>();
        final private Map<Item, Item> aliasedAssets = new HashMap<Item, Item>();

        ItemAssets() {
        }

        @Override
        public void accept(Item item, ItemModel.Unbaked model) {
            this.accept(item, new ItemAsset(model, ItemAsset.Properties.DEFAULT));
        }

        private void accept(Item item, ItemAsset asset) {
            ItemAsset itemAsset = this.itemAssets.put(item, asset);
            if (itemAsset != null) {
                throw new IllegalStateException("Duplicate item model definition for " + String.valueOf(item));
            }
        }

        @Override
        public void acceptAlias(Item base, Item alias) {
            this.aliasedAssets.put(alias, base);
        }

        public void resolveAndValidate() {
            Registries.ITEM.forEach(item -> {
                BlockItem blockItem;
                if (this.aliasedAssets.containsKey(item)) {
                    return;
                }
                if (item instanceof BlockItem && !this.itemAssets.containsKey(blockItem = (BlockItem)item)) {
                    Identifier identifier = ModelIds.getBlockModelId(blockItem.getBlock());
                    this.accept((Item)blockItem, ItemModels.basic(identifier));
                }
            });
            this.aliasedAssets.forEach((base, alias) -> {
                ItemAsset itemAsset = this.itemAssets.get(alias);
                if (itemAsset == null) {
                    throw new IllegalStateException("Missing donor: " + String.valueOf(alias) + " -> " + String.valueOf(base));
                }
                this.accept((Item)base, itemAsset);
            });
            List<Identifier> list = Registries.ITEM.streamEntries().filter(entry -> !this.itemAssets.containsKey(entry.value())).map(entryx -> entryx.registryKey().getValue()).toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + String.valueOf(list));
            }
        }

        public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
            return DataProvider.writeAllToPath(writer, ItemAsset.CODEC, item -> pathResolver.resolveJson(item.getRegistryEntry().registryKey().getValue()), this.itemAssets);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlockStateSuppliers
    implements Consumer<BlockModelDefinitionCreator> {
        final private Map<Block, BlockModelDefinitionCreator> blockStateSuppliers = new HashMap<Block, BlockModelDefinitionCreator>();

        BlockStateSuppliers() {
        }

        @Override
        public void accept(BlockModelDefinitionCreator blockModelDefinitionCreator) {
            Block block = blockModelDefinitionCreator.getBlock();
            BlockModelDefinitionCreator blockModelDefinitionCreator2 = this.blockStateSuppliers.put(block, blockModelDefinitionCreator);
            if (blockModelDefinitionCreator2 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf(block));
            }
        }

        public void validate() {
            Stream<RegistryEntry.Reference> stream = Registries.BLOCK.streamEntries().filter(entry -> true);
            List<Identifier> list = stream.filter(entry -> !this.blockStateSuppliers.containsKey(entry.value())).map(entryx -> entryx.registryKey().getValue()).toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf(list));
            }
        }

        public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
            Map map = Maps.transformValues(this.blockStateSuppliers, BlockModelDefinitionCreator::createBlockModelDefinition);
            Function<Block, Path> function = block -> pathResolver.resolveJson(block.getRegistryEntry().registryKey().getValue());
            return DataProvider.writeAllToPath(writer, BlockModelDefinition.CODEC, function, map);
        }

        @Override
        public void accept(Object blockStateSupplier) {
            this.accept((BlockModelDefinitionCreator)blockStateSupplier);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelSuppliers
    implements BiConsumer<Identifier, ModelSupplier> {
        final private Map<Identifier, ModelSupplier> modelSuppliers = new HashMap<Identifier, ModelSupplier>();

        ModelSuppliers() {
        }

        @Override
        public void accept(Identifier identifier, ModelSupplier modelSupplier) {
            Supplier supplier = this.modelSuppliers.put(identifier, modelSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + String.valueOf(identifier));
            }
        }

        public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
            return DataProvider.writeAllToPath(writer, Supplier::get, pathResolver::resolveJson, this.modelSuppliers);
        }

        @Override
        public void accept(Object id, Object modelSupplier) {
            this.accept((Identifier)id, (ModelSupplier)modelSupplier);
        }
    }
}

