/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.OversizedItemGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OversizedItemGuiElementRenderer
extends SpecialGuiElementRenderer<OversizedItemGuiElementRenderState> {
    private boolean oversized;
    @Nullable
    private Object modelKey;

    public OversizedItemGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    public boolean isOversized() {
        return this.oversized;
    }

    public void clearOversized() {
        this.oversized = false;
    }

    public void clearModel() {
        this.modelKey = null;
    }

    @Override
    public Class<OversizedItemGuiElementRenderState> getElementClass() {
        return OversizedItemGuiElementRenderState.class;
    }

    @Override
    protected void render(OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState, MatrixStack matrixStack) {
        boolean bl;
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        ItemGuiElementRenderState itemGuiElementRenderState = oversizedItemGuiElementRenderState.guiItemRenderState();
        ScreenRect screenRect = itemGuiElementRenderState.oversizedBounds();
        Objects.requireNonNull(screenRect);
        float f = (float)(screenRect.getLeft() + screenRect.getRight()) / 2.0f;
        float g = (float)(screenRect.getTop() + screenRect.getBottom()) / 2.0f;
        float h = (float)itemGuiElementRenderState.x() + 8.0f;
        float i = (float)itemGuiElementRenderState.y() + 8.0f;
        matrixStack.translate((h - f) / 16.0f, (g - i) / 16.0f, 0.0f);
        KeyedItemRenderState keyedItemRenderState = itemGuiElementRenderState.state();
        boolean bl2 = bl = !keyedItemRenderState.isSideLit();
        if (bl) {
            MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        } else {
            MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
        }
        keyedItemRenderState.render(matrixStack, this.vertexConsumers, 0xF000F0, OverlayTexture.DEFAULT_UV);
        this.modelKey = keyedItemRenderState.getModelKey();
    }

    @Override
    public void renderElement(OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState, GuiRenderState guiRenderState) {
        super.renderElement(oversizedItemGuiElementRenderState, guiRenderState);
        this.oversized = true;
    }

    @Override
    public boolean shouldBypassScaling(OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState) {
        KeyedItemRenderState keyedItemRenderState = oversizedItemGuiElementRenderState.guiItemRenderState().state();
        return !keyedItemRenderState.isAnimated() && keyedItemRenderState.getModelKey().equals(this.modelKey);
    }

    @Override
    protected float getYOffset(int height, int windowScaleFactor) {
        return (float)height / 2.0f;
    }

    @Override
    protected String getName() {
        return "oversized_item";
    }
}

