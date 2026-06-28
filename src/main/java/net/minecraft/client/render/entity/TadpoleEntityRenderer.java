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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TadpoleEntityRenderer
extends MobEntityRenderer<TadpoleEntity, LivingEntityRenderState, TadpoleEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/tadpole/tadpole.png");

    public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TadpoleEntityModel(context.getPart(EntityModelLayers.TADPOLE)), 0.14f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState net_minecraft_client_render_entity_state_LivingEntityRenderState_createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_LivingEntityRenderState_createRenderState();
    }
}

