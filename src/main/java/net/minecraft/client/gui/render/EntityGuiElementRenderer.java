/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class EntityGuiElementRenderer
extends SpecialGuiElementRenderer<EntityGuiElementRenderState> {
    final private EntityRenderDispatcher entityRenderDispatcher;

    public EntityGuiElementRenderer(VertexConsumerProvider.Immediate vertexConsumers, EntityRenderDispatcher entityRenderDispatcher) {
        super(vertexConsumers);
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    @Override
    public Class<EntityGuiElementRenderState> getElementClass() {
        return EntityGuiElementRenderState.class;
    }

    @Override
    protected void render(EntityGuiElementRenderState entityGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ENTITY_IN_UI);
        Vector3f vector3f = entityGuiElementRenderState.translation();
        matrixStack.translate(vector3f.x, vector3f.y, vector3f.z);
        matrixStack.multiply((Quaternionfc)entityGuiElementRenderState.rotation());
        Quaternionf quaternionf = entityGuiElementRenderState.overrideCameraAngle();
        if (quaternionf != null) {
            this.entityRenderDispatcher.setRotation(quaternionf.conjugate(new Quaternionf()).rotateY((float)Math.PI));
        }
        this.entityRenderDispatcher.setRenderShadows(false);
        this.entityRenderDispatcher.render(entityGuiElementRenderState.renderState(), 0.0, 0.0, 0.0, matrixStack, this.vertexConsumers, 0xF000F0);
        this.entityRenderDispatcher.setRenderShadows(true);
    }

    @Override
    protected float getYOffset(int height, int windowScaleFactor) {
        return (float)height / 2.0f;
    }

    @Override
    protected String getName() {
        return "entity";
    }
}

