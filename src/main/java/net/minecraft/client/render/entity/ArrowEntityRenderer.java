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
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ArrowEntityRenderer
extends ProjectileEntityRenderer<ArrowEntity, ArrowEntityRenderState> {
    final static public Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/arrow.png");
    final static public Identifier TIPPED_TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/tipped_arrow.png");

    public ArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected Identifier getTexture(ArrowEntityRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.tipped ? TIPPED_TEXTURE : TEXTURE;
    }

    @Override
    public ArrowEntityRenderState net_minecraft_client_render_entity_state_ArrowEntityRenderState_createRenderState() {
        return new ArrowEntityRenderState();
    }

    @Override
    public void updateRenderState(ArrowEntity arrowEntity, ArrowEntityRenderState arrowEntityRenderState, float f) {
        super.updateRenderState(arrowEntity, arrowEntityRenderState, f);
        arrowEntityRenderState.tipped = arrowEntity.getColor() > 0;
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ArrowEntityRenderState_createRenderState();
    }
}

