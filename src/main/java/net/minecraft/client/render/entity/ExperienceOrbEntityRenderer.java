/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity, ExperienceOrbEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");
    final static private RenderLayer LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);

    public ExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    @Override
    protected int getBlockLight(ExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
        return MathHelper.clamp(super.getBlockLight(experienceOrbEntity, blockPos) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrbEntityRenderState experienceOrbEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        int j = experienceOrbEntityRenderState.size;
        float f = (float)(j % 4 * 16 + 0) / 64.0f;
        float g = (float)(j % 4 * 16 + 16) / 64.0f;
        float h = (float)(j / 4 * 16 + 0) / 64.0f;
        float k = (float)(j / 4 * 16 + 16) / 64.0f;
        float l = 1.0f;
        float m = 0.5f;
        float n = 0.25f;
        float o = 255.0f;
        float p = experienceOrbEntityRenderState.age / 2.0f;
        int q = (int)((MathHelper.sin(p + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int r = 255;
        int s = (int)((MathHelper.sin(p + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        matrixStack.translate(0.0f, 0.1f, 0.0f);
        matrixStack.multiply((Quaternionfc)this.dispatcher.getRotation());
        float t = 0.3f;
        matrixStack.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        MatrixStack.Entry entry = matrixStack.peek();
        ExperienceOrbEntityRenderer.vertex(vertexConsumer, entry, -0.5f, -0.25f, q, 255, s, f, k, 1);
        ExperienceOrbEntityRenderer.vertex(vertexConsumer, entry, 0.5f, -0.25f, q, 255, s, g, k, 1);
        ExperienceOrbEntityRenderer.vertex(vertexConsumer, entry, 0.5f, 0.75f, q, 255, s, g, h, 1);
        ExperienceOrbEntityRenderer.vertex(vertexConsumer, entry, -0.5f, 0.75f, q, 255, s, f, h, 1);
        matrixStack.pop();
        super.render(experienceOrbEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(matrix, x, y, 0.0f).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public ExperienceOrbEntityRenderState net_minecraft_client_render_entity_state_ExperienceOrbEntityRenderState_createRenderState() {
        return new ExperienceOrbEntityRenderState();
    }

    @Override
    public void updateRenderState(ExperienceOrbEntity experienceOrbEntity, ExperienceOrbEntityRenderState experienceOrbEntityRenderState, float f) {
        super.updateRenderState(experienceOrbEntity, experienceOrbEntityRenderState, f);
        experienceOrbEntityRenderState.size = experienceOrbEntity.getOrbSize();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ExperienceOrbEntityRenderState_createRenderState();
    }
}

