/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;
import net.minecraft.client.render.model.json.BlockPropertiesPredicate;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.state.StateManager;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record BlockModelDefinition(Optional<Variants> simpleModels, Optional<Multipart> multipartModel) {
    final static Logger LOGGER = LogUtils.getLogger();
    final static public Codec<BlockModelDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Variants.CODEC.optionalFieldOf("variants").forGetter(BlockModelDefinition::simpleModels), (App)Multipart.CODEC.optionalFieldOf("multipart").forGetter(BlockModelDefinition::multipartModel)).apply((Applicative)instance, BlockModelDefinition::new)).validate(modelDefinition -> {
        if (modelDefinition.simpleModels().isEmpty() && modelDefinition.multipartModel().isEmpty()) {
            return DataResult.error(() -> "Neither 'variants' nor 'multipart' found");
        }
        return DataResult.success((Object)modelDefinition);
    });

    public Map<BlockState, BlockStateModel.UnbakedGrouped> load(StateManager<Block, BlockState> stateManager, Supplier<String> idSupplier) {
        IdentityHashMap<BlockState, BlockStateModel.UnbakedGrouped> map = new IdentityHashMap<BlockState, BlockStateModel.UnbakedGrouped>();
        this.simpleModels.ifPresent(simpleModels -> simpleModels.load(stateManager, idSupplier, (state, model) -> {
            BlockStateModel.UnbakedGrouped unbakedGrouped = map.put((BlockState)state, (BlockStateModel.UnbakedGrouped)model);
            if (unbakedGrouped != null) {
                throw new IllegalArgumentException("Overlapping definition on state: " + String.valueOf(state));
            }
        }));
        this.multipartModel.ifPresent(multipartModel -> {
            ImmutableList list = stateManager.getStates();
            MultipartBlockStateModel.MultipartUnbaked unbakedGrouped = multipartModel.toModel(stateManager);
            for (BlockState blockState : list) {
                map.putIfAbsent(blockState, unbakedGrouped);
            }
        });
        return map;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockModelDefinition.class, "simpleModels;multiPart", "simpleModels", "multipartModel"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockModelDefinition.class, "simpleModels;multiPart", "simpleModels", "multipartModel"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockModelDefinition.class, "simpleModels;multiPart", "simpleModels", "multipartModel"}, this, o);
    }

    @Environment(value=EnvType.CLIENT)
    public record Multipart(List<MultipartModelComponent> selectors) {
        final static public Codec<Multipart> CODEC = Codecs.nonEmptyList(MultipartModelComponent.CODEC.listOf()).xmap(Multipart::new, Multipart::selectors);

        public MultipartBlockStateModel.MultipartUnbaked toModel(StateManager<Block, BlockState> stateManager) {
            ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)this.selectors.size());
            for (MultipartModelComponent multipartModelComponent : this.selectors) {
                builder.add(new MultipartBlockStateModel.Selector<BlockStateModel.Unbaked>(multipartModelComponent.init(stateManager), multipartModelComponent.model()));
            }
            return new MultipartBlockStateModel.MultipartUnbaked((List<MultipartBlockStateModel.Selector<BlockStateModel.Unbaked>>)builder.build());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Variants(Map<String, BlockStateModel.Unbaked> models) {
        final static public Codec<Variants> CODEC = Codecs.nonEmptyMap(Codec.unboundedMap((Codec)Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(Variants::new, Variants::models);

        public void load(StateManager<Block, BlockState> stateManager, Supplier<String> idSupplier, BiConsumer<BlockState, BlockStateModel.UnbakedGrouped> callback) {
            this.models.forEach((predicate, model) -> {
                try {
                    Predicate predicate2 = BlockPropertiesPredicate.parse(stateManager, predicate);
                    BlockStateModel.UnbakedGrouped unbakedGrouped = model.cached();
                    for (BlockState blockState : stateManager.getStates()) {
                        if (!predicate2.test(blockState)) continue;
                        callback.accept(blockState, unbakedGrouped);
                    }
                }
                catch (Exception exception) {
                    LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{idSupplier.get(), predicate, exception.getMessage()});
                }
            });
        }
    }
}

