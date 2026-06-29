/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.ContextSwapper;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SelectItemModel<T>
implements ItemModel {
    final private SelectProperty<T> property;
    final private ModelSelector<T> selector;

    public SelectItemModel(SelectProperty<T> property, ModelSelector<T> selector) {
        this.property = property;
        this.selector = selector;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        T object = this.property.getValue(stack, world, user, seed, displayContext);
        ItemModel itemModel = this.selector.get(object, world);
        if (itemModel != null) {
            itemModel.update(state, stack, resolver, displayContext, world, user, seed);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface ModelSelector<T> {
        @Nullable
        public ItemModel get(@Nullable T var1, @Nullable ClientWorld var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class SwitchCase<T>
    extends Record {
        final List<T> values;
        final ItemModel.Unbaked model;

        public SwitchCase(List<T> list, ItemModel.Unbaked unbaked) {
            this.values = list;
            this.model = unbaked;
        }

        public static <T> Codec<SwitchCase<T>> createCodec(Codec<T> conditionCodec) {
            return RecordCodecBuilder.create(instance -> instance.group((App)Codecs.nonEmptyList(Codecs.listOrSingle(conditionCodec)).fieldOf("when").forGetter(SwitchCase::values), (App)ItemModelTypes.CODEC.fieldOf("model").forGetter(SwitchCase::model)).apply((Applicative)instance, SwitchCase::new));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SwitchCase.class, "values;model", "values", "model"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SwitchCase.class, "values;model", "values", "model"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SwitchCase.class, "values;model", "values", "model"}, this, object);
        }

        public List<T> values() {
            return this.values;
        }

        public ItemModel.Unbaked model() {
            return this.model;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record UnbakedSwitch<P extends SelectProperty<T>, T>(P property, List<SwitchCase<T>> cases) {
        final static public MapCodec<UnbakedSwitch<?, ?>> CODEC = SelectProperties.CODEC.dispatchMap("property", unbakedSwitch -> unbakedSwitch.property().getType(), SelectProperty.Type::switchCodec);

        public ItemModel bake(ItemModel.BakeContext context, ItemModel fallback) {
            Object2ObjectOpenHashMap object2ObjectMap = new Object2ObjectOpenHashMap();
            for (SwitchCase<T> switchCase : this.cases) {
                ItemModel.Unbaked unbaked = switchCase.model;
                ItemModel itemModel = unbaked.bake(context);
                for (Object object : switchCase.values) {
                    object2ObjectMap.put(object, (Object)itemModel);
                }
            }
            object2ObjectMap.defaultReturnValue((Object)fallback);
            return new SelectItemModel<T>(this.property, this.buildModelSelector((Object2ObjectMap<T, ItemModel>)object2ObjectMap, context.contextSwapper()));
        }

        private ModelSelector<T> buildModelSelector(Object2ObjectMap<T, ItemModel> models, @Nullable ContextSwapper contextSwapper) {
            if (contextSwapper == null) {
                return (value, world) -> (ItemModel)models.get(value);
            }
            ItemModel itemModel = (ItemModel)models.defaultReturnValue();
            DataCache<ClientWorld, Object2ObjectMap> dataCache = new DataCache<ClientWorld, Object2ObjectMap>(world -> {
                Object2ObjectOpenHashMap object2ObjectMap2 = new Object2ObjectOpenHashMap(models.size());
                object2ObjectMap2.defaultReturnValue((Object)itemModel);
                models.forEach((arg_0, arg_1) -> this.method_67280(contextSwapper, world, (Object2ObjectMap)object2ObjectMap2, arg_0, arg_1));
                return object2ObjectMap2;
            });
            return (value, world) -> {
                if (world == null) {
                    return (ItemModel)models.get(value);
                }
                if (value == null) {
                    return itemModel;
                }
                return (ItemModel)((Object2ObjectMap)dataCache.compute(world)).get(value);
            };
        }

        public void resolveCases(ResolvableModel.Resolver resolver) {
            for (SwitchCase<T> switchCase : this.cases) {
                switchCase.model.resolve(resolver);
            }
        }

        private void method_67280(ContextSwapper contextSwapper, ClientWorld clientWorld, Object2ObjectMap object2ObjectMap, Object value, ItemModel world) {
            contextSwapper.swapContext(this.property.valueCodec(), value, clientWorld.getRegistryManager()).ifSuccess(swappedValue -> object2ObjectMap.put(swappedValue, (Object)world));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)UnbakedSwitch.CODEC.forGetter(Unbaked::unbakedSwitch), (App)ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply((Applicative)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            ItemModel itemModel = this.fallback.map(model -> model.bake(context)).orElse(context.missingItemModel());
            return this.unbakedSwitch.bake(context, itemModel);
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.unbakedSwitch.resolveCases(resolver);
            this.fallback.ifPresent(model -> model.resolve(resolver));
        }
    }
}

