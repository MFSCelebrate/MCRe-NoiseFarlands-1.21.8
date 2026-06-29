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
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BlazeEntityRenderer
extends MobEntityRenderer<BlazeEntity, LivingEntityRenderState, BlazeEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/blaze.png");

    public BlazeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BlazeEntityModel(context.getPart(EntityModelLayers.BLAZE)), 0.5f);
    }

    @Override
    protected int getBlockLight(BlazeEntity blazeEntity, BlockPos blockPos) {
        return 15;
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

