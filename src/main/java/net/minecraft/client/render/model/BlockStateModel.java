/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.WeightedBlockStateModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public interface BlockStateModel
extends FabricBlockStateModel {
    public void addParts(Random var1, List<BlockModelPart> var2);

    default public List<BlockModelPart> getParts(Random random) {
        ObjectArrayList list = new ObjectArrayList();
        this.addParts(random, (List<BlockModelPart>)list);
        return list;
    }

    public Sprite particleSprite();

    @Environment(value=EnvType.CLIENT)
    public static class CachedUnbaked
    implements UnbakedGrouped {
        final Unbaked delegate;
        final private Baker.ResolvableCacheKey<BlockStateModel> cacheKey = new Baker.ResolvableCacheKey<BlockStateModel>(){

            @Override
            public BlockStateModel net_minecraft_client_render_model_BlockStateModel_compute(Baker baker) {
                return delegate.bake(baker);
            }

            @Override
            public Object java_lang_Object_compute(Baker baker) {
                return this.net_minecraft_client_render_model_BlockStateModel_compute(baker);
            }
        };

        public CachedUnbaked(Unbaked delegate) {
            this.delegate = delegate;
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.delegate.resolve(resolver);
        }

        @Override
        public BlockStateModel bake(BlockState state, Baker baker) {
            return baker.compute(this.cacheKey);
        }

        @Override
        public Object getEqualityGroup(BlockState state) {
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Unbaked
    extends ResolvableModel {
        final static public Codec<Weighted<ModelVariant>> WEIGHTED_VARIANT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ModelVariant.MAP_CODEC.forGetter(Weighted::value), (App)Codecs.POSITIVE_INT.optionalFieldOf("weight", (Object)1).forGetter(Weighted::weight)).apply((Applicative)instance, Weighted::new));
        final static public Codec<WeightedBlockStateModel.Unbaked> WEIGHTED_CODEC = Codecs.nonEmptyList(WEIGHTED_VARIANT_CODEC.listOf()).flatComapMap(list -> new WeightedBlockStateModel.Unbaked(Pool.of(Lists.transform((List)list, weighted -> weighted.transform(SimpleBlockStateModel.Unbaked::new)))), unbaked -> {
            List<Weighted<Unbaked>> list = unbaked.entries().getEntries();
            ArrayList<Weighted<ModelVariant>> list2 = new ArrayList<Weighted<ModelVariant>>(list.size());
            for (Weighted<Unbaked> weighted : list) {
                Unbaked object = weighted.value();
                if (object instanceof SimpleBlockStateModel.Unbaked) {
                    SimpleBlockStateModel.Unbaked unbaked2 = (SimpleBlockStateModel.Unbaked)object;
                    list2.add(new Weighted<ModelVariant>(unbaked2.variant(), weighted.weight()));
                    continue;
                }
                return DataResult.error(() -> "Only single variants are supported");
            }
            return DataResult.success(list2);
        });
        final static public Codec<Unbaked> CODEC = Codec.either(WEIGHTED_CODEC, SimpleBlockStateModel.Unbaked.CODEC).flatComapMap(either -> (Unbaked)either.map(left -> left, right -> right), variant -> {
            Unbaked unbaked = variant;
            Objects.requireNonNull(unbaked);
            Unbaked unbaked2 = unbaked;
            int i = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SimpleBlockStateModel.Unbaked.class, WeightedBlockStateModel.Unbaked.class}, (Object)unbaked2, i)) {
                case 0 -> {
                    SimpleBlockStateModel.Unbaked unbaked2 = (SimpleBlockStateModel.Unbaked)unbaked2;
                    yield DataResult.success((Object)Either.right((Object)unbaked2));
                }
                case 1 -> {
                    WeightedBlockStateModel.Unbaked unbaked3 = (WeightedBlockStateModel.Unbaked)unbaked2;
                    yield DataResult.success((Object)Either.left((Object)unbaked3));
                }
                default -> DataResult.error(() -> "Only a single variant or a list of variants are supported");
            };
        });

        public BlockStateModel bake(Baker var1);

        default public UnbakedGrouped cached() {
            return new CachedUnbaked(this);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface UnbakedGrouped
    extends ResolvableModel {
        public BlockStateModel bake(BlockState var1, Baker var2);

        public Object getEqualityGroup(BlockState var1);
    }
}

