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
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PufferfishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PufferfishEntityRenderer
extends MobEntityRenderer<PufferfishEntity, PufferfishEntityRenderState, EntityModel<EntityRenderState>> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/pufferfish.png");
    final private EntityModel<EntityRenderState> smallModel;
    final private EntityModel<EntityRenderState> mediumModel;
    final private EntityModel<EntityRenderState> largeModel = this.getModel();

    public PufferfishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LargePufferfishEntityModel(context.getPart(EntityModelLayers.PUFFERFISH_BIG)), 0.2f);
        this.mediumModel = new MediumPufferfishEntityModel(context.getPart(EntityModelLayers.PUFFERFISH_MEDIUM));
        this.smallModel = new SmallPufferfishEntityModel(context.getPart(EntityModelLayers.PUFFERFISH_SMALL));
    }

    @Override
    public Identifier getTexture(PufferfishEntityRenderState pufferfishEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public PufferfishEntityRenderState net_minecraft_client_render_entity_state_PufferfishEntityRenderState_createRenderState() {
        return new PufferfishEntityRenderState();
    }

    @Override
    protected float getShadowRadius(PufferfishEntityRenderState pufferfishEntityRenderState) {
        return 0.1f + 0.1f * (float)pufferfishEntityRenderState.puffState;
    }

    @Override
    public void render(PufferfishEntityRenderState pufferfishEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.model = switch (pufferfishEntityRenderState.puffState) {
            case 0 -> this.smallModel;
            case 1 -> this.mediumModel;
            default -> this.largeModel;
        };
        super.render(pufferfishEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public void updateRenderState(PufferfishEntity pufferfishEntity, PufferfishEntityRenderState pufferfishEntityRenderState, float f) {
        super.updateRenderState(pufferfishEntity, pufferfishEntityRenderState, f);
        pufferfishEntityRenderState.puffState = pufferfishEntity.getPuffState();
    }

    @Override
    protected void setupTransforms(PufferfishEntityRenderState pufferfishEntityRenderState, MatrixStack matrixStack, float f, float g) {
        matrixStack.translate(0.0f, MathHelper.cos(pufferfishEntityRenderState.age * 0.05f) * 0.08f, 0.0f);
        super.setupTransforms(pufferfishEntityRenderState, matrixStack, f, g);
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((PufferfishEntityRenderState)livingEntityRenderState);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PufferfishEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PufferfishEntityRenderState_createRenderState();
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((PufferfishEntityRenderState)state);
    }
}

