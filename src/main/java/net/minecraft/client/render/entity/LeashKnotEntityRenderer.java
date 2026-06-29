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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LeashKnotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LeashKnotEntityRenderer
extends EntityRenderer<LeashKnotEntity, EntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/lead_knot.png");
    final private LeashKnotEntityModel field_53192;

    public LeashKnotEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.field_53192 = new LeashKnotEntityModel(context.getPart(EntityModelLayers.LEASH_KNOT));
    }

    @Override
    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        this.field_53192.setAngles(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.field_53192.getLayer(TEXTURE));
        this.field_53192.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

