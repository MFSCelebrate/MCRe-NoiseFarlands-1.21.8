/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class BannerResultGuiElementRenderer
extends SpecialGuiElementRenderer<BannerResultGuiElementRenderState> {
    public BannerResultGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    @Override
    public Class<BannerResultGuiElementRenderState> getElementClass() {
        return BannerResultGuiElementRenderState.class;
    }

    @Override
    protected void render(BannerResultGuiElementRenderState bannerResultGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        matrixStack.translate(0.0f, 0.25f, 0.0f);
        BannerBlockEntityRenderer.renderCanvas(matrixStack, this.vertexConsumers, 0xF000F0, OverlayTexture.DEFAULT_UV, bannerResultGuiElementRenderState.flag(), ModelBaker.BANNER_BASE, true, bannerResultGuiElementRenderState.baseColor(), bannerResultGuiElementRenderState.resultBannerPatterns());
    }

    @Override
    protected String getName() {
        return "banner result";
    }
}

