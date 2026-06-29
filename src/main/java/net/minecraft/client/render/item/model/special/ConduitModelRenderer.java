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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class ConduitModelRenderer
implements SimpleSpecialModelRenderer {
    final private ModelPart shell;

    public ConduitModelRenderer(ModelPart shell) {
        this.shell = shell;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        VertexConsumer vertexConsumer = ConduitBlockEntityRenderer.BASE_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        this.shell.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        this.shell.collectVertices(matrixStack, vertices);
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
            return new ConduitModelRenderer(entityModels.getModelPart(EntityModelLayers.CONDUIT_SHELL));
        }
    }
}

