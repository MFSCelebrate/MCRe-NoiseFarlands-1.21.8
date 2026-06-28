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
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class VaultBlockEntityRenderer
implements BlockEntityRenderer<VaultBlockEntity> {
    final private ItemModelManager itemModelManager;
    final private Random random = Random.create();
    final private ItemStackEntityRenderState itemRenderState = new ItemStackEntityRenderState();

    public VaultBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public void render(VaultBlockEntity vaultBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        if (!VaultBlockEntity.Client.hasDisplayItem(vaultBlockEntity.getSharedData())) {
            return;
        }
        World world = vaultBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        ItemStack itemStack = vaultBlockEntity.getSharedData().getDisplayItem();
        if (itemStack.isEmpty()) {
            return;
        }
        this.itemModelManager.clearAndUpdate(this.itemRenderState.itemRenderState, itemStack, ItemDisplayContext.GROUND, world, null, 0);
        this.itemRenderState.renderedAmount = ItemStackEntityRenderState.getRenderedAmount(itemStack.getCount());
        this.itemRenderState.seed = ItemStackEntityRenderState.getSeed(itemStack);
        VaultClientData vaultClientData = vaultBlockEntity.getClientData();
        matrixStack.push();
        matrixStack.translate(0.5f, 0.4f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerpAngleDegrees(f, vaultClientData.getLastDisplayRotation(), vaultClientData.getDisplayRotation())));
        ItemEntityRenderer.render(matrixStack, vertexConsumerProvider, i, this.itemRenderState, this.random);
        matrixStack.pop();
    }
}

