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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityRenderer
extends MobEntityRenderer<WitherEntity, WitherEntityRenderState, WitherEntityModel> {
    final static private Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither_invulnerable.png");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither.png");

    public WitherEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WitherEntityModel(context.getPart(EntityModelLayers.WITHER)), 1.0f);
        this.addFeature(new WitherArmorFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    protected int getBlockLight(WitherEntity witherEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(WitherEntityRenderState witherEntityRenderState) {
        int i = MathHelper.floor(witherEntityRenderState.invulnerableTimer);
        if (i <= 0 || i <= 80 && i / 5 % 2 == 1) {
            return TEXTURE;
        }
        return INVULNERABLE_TEXTURE;
    }

    @Override
    public WitherEntityRenderState net_minecraft_client_render_entity_state_WitherEntityRenderState_createRenderState() {
        return new WitherEntityRenderState();
    }

    @Override
    protected void scale(WitherEntityRenderState witherEntityRenderState, MatrixStack matrixStack) {
        float f = 2.0f;
        if (witherEntityRenderState.invulnerableTimer > 0.0f) {
            f -= witherEntityRenderState.invulnerableTimer / 220.0f * 0.5f;
        }
        matrixStack.scale(f, f, f);
    }

    @Override
    public void updateRenderState(WitherEntity witherEntity, WitherEntityRenderState witherEntityRenderState, float f) {
        super.updateRenderState(witherEntity, witherEntityRenderState, f);
        int i = witherEntity.getInvulnerableTimer();
        witherEntityRenderState.invulnerableTimer = 1 > 0 ? 1.0f - f : 0.0f;
        System.arraycopy(witherEntity.getSideHeadPitches(), 0, witherEntityRenderState.sideHeadPitches, 0, witherEntityRenderState.sideHeadPitches.length);
        System.arraycopy(witherEntity.getSideHeadYaws(), 0, witherEntityRenderState.sideHeadYaws, 0, witherEntityRenderState.sideHeadYaws.length);
        witherEntityRenderState.renderOverlay = witherEntity.shouldRenderOverlay();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WitherEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_WitherEntityRenderState_createRenderState();
    }
}

