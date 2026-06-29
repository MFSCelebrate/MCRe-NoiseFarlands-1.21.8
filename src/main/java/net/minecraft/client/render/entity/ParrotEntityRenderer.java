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
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityRenderer
extends MobEntityRenderer<ParrotEntity, ParrotEntityRenderState, ParrotEntityModel> {
    final static private Identifier RED_BLUE_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_red_blue.png");
    final static private Identifier BLUE_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_blue.png");
    final static private Identifier GREEN_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_green.png");
    final static private Identifier YELLOW_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_yellow_blue.png");
    final static private Identifier GREY_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_grey.png");

    public ParrotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3f);
    }

    @Override
    public Identifier getTexture(ParrotEntityRenderState parrotEntityRenderState) {
        return ParrotEntityRenderer.getTexture(parrotEntityRenderState.variant);
    }

    @Override
    public ParrotEntityRenderState net_minecraft_client_render_entity_state_ParrotEntityRenderState_createRenderState() {
        return new ParrotEntityRenderState();
    }

    @Override
    public void updateRenderState(ParrotEntity parrotEntity, ParrotEntityRenderState parrotEntityRenderState, float f) {
        super.updateRenderState(parrotEntity, parrotEntityRenderState, f);
        parrotEntityRenderState.variant = parrotEntity.getVariant();
        float g = MathHelper.lerp(f, parrotEntity.lastFlapProgress, parrotEntity.flapProgress);
        float h = MathHelper.lerp(f, parrotEntity.lastMaxWingDeviation, parrotEntity.maxWingDeviation);
        parrotEntityRenderState.flapAngle = (MathHelper.sin(g) + 1.0f) * h;
        parrotEntityRenderState.parrotPose = ParrotEntityModel.getPose(parrotEntity);
    }

    public static Identifier getTexture(ParrotEntity.Variant variant) {
        return switch (variant) {
            default -> throw new MatchException(null, null);
            case ParrotEntity.Variant.RED_BLUE -> RED_BLUE_TEXTURE;
            case ParrotEntity.Variant.BLUE -> BLUE_TEXTURE;
            case ParrotEntity.Variant.GREEN -> GREEN_TEXTURE;
            case ParrotEntity.Variant.YELLOW_BLUE -> YELLOW_TEXTURE;
            case ParrotEntity.Variant.GRAY -> GREY_TEXTURE;
        };
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ParrotEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ParrotEntityRenderState_createRenderState();
    }
}

