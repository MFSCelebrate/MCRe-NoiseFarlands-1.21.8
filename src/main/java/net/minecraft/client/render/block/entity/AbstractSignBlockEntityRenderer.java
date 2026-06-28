/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSignBlockEntityRenderer
implements BlockEntityRenderer<SignBlockEntity> {
    final static private int GLOWING_BLACK_TEXT_COLOR = -988212;
    final static private int MAX_COLORED_TEXT_OUTLINE_RENDER_DISTANCE = MathHelper.square(16);
    final private TextRenderer textRenderer;

    public AbstractSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.getTextRenderer();
    }

    protected abstract Model getModel(BlockState var1, WoodType var2);

    protected abstract SpriteIdentifier getTextureId(WoodType var1);

    protected abstract float getSignScale();

    protected abstract float getTextScale();

    protected abstract Vec3d getTextOffset();

    protected abstract void applyTransforms(MatrixStack var1, float var2, BlockState var3);

    @Override
    public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        BlockState blockState = signBlockEntity.getCachedState();
        AbstractSignBlock abstractSignBlock = (AbstractSignBlock)blockState.getBlock();
        Model model = this.getModel(blockState, abstractSignBlock.getWoodType());
        this.render(signBlockEntity, matrixStack, vertexConsumerProvider, i, j, blockState, abstractSignBlock, abstractSignBlock.getWoodType(), model);
    }

    private void render(SignBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BlockState state, AbstractSignBlock block, WoodType woodType, Model model) {
        matrices.push();
        this.applyTransforms(matrices, -block.getRotationDegrees(state), state);
        this.renderSign(matrices, vertexConsumers, light, overlay, woodType, model);
        this.renderText(blockEntity.getPos(), blockEntity.getFrontText(), matrices, vertexConsumers, light, blockEntity.getTextLineHeight(), blockEntity.getMaxTextWidth(), true);
        this.renderText(blockEntity.getPos(), blockEntity.getBackText(), matrices, vertexConsumers, light, blockEntity.getTextLineHeight(), blockEntity.getMaxTextWidth(), false);
        matrices.pop();
    }

    protected void renderSign(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, WoodType woodType, Model model) {
        matrices.push();
        float f = this.getSignScale();
        matrices.scale(f, -f, -f);
        SpriteIdentifier spriteIdentifier = this.getTextureId(woodType);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, model::getLayer);
        model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }

    private void renderText(BlockPos pos, SignText text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int textLineHeight, int maxTextWidth, boolean front) {
        int l;
        boolean bl;
        int k;
        matrices.push();
        this.applyTextTransforms(matrices, front, this.getTextOffset());
        int i = AbstractSignBlockEntityRenderer.getTextColor(text);
        int j = 4 * textLineHeight / 2;
        OrderedText[] orderedTexts = text.getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), textx -> {
            List<OrderedText> list = this.textRenderer.wrapLines((StringVisitable)textx, maxTextWidth);
            return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
        });
        if (text.isGlowing()) {
            k = text.getColor().getSignColor();
            bl = AbstractSignBlockEntityRenderer.shouldRenderTextOutline(pos, k);
            l = 0xF000F0;
        } else {
            k = i;
            bl = false;
            l = light;
        }
        for (int m = 0; m < 4; ++m) {
            OrderedText orderedText = orderedTexts[m];
            float f = -this.textRenderer.getWidth(orderedText) / 2;
            if (bl) {
                this.textRenderer.drawWithOutline(orderedText, f, m * textLineHeight - j, k, i, matrices.peek().getPositionMatrix(), vertexConsumers, l);
                continue;
            }
            this.textRenderer.draw(orderedText, f, (float)(m * textLineHeight - j), k, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, l);
        }
        matrices.pop();
    }

    private void applyTextTransforms(MatrixStack matrices, boolean front, Vec3d textOffset) {
        if (!front) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }
        float f = 0.015625f * this.getTextScale();
        matrices.translate(textOffset);
        matrices.scale(f, -f, f);
    }

    private static boolean shouldRenderTextOutline(BlockPos pos, int color) {
        if (color == DyeColor.BLACK.getSignColor()) {
            return true;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
            return true;
        }
        Entity entity = minecraftClient.getCameraEntity();
        return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(pos)) < (double)MAX_COLORED_TEXT_OUTLINE_RENDER_DISTANCE;
    }

    public static int getTextColor(SignText text) {
        int i = text.getColor().getSignColor();
        if (i == DyeColor.BLACK.getSignColor() && text.isGlowing()) {
            return -988212;
        }
        return ColorHelper.scaleRgb(i, 0.4f);
    }
}

