/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fStack
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.PlayerSkinGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinGuiElementRenderer
extends SpecialGuiElementRenderer<PlayerSkinGuiElementRenderState> {
    public PlayerSkinGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    @Override
    public Class<PlayerSkinGuiElementRenderState> getElementClass() {
        return PlayerSkinGuiElementRenderState.class;
    }

    @Override
    protected void render(PlayerSkinGuiElementRenderState playerSkinGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.PLAYER_SKIN);
        int i = MinecraftClient.getInstance().getWindow().getScaleFactor();
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        float f = playerSkinGuiElementRenderState.scale() * (float)i;
        matrix4fStack.rotateAround((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(playerSkinGuiElementRenderState.xRotation()), 0.0f, f * -playerSkinGuiElementRenderState.yPivot(), 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-playerSkinGuiElementRenderState.yRotation()));
        matrixStack.translate(0.0f, -1.6010001f, 0.0f);
        RenderLayer renderLayer = playerSkinGuiElementRenderState.playerModel().getLayer(playerSkinGuiElementRenderState.texture());
        playerSkinGuiElementRenderState.playerModel().render(matrixStack, this.vertexConsumers.getBuffer(renderLayer), 0xF000F0, OverlayTexture.DEFAULT_UV);
        this.vertexConsumers.draw();
        matrix4fStack.popMatrix();
    }

    @Override
    protected String getName() {
        return "player skin";
    }
}

