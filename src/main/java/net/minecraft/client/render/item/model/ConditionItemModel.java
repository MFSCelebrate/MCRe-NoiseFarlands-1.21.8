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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.PropertyTester;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.ContextSwapper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ConditionItemModel
implements ItemModel {
    final private PropertyTester property;
    final private ItemModel onTrue;
    final private ItemModel onFalse;

    public ConditionItemModel(PropertyTester property, ItemModel onTrue, ItemModel onFalse) {
        this.property = property;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        (this.property.test(stack, world, user, seed, displayContext) ? this.onTrue : this.onFalse).update(state, stack, resolver, displayContext, world, user, seed);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(BooleanProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) implements ItemModel.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BooleanProperties.CODEC.forGetter(Unbaked::property), (App)ItemModelTypes.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue), (App)ItemModelTypes.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)).apply((Applicative)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            return new ConditionItemModel(this.makeWorldIndependentProperty(this.property, context.contextSwapper()), this.onTrue.bake(context), this.onFalse.bake(context));
        }

        private PropertyTester makeWorldIndependentProperty(BooleanProperty property, @Nullable ContextSwapper contextSwapper) {
            if (contextSwapper == null) {
                return property;
            }
            DataCache<ClientWorld, PropertyTester> dataCache = new DataCache<ClientWorld, PropertyTester>(world -> Unbaked.swapContext(property, contextSwapper, world));
            return (stack, world, entity, seed, transformationMode) -> {
                BooleanProperty propertyTester = world == null ? property : (PropertyTester)dataCache.compute(world);
                return propertyTester.test(stack, world, entity, seed, transformationMode);
            };
        }

        private static <T extends BooleanProperty> T swapContext(T value, ContextSwapper contextSwapper, ClientWorld world) {
            return contextSwapper.swapContext(value.getCodec().codec(), value, world.getRegistryManager()).result().orElse(value);
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.onTrue.resolve(resolver);
            this.onFalse.resolve(resolver);
        }
    }
}

