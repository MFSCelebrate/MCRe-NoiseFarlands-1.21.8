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
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityRenderer
extends MobEntityRenderer<MagmaCubeEntity, SlimeEntityRenderState, MagmaCubeEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/magmacube.png");

    public MagmaCubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MagmaCubeEntityModel(context.getPart(EntityModelLayers.MAGMA_CUBE)), 0.25f);
    }

    @Override
    protected int getBlockLight(MagmaCubeEntity magmaCubeEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState slimeEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SlimeEntityRenderState net_minecraft_client_render_entity_state_SlimeEntityRenderState_createRenderState() {
        return new SlimeEntityRenderState();
    }

    @Override
    public void updateRenderState(MagmaCubeEntity magmaCubeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
        super.updateRenderState(magmaCubeEntity, slimeEntityRenderState, f);
        slimeEntityRenderState.stretch = MathHelper.lerp(f, magmaCubeEntity.lastStretch, magmaCubeEntity.stretch);
        slimeEntityRenderState.size = magmaCubeEntity.getSize();
    }

    @Override
    protected float getShadowRadius(SlimeEntityRenderState slimeEntityRenderState) {
        return (float)slimeEntityRenderState.size * 0.25f;
    }

    @Override
    protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
        int i = slimeEntityRenderState.size;
        float f = slimeEntityRenderState.stretch / 1.5f;
        float g = 1.0f / (f + 1.0f);
        matrixStack.scale(g * 1.0f, 1.0f / g * 1.0f, g * 1.0f);
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((SlimeEntityRenderState)livingEntityRenderState);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SlimeEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SlimeEntityRenderState_createRenderState();
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((SlimeEntityRenderState)state);
    }
}

