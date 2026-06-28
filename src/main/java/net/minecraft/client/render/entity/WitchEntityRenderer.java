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
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitchEntityRenderer
extends MobEntityRenderer<WitchEntity, WitchEntityRenderState, WitchEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/witch.png");

    public WitchEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WitchEntityModel(context.getPart(EntityModelLayers.WITCH)), 0.5f);
        this.addFeature(new WitchHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(WitchEntityRenderState witchEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public WitchEntityRenderState net_minecraft_client_render_entity_state_WitchEntityRenderState_createRenderState() {
        return new WitchEntityRenderState();
    }

    @Override
    public void updateRenderState(WitchEntity witchEntity, WitchEntityRenderState witchEntityRenderState, float f) {
        super.updateRenderState(witchEntity, witchEntityRenderState, f);
        ItemHolderEntityRenderState.update(witchEntity, witchEntityRenderState, this.itemModelResolver);
        witchEntityRenderState.id = witchEntity.getId();
        ItemStack itemStack = witchEntity.getMainHandStack();
        witchEntityRenderState.holdingItem = !itemStack.isEmpty();
        witchEntityRenderState.holdingPotion = itemStack.isOf(Items.POTION);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WitchEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_WitchEntityRenderState_createRenderState();
    }
}

