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
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class SpecialItemModel<T>
implements ItemModel {
    final private SpecialModelRenderer<T> specialModelType;
    final private ModelSettings settings;

    public SpecialItemModel(SpecialModelRenderer<T> specialModelType, ModelSettings settings) {
        this.specialModelType = specialModelType;
        this.settings = settings;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        if (stack.hasGlint()) {
            ItemRenderState.Glint glint = ItemRenderState.Glint.STANDARD;
            layerRenderState.setGlint(glint);
            state.markAnimated();
            state.addModelKey((Object)glint);
        }
        T object = this.specialModelType.getData(stack);
        layerRenderState.setVertices(() -> {
            HashSet<Vector3f> set = new HashSet<Vector3f>();
            this.specialModelType.collectVertices(set);
            return set.toArray(new Vector3f[0]);
        });
        layerRenderState.setSpecialModel(this.specialModelType, object);
        if (object != null) {
            state.addModelKey(object);
        }
        this.settings.addSettings(layerRenderState, displayContext);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("base").forGetter(Unbaked::base), (App)SpecialModelTypes.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply((Applicative)instance, Unbaked::new));

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.base);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            SpecialModelRenderer<?> specialModelRenderer = this.specialModel.bake(context.entityModelSet());
            if (specialModelRenderer == null) {
                return context.missingItemModel();
            }
            ModelSettings modelSettings = this.getSettings(context);
            return new SpecialItemModel(specialModelRenderer, modelSettings);
        }

        private ModelSettings getSettings(ItemModel.BakeContext context) {
            Baker baker = context.blockModelBaker();
            BakedSimpleModel bakedSimpleModel = baker.getModel(this.base);
            ModelTextures modelTextures = bakedSimpleModel.getTextures();
            return ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }
    }
}

