/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
implements BlockEntityRenderer<EnchantingTableBlockEntity> {
    final static public SpriteIdentifier BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/enchanting_table_book"));
    final private BookModel book;

    public EnchantingTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(EnchantingTableBlockEntity enchantingTableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        float h;
        matrixStack.push();
        matrixStack.translate(0.5f, 0.75f, 0.5f);
        float g = (float)enchantingTableBlockEntity.ticks + f;
        matrixStack.translate(0.0f, 0.1f + MathHelper.sin(g * 0.1f) * 0.01f, 0.0f);
        for (h = enchantingTableBlockEntity.bookRotation - enchantingTableBlockEntity.lastBookRotation; h >= (float)Math.PI; h -= (float)Math.PI * 2) {
        }
        while (h < (float)(-Math.PI)) {
            h += (float)Math.PI * 2;
        }
        float k = enchantingTableBlockEntity.lastBookRotation + h * f;
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(-k));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(80.0f));
        float l = MathHelper.lerp(f, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25f) * 1.6f - 0.3f;
        float n = MathHelper.fractionalPart(l + 0.75f) * 1.6f - 0.3f;
        float o = MathHelper.lerp(f, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0f, 1.0f), MathHelper.clamp(n, 0.0f, 1.0f), o);
        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.book.render(matrixStack, vertexConsumer, i, j);
        matrixStack.pop();
    }
}

