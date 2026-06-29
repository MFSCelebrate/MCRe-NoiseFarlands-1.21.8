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
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ItemEntityRenderer
extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
    final static private float field_56954 = 0.0625f;
    final static private float field_32924 = 0.15f;
    final static private float field_56955 = 0.0625f;
    final private ItemModelManager itemModelManager;
    final private Random random = Random.create();

    public ItemEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    @Override
    public ItemEntityRenderState net_minecraft_client_render_entity_state_ItemEntityRenderState_createRenderState() {
        return new ItemEntityRenderState();
    }

    @Override
    public void updateRenderState(ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float f) {
        super.updateRenderState(itemEntity, itemEntityRenderState, f);
        itemEntityRenderState.age = (float)itemEntity.getItemAge() + f;
        itemEntityRenderState.uniqueOffset = itemEntity.uniqueOffset;
        itemEntityRenderState.update(itemEntity, itemEntity.getStack(), this.itemModelManager);
    }

    @Override
    public void render(ItemEntityRenderState itemEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (itemEntityRenderState.itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        Box box = itemEntityRenderState.itemRenderState.getModelBoundingBox();
        float f = -((float)box.minY) + 0.0625f;
        float g = MathHelper.sin(itemEntityRenderState.age / 10.0f + itemEntityRenderState.uniqueOffset) * 0.1f + 0.1f;
        matrixStack.translate(0.0f, g + f, 0.0f);
        float h = ItemEntity.getRotation(itemEntityRenderState.age, itemEntityRenderState.uniqueOffset);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(h));
        ItemEntityRenderer.renderStack(matrixStack, vertexConsumerProvider, 1, itemEntityRenderState, this.random, box);
        matrixStack.pop();
        super.render(itemEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    public static void render(MatrixStack matrices, VertexConsumerProvider provider, int light, ItemStackEntityRenderState state, Random random) {
        ItemEntityRenderer.renderStack(matrices, provider, light, state, random, state.itemRenderState.getModelBoundingBox());
    }

    public static void renderStack(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStackEntityRenderState state, Random random, Box box) {
        int i = state.renderedAmount;
        if (i == 0) {
            return;
        }
        random.setSeed(state.seed);
        ItemRenderState itemRenderState = state.itemRenderState;
        float f = (float)box.getLengthZ();
        if (f > 0.0625f) {
            itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            for (int j = 1; j < i; ++j) {
                matrices.push();
                float g = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float h = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float k = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                matrices.translate(g, h, k);
                itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            }
        } else {
            float l = f * 1.5f;
            matrices.translate(0.0f, 0.0f, -(l * (float)(i - 1) / 2.0f));
            itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.translate(0.0f, 0.0f, l);
            for (int m = 1; m < i; ++m) {
                matrices.push();
                float h = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                float k = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                matrices.translate(h, k, 0.0f);
                itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
                matrices.translate(0.0f, 0.0f, l);
            }
        }
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ItemEntityRenderState_createRenderState();
    }
}

