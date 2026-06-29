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
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityRenderer
extends AgeableMobEntityRenderer<LlamaEntity, LlamaEntityRenderState, LlamaEntityModel> {
    final static private Identifier CREAMY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/creamy.png");
    final static private Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/llama/white.png");
    final static private Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/llama/brown.png");
    final static private Identifier GRAY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/gray.png");

    public LlamaEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer) {
        super(context, new LlamaEntityModel(context.getPart(layer)), new LlamaEntityModel(context.getPart(babyLayer)), 0.7f);
        this.addFeature(new LlamaDecorFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(LlamaEntityRenderState llamaEntityRenderState) {
        return switch (llamaEntityRenderState.variant) {
            default -> throw new MatchException(null, null);
            case LlamaEntity.Variant.CREAMY -> CREAMY_TEXTURE;
            case LlamaEntity.Variant.WHITE -> WHITE_TEXTURE;
            case LlamaEntity.Variant.BROWN -> BROWN_TEXTURE;
            case LlamaEntity.Variant.GRAY -> GRAY_TEXTURE;
        };
    }

    @Override
    public LlamaEntityRenderState net_minecraft_client_render_entity_state_LlamaEntityRenderState_createRenderState() {
        return new LlamaEntityRenderState();
    }

    @Override
    public void updateRenderState(LlamaEntity llamaEntity, LlamaEntityRenderState llamaEntityRenderState, float f) {
        super.updateRenderState(llamaEntity, llamaEntityRenderState, f);
        llamaEntityRenderState.variant = llamaEntity.getVariant();
        llamaEntityRenderState.hasChest = !llamaEntity.isBaby() && llamaEntity.hasChest();
        llamaEntityRenderState.bodyArmor = llamaEntity.getBodyArmor();
        llamaEntityRenderState.trader = llamaEntity.isTrader();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((LlamaEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_LlamaEntityRenderState_createRenderState();
    }
}

