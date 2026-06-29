/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderer {
    final static public Identifier ENTITY_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_armor.png");
    final static public Identifier ITEM_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_item.png");
    final static public float field_60154 = 0.5f;
    final static public float field_60155 = 0.75f;
    final static public float field_60156 = 0.0078125f;
    final static public int field_55295 = -1;
    final private ItemModelManager itemModelManager;
    final private ItemRenderState itemRenderState = new ItemRenderState();

    public ItemRenderer(ItemModelManager itemModelManager) {
        this.itemModelManager = itemModelManager;
    }

    public static void renderItem(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int[] tints, List<BakedQuad> quads, RenderLayer layer, ItemRenderState.Glint glint) {
        VertexConsumer vertexConsumer;
        if (glint == ItemRenderState.Glint.SPECIAL) {
            MatrixStack.Entry entry = matrices.peek().copy();
            if (displayContext == ItemDisplayContext.GUI) {
                MatrixUtil.scale(entry.getPositionMatrix(), 0.5f);
            } else if (displayContext.isFirstPerson()) {
                MatrixUtil.scale(entry.getPositionMatrix(), 0.75f);
            }
            vertexConsumer = ItemRenderer.getSpecialItemGlintConsumer(vertexConsumers, layer, entry);
        } else {
            vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, layer, true, glint != ItemRenderState.Glint.NONE);
        }
        ItemRenderer.renderBakedItemQuads(matrices, vertexConsumer, quads, tints, light, overlay);
    }

    public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean glint) {
        if (glint) {
            return VertexConsumers.union(provider.getBuffer(RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer));
        }
        return provider.getBuffer(layer);
    }

    private static VertexConsumer getSpecialItemGlintConsumer(VertexConsumerProvider consumers, RenderLayer layer, MatrixStack.Entry matrix) {
        return VertexConsumers.union((VertexConsumer)new OverlayVertexConsumer(consumers.getBuffer(ItemRenderer.useTranslucentGlint(layer) ? RenderLayer.getGlintTranslucent() : RenderLayer.getGlint()), matrix, 0.0078125f), consumers.getBuffer(layer));
    }

    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            if (ItemRenderer.useTranslucentGlint(layer)) {
                return VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlintTranslucent()), vertexConsumers.getBuffer(layer));
            }
            return VertexConsumers.union(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer));
        }
        return vertexConsumers.getBuffer(layer);
    }

    private static boolean useTranslucentGlint(RenderLayer renderLayer) {
        return MinecraftClient.isFabulousGraphicsOrBetter() && renderLayer == TexturedRenderLayers.getItemEntityTranslucentCull();
    }

    private static int getTint(int[] tints, int index) {
        if (index < 0 || index >= tints.length) {
            return -1;
        }
        return tints[index];
    }

    private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay) {
        MatrixStack.Entry entry = matrices.peek();
        for (BakedQuad bakedQuad : quads) {
            float j;
            float h;
            float g;
            float f;
            if (bakedQuad.hasTint()) {
                int i = ItemRenderer.getTint(tints, bakedQuad.tintIndex());
                f = (float)ColorHelper.getAlpha(i) / 255.0f;
                g = (float)ColorHelper.getRed(i) / 255.0f;
                h = (float)ColorHelper.getGreen(i) / 255.0f;
                j = (float)ColorHelper.getBlue(i) / 255.0f;
            } else {
                f = 1.0f;
                g = 1.0f;
                h = 1.0f;
                j = 1.0f;
            }
            vertexConsumer.quad(entry, bakedQuad, g, h, j, f, light, overlay);
        }
    }

    public void renderItem(ItemStack stack, ItemDisplayContext displayContext, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int seed) {
        this.renderItem(null, stack, displayContext, matrices, vertexConsumers, world, light, overlay, seed);
    }

    public void renderItem(@Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {
        this.itemModelManager.clearAndUpdate(this.itemRenderState, stack, displayContext, world, entity, seed);
        this.itemRenderState.render(matrices, vertexConsumers, light, overlay);
    }
}

