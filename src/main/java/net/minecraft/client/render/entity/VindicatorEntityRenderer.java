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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VindicatorEntityRenderer
extends IllagerEntityRenderer<VindicatorEntity, IllagerEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/vindicator.png");

    public VindicatorEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.VINDICATOR)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllagerEntityRenderState, IllagerEntityModel<IllagerEntityRenderState>>(this, (FeatureRendererContext)this){

            @Override
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IllagerEntityRenderState illagerEntityRenderState, float f, float g) {
                if (illagerEntityRenderState.attacking) {
                    super.render(matrixStack, vertexConsumerProvider, 1, illagerEntityRenderState, f, g);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public IllagerEntityRenderState net_minecraft_client_render_entity_state_IllagerEntityRenderState_createRenderState() {
        return new IllagerEntityRenderState();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IllagerEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_IllagerEntityRenderState_createRenderState();
    }
}

