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
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityRenderer
extends AgeableMobEntityRenderer<OcelotEntity, FelineEntityRenderState, OcelotEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/cat/ocelot.png");

    public OcelotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT)), new OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT_BABY)), 0.4f);
    }

    @Override
    public Identifier getTexture(FelineEntityRenderState felineEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public FelineEntityRenderState net_minecraft_client_render_entity_state_FelineEntityRenderState_createRenderState() {
        return new FelineEntityRenderState();
    }

    @Override
    public void updateRenderState(OcelotEntity ocelotEntity, FelineEntityRenderState felineEntityRenderState, float f) {
        super.updateRenderState(ocelotEntity, felineEntityRenderState, f);
        felineEntityRenderState.inSneakingPose = ocelotEntity.isInSneakingPose();
        felineEntityRenderState.sprinting = ocelotEntity.isSprinting();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FelineEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_FelineEntityRenderState_createRenderState();
    }
}

