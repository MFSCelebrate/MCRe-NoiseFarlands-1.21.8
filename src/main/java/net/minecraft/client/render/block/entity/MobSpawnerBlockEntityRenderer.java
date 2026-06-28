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
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
implements BlockEntityRenderer<MobSpawnerBlockEntity> {
    final private EntityRenderDispatcher entityRenderDispatcher;

    public MobSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    @Override
    public void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        World world = mobSpawnerBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
        Entity entity = mobSpawnerLogic.getRenderedEntity(world, mobSpawnerBlockEntity.getPos());
        if (entity != null) {
            MobSpawnerBlockEntityRenderer.render(f, matrixStack, vertexConsumerProvider, i, entity, this.entityRenderDispatcher, mobSpawnerLogic.getLastRotation(), mobSpawnerLogic.getRotation());
        }
    }

    public static void render(float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, EntityRenderDispatcher entityRenderDispatcher, double lastRotation, double rotation) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);
        float f = 0.53125f;
        float g = Math.max(entity.getWidth(), entity.getHeight());
        if ((double)g > 1.0) {
            f /= g;
        }
        matrices.translate(0.0f, 0.4f, 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)MathHelper.lerp((double)tickProgress, lastRotation, rotation) * 10.0f));
        matrices.translate(0.0f, -0.2f, 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-30.0f));
        matrices.scale(f, f, f);
        entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, tickProgress, matrices, vertexConsumers, light);
        matrices.pop();
    }
}

