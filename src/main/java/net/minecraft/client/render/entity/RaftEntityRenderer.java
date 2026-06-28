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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RaftEntityRenderer
extends AbstractBoatEntityRenderer {
    final private EntityModel<BoatEntityRenderState> model;
    final private Identifier texture;

    public RaftEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer) {
        super(context);
        this.texture = layer.id().withPath(path -> "textures/entity/" + path + ".png");
        this.model = new RaftEntityModel(context.getPart(layer));
    }

    @Override
    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.model;
    }

    @Override
    protected RenderLayer getRenderLayer() {
        return this.model.getLayer(this.texture);
    }
}

