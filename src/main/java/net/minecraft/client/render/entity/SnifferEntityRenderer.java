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
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnifferEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnifferEntityRenderState;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class SnifferEntityRenderer
extends AgeableMobEntityRenderer<SnifferEntity, SnifferEntityRenderState, SnifferEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sniffer/sniffer.png");

    public SnifferEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SnifferEntityModel(context.getPart(EntityModelLayers.SNIFFER)), new SnifferEntityModel(context.getPart(EntityModelLayers.SNIFFER_BABY)), 1.1f);
    }

    @Override
    public Identifier getTexture(SnifferEntityRenderState snifferEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SnifferEntityRenderState net_minecraft_client_render_entity_state_SnifferEntityRenderState_createRenderState() {
        return new SnifferEntityRenderState();
    }

    @Override
    public void updateRenderState(SnifferEntity snifferEntity, SnifferEntityRenderState snifferEntityRenderState, float f) {
        super.updateRenderState(snifferEntity, snifferEntityRenderState, f);
        snifferEntityRenderState.searching = snifferEntity.isSearching();
        snifferEntityRenderState.diggingAnimationState.copyFrom(snifferEntity.diggingAnimationState);
        snifferEntityRenderState.sniffingAnimationState.copyFrom(snifferEntity.sniffingAnimationState);
        snifferEntityRenderState.risingAnimationState.copyFrom(snifferEntity.risingAnimationState);
        snifferEntityRenderState.feelingHappyAnimationState.copyFrom(snifferEntity.feelingHappyAnimationState);
        snifferEntityRenderState.scentingAnimationState.copyFrom(snifferEntity.scentingAnimationState);
    }

    @Override
    protected Box getBoundingBox(SnifferEntity snifferEntity) {
        return super.getBoundingBox(snifferEntity).expand(0.6f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SnifferEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SnifferEntityRenderState_createRenderState();
    }
}

