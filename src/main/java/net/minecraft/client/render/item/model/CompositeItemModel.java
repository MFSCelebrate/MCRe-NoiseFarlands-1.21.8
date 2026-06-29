/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompositeItemModel
implements ItemModel {
    final private List<ItemModel> models;

    public CompositeItemModel(List<ItemModel> models) {
        this.models = models;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        state.addLayers(this.models.size());
        for (ItemModel itemModel : this.models) {
            itemModel.update(state, stack, resolver, displayContext, world, user, seed);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(List<ItemModel.Unbaked> models) implements ItemModel.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ItemModelTypes.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply((Applicative)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            for (ItemModel.Unbaked unbaked : this.models) {
                unbaked.resolve(resolver);
            }
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            return new CompositeItemModel(this.models.stream().map(model -> model.bake(context)).toList());
        }
    }
}

