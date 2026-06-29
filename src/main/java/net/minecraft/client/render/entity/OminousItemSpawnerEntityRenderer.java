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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class OminousItemSpawnerEntityRenderer
extends EntityRenderer<OminousItemSpawnerEntity, ItemStackEntityRenderState> {
    final static private float field_50231 = 40.0f;
    final static private int field_50232 = 50;
    final private ItemModelManager itemModelManager;
    final private Random random = Random.create();

    protected OminousItemSpawnerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public ItemStackEntityRenderState net_minecraft_client_render_entity_state_ItemStackEntityRenderState_createRenderState() {
        return new ItemStackEntityRenderState();
    }

    @Override
    public void updateRenderState(OminousItemSpawnerEntity ominousItemSpawnerEntity, ItemStackEntityRenderState itemStackEntityRenderState, float f) {
        super.updateRenderState(ominousItemSpawnerEntity, itemStackEntityRenderState, f);
        ItemStack itemStack = ominousItemSpawnerEntity.getItem();
        itemStackEntityRenderState.update(ominousItemSpawnerEntity, itemStack, this.itemModelManager);
    }

    @Override
    public void render(ItemStackEntityRenderState itemStackEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float f;
        if (itemStackEntityRenderState.itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        if (itemStackEntityRenderState.age <= 50.0f) {
            f = Math.min(itemStackEntityRenderState.age, 50.0f) / 50.0f;
            matrixStack.scale(f, f, f);
        }
        f = MathHelper.wrapDegrees(itemStackEntityRenderState.age * 40.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f));
        ItemEntityRenderer.render(matrixStack, vertexConsumerProvider, 0xF000F0, itemStackEntityRenderState, this.random);
        matrixStack.pop();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ItemStackEntityRenderState_createRenderState();
    }
}

