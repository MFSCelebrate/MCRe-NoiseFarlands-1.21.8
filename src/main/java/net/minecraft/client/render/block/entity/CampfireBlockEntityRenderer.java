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
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class CampfireBlockEntityRenderer
implements BlockEntityRenderer<CampfireBlockEntity> {
    final static private float SCALE = 0.375f;
    final private ItemRenderer itemRenderer;

    public CampfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(CampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
        DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();
        int k = (int)campfireBlockEntity.getPos().asLong();
        for (int l = 0; l < defaultedList.size(); ++l) {
            ItemStack itemStack = defaultedList.get(l);
            if (itemStack == ItemStack.EMPTY) continue;
            matrixStack.push();
            matrixStack.translate(0.5f, 0.44921875f, 0.5f);
            Direction direction2 = Direction.fromHorizontalQuarterTurns((l + direction.getHorizontalQuarterTurns()) % 4);
            float g = -direction2.getPositiveHorizontalDegrees();
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            matrixStack.translate(-0.3125f, -0.3125f, 0.0f);
            matrixStack.scale(0.375f, 0.375f, 0.375f);
            this.itemRenderer.renderItem(itemStack, ItemDisplayContext.FIXED, i, j, matrixStack, vertexConsumerProvider, campfireBlockEntity.getWorld(), k + l);
            matrixStack.pop();
        }
    }
}

