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
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityRenderer
extends AbstractMinecartEntityRenderer<AbstractMinecartEntity, MinecartEntityRenderState> {
    public MinecartEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer entityModelLayer) {
        super(context, entityModelLayer);
    }

    @Override
    public MinecartEntityRenderState net_minecraft_client_render_entity_state_MinecartEntityRenderState_createRenderState() {
        return new MinecartEntityRenderState();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_MinecartEntityRenderState_createRenderState();
    }
}

