/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class TridentModelRenderer
implements SimpleSpecialModelRenderer {
    final private TridentEntityModel model;

    public TridentModelRenderer(TridentEntityModel model) {
        this.model = model;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        matrices.push();
        matrices.scale(1.0f, -1.0f, -1.0f);
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, this.model.getLayer(TridentEntityModel.TEXTURE), false, glint);
        this.model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new TridentModelRenderer(new TridentEntityModel(entityModels.getModelPart(EntityModelLayers.TRIDENT)));
        }
    }
}

