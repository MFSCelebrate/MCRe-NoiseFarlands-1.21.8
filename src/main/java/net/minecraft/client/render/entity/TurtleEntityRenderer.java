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
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TurtleEntityRenderState;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityRenderer
extends AgeableMobEntityRenderer<TurtleEntity, TurtleEntityRenderState, TurtleEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/turtle/big_sea_turtle.png");

    public TurtleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TurtleEntityModel(context.getPart(EntityModelLayers.TURTLE)), new TurtleEntityModel(context.getPart(EntityModelLayers.TURTLE_BABY)), 0.7f);
    }

    @Override
    protected float getShadowRadius(TurtleEntityRenderState turtleEntityRenderState) {
        float f = super.getShadowRadius(turtleEntityRenderState);
        if (turtleEntityRenderState.baby) {
            return f * 0.83f;
        }
        return f;
    }

    @Override
    public TurtleEntityRenderState net_minecraft_client_render_entity_state_TurtleEntityRenderState_createRenderState() {
        return new TurtleEntityRenderState();
    }

    @Override
    public void updateRenderState(TurtleEntity turtleEntity, TurtleEntityRenderState turtleEntityRenderState, float f) {
        super.updateRenderState(turtleEntity, turtleEntityRenderState, f);
        turtleEntityRenderState.onLand = !turtleEntity.isTouchingWater() && turtleEntity.isOnGround();
        turtleEntityRenderState.diggingSand = turtleEntity.isDiggingSand();
        turtleEntityRenderState.hasEgg = !turtleEntity.isBaby() && turtleEntity.hasEgg();
    }

    @Override
    public Identifier getTexture(TurtleEntityRenderState turtleEntityRenderState) {
        return TEXTURE;
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((TurtleEntityRenderState)livingEntityRenderState);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_TurtleEntityRenderState_createRenderState();
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((TurtleEntityRenderState)state);
    }
}

