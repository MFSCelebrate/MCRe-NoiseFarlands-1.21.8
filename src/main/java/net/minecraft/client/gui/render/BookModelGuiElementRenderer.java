/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.BookModelGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class BookModelGuiElementRenderer
extends SpecialGuiElementRenderer<BookModelGuiElementRenderState> {
    public BookModelGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    @Override
    public Class<BookModelGuiElementRenderState> getElementClass() {
        return BookModelGuiElementRenderState.class;
    }

    @Override
    protected void render(BookModelGuiElementRenderState bookModelGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ENTITY_IN_UI);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(25.0f));
        float f = bookModelGuiElementRenderState.open();
        matrixStack.translate((1.0f - f) * 0.2f, (1.0f - f) * 0.1f, (1.0f - f) * 0.25f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-(1.0f - f) * 90.0f - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        float g = bookModelGuiElementRenderState.flip();
        float h = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25f) * 1.6f - 0.3f, 0.0f, 1.0f);
        float i = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75f) * 1.6f - 0.3f, 0.0f, 1.0f);
        BookModel bookModel = bookModelGuiElementRenderState.bookModel();
        bookModel.setPageAngles(0.0f, h, i, f);
        Identifier identifier = bookModelGuiElementRenderState.texture();
        VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(bookModel.getLayer(identifier));
        bookModel.render(matrixStack, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected float getYOffset(int height, int windowScaleFactor) {
        return 17 * windowScaleFactor;
    }

    @Override
    protected String getName() {
        return "book model";
    }
}

