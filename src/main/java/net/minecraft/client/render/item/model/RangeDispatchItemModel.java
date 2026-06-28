/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RangeDispatchItemModel
implements ItemModel {
    final static private int field_55353 = 16;
    final private NumericProperty property;
    final private float scale;
    final private float[] thresholds;
    final private ItemModel[] models;
    final private ItemModel fallback;

    RangeDispatchItemModel(NumericProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback) {
        this.property = property;
        this.thresholds = thresholds;
        this.models = models;
        this.fallback = fallback;
        this.scale = scale;
    }

    private static int getIndex(float[] thresholds, float value) {
        if (thresholds.length < 16) {
            for (int i = 0; i < thresholds.length; ++i) {
                if (!(thresholds[i] > value)) continue;
                return i - 1;
            }
            return thresholds.length - 1;
        }
        int i = Arrays.binarySearch(thresholds, value);
        if (i < 0) {
            int j = ~i;
            return j - 1;
        }
        return i;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        int i;
        state.addModelKey(this);
        float f = this.property.getValue(stack, world, user, seed) * this.scale;
        ItemModel itemModel = Float.isNaN(f) ? this.fallback : ((i = RangeDispatchItemModel.getIndex(this.thresholds, f)) == -1 ? this.fallback : this.models[i]);
        itemModel.update(state, stack, resolver, displayContext, world, user, seed);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Entry
    extends Record {
        final float threshold;
        final ItemModel.Unbaked model;
        final static public Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("threshold").forGetter(Entry::threshold), (App)ItemModelTypes.CODEC.fieldOf("model").forGetter(Entry::model)).apply((Applicative)instance, Entry::new));
        final static public Comparator<Entry> COMPARATOR = Comparator.comparingDouble(Entry::threshold);

        public Entry(float f, ItemModel.Unbaked unbaked) {
            this.threshold = f;
            this.model = unbaked;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "threshold;model", "threshold", "model"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "threshold;model", "threshold", "model"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "threshold;model", "threshold", "model"}, this, object);
        }

        public float threshold() {
            return this.threshold;
        }

        public ItemModel.Unbaked model() {
            return this.model;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(NumericProperty property, float scale, List<Entry> entries, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)NumericProperties.CODEC.forGetter(Unbaked::property), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(Unbaked::scale), (App)Entry.CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries), (App)ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply((Applicative)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            float[] fs = new float[this.entries.size()];
            ItemModel[] itemModels = new ItemModel[this.entries.size()];
            ArrayList<Entry> list = new ArrayList<Entry>(this.entries);
            list.sort(Entry.COMPARATOR);
            for (int i = 0; i < list.size(); ++i) {
                Entry entry = (Entry)list.get(i);
                fs[i] = entry.threshold;
                itemModels[i] = entry.model.bake(context);
            }
            ItemModel itemModel = this.fallback.map(model -> model.bake(context)).orElse(context.missingItemModel());
            return new RangeDispatchItemModel(this.property, this.scale, fs, itemModels, itemModel);
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.fallback.ifPresent(model -> model.resolve(resolver));
            this.entries.forEach(entry -> entry.model.resolve(resolver));
        }
    }
}

