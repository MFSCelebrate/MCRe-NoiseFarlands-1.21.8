/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends AbstractBoatEntityRenderer {
    final private Model waterMaskModel;
    final private Identifier texture;
    final private EntityModel<BoatEntityRenderState> model;

    public BoatEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx);
        this.texture = layer.id().withPath(path -> "textures/entity/" + path + ".png");
        this.waterMaskModel = new Model.SinglePartModel(ctx.getPart(EntityModelLayers.BOAT), id -> RenderLayer.getWaterMask());
        this.model = new BoatEntityModel(ctx.getPart(layer));
    }

    @Override
    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.model;
    }

    @Override
    protected RenderLayer getRenderLayer() {
        return this.model.getLayer(this.texture);
    }

    @Override
    protected void renderWaterMask(BoatEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!state.submergedInWater) {
            this.waterMaskModel.render(matrices, vertexConsumers.getBuffer(this.waterMaskModel.getLayer(this.texture)), light, OverlayTexture.DEFAULT_UV);
        }
    }
}

