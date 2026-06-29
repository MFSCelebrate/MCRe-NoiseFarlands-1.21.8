/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
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
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class TridentEntityRenderer
extends EntityRenderer<TridentEntity, TridentEntityRenderState> {
    final static public Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident.png");
    final private TridentEntityModel model;

    public TridentEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new TridentEntityModel(context.getPart(EntityModelLayers.TRIDENT));
    }

    @Override
    public void render(TridentEntityRenderState tridentEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntityRenderState.yaw - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(tridentEntityRenderState.pitch + 90.0f));
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(TEXTURE), false, tridentEntityRenderState.enchanted);
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(tridentEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public TridentEntityRenderState net_minecraft_client_render_entity_state_TridentEntityRenderState_createRenderState() {
        return new TridentEntityRenderState();
    }

    @Override
    public void updateRenderState(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f) {
        super.updateRenderState(tridentEntity, tridentEntityRenderState, f);
        tridentEntityRenderState.yaw = tridentEntity.getLerpedYaw(f);
        tridentEntityRenderState.pitch = tridentEntity.getLerpedPitch(f);
        tridentEntityRenderState.enchanted = tridentEntity.isEnchanted();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_TridentEntityRenderState_createRenderState();
    }
}

