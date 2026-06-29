/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class TrialSpawnerBlockEntityRenderer
implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
    final private EntityRenderDispatcher entityRenderDispatcher;

    public TrialSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderDispatcher();
    }

    @Override
    public void render(TrialSpawnerBlockEntity trialSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        World world = trialSpawnerBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        TrialSpawnerLogic trialSpawnerLogic = trialSpawnerBlockEntity.getSpawner();
        TrialSpawnerData trialSpawnerData = trialSpawnerLogic.getData();
        Entity entity = trialSpawnerData.setDisplayEntity(trialSpawnerLogic, world, trialSpawnerLogic.getSpawnerState());
        if (entity != null) {
            MobSpawnerBlockEntityRenderer.render(f, matrixStack, vertexConsumerProvider, i, entity, this.entityRenderDispatcher, trialSpawnerData.getLastDisplayEntityRotation(), trialSpawnerData.getDisplayEntityRotation());
        }
    }
}

