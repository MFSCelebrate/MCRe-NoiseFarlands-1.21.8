/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
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

import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class BasicItemModel
implements ItemModel {
    final private List<TintSource> tints;
    final private List<BakedQuad> quads;
    final private Supplier<Vector3f[]> vector;
    final private ModelSettings settings;
    final private boolean animated;

    public BasicItemModel(List<TintSource> tints, List<BakedQuad> quads, ModelSettings settings) {
        this.tints = tints;
        this.quads = quads;
        this.settings = settings;
        this.vector = Suppliers.memoize(() -> BasicItemModel.bakeQuads(this.quads));
        boolean bl = false;
        for (BakedQuad bakedQuad : quads) {
            if (!bakedQuad.sprite().isAnimated()) continue;
            bl = true;
            break;
        }
        this.animated = bl;
    }

    public static Vector3f[] bakeQuads(List<BakedQuad> quads) {
        HashSet set = new HashSet();
        for (BakedQuad bakedQuad : quads) {
            BakedQuadFactory.calculatePosition(bakedQuad.vertexData(), set::add);
        }
        return (Vector3f[])set.toArray(Vector3f[]::new);
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        if (stack.hasGlint()) {
            ItemRenderState.Glint glint = BasicItemModel.shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD;
            layerRenderState.setGlint(glint);
            state.markAnimated();
            state.addModelKey((Object)glint);
        }
        int i = this.tints.size();
        int[] is = layerRenderState.initTints(i);
        for (int j = 0; j < i; ++j) {
            int k;
            is[j] = k = this.tints.get(j).getTint(stack, world, user);
            state.addModelKey(k);
        }
        layerRenderState.setVertices(this.vector);
        layerRenderState.setRenderLayer(RenderLayers.getItemLayer(stack));
        this.settings.addSettings(layerRenderState, displayContext);
        layerRenderState.getQuads().addAll(this.quads);
        if (this.animated) {
            state.markAnimated();
        }
    }

    private static boolean shouldUseSpecialGlint(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier model, List<TintSource> tints) implements ItemModel.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model), (App)TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply((Applicative)instance, Unbaked::new));

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            Baker baker = context.blockModelBaker();
            BakedSimpleModel bakedSimpleModel = baker.getModel(this.model);
            ModelTextures modelTextures = bakedSimpleModel.getTextures();
            List<BakedQuad> list = bakedSimpleModel.bakeGeometry(modelTextures, baker, ModelRotation.X0_Y0).getAllQuads();
            ModelSettings modelSettings = ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
            return new BasicItemModel(this.tints, list, modelSettings);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }
    }
}

