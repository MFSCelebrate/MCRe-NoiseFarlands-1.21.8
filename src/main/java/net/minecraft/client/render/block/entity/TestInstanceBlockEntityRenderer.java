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
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class TestInstanceBlockEntityRenderer
implements BlockEntityRenderer<TestInstanceBlockEntity> {
    final private BeaconBlockEntityRenderer<TestInstanceBlockEntity> beaconBlockEntityRenderer;
    final private StructureBlockBlockEntityRenderer<TestInstanceBlockEntity> structureBlockBlockEntityRenderer;

    public TestInstanceBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.beaconBlockEntityRenderer = new BeaconBlockEntityRenderer(context);
        this.structureBlockBlockEntityRenderer = new StructureBlockBlockEntityRenderer(context);
    }

    @Override
    public void render(TestInstanceBlockEntity testInstanceBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        this.beaconBlockEntityRenderer.render(testInstanceBlockEntity, f, matrixStack, vertexConsumerProvider, i, j, vec3d);
        this.structureBlockBlockEntityRenderer.render(testInstanceBlockEntity, f, matrixStack, vertexConsumerProvider, i, j, vec3d);
    }

    @Override
    public boolean rendersOutsideBoundingBox() {
        return this.beaconBlockEntityRenderer.rendersOutsideBoundingBox() || this.structureBlockBlockEntityRenderer.rendersOutsideBoundingBox();
    }

    @Override
    public int getRenderDistance() {
        return Math.max(this.beaconBlockEntityRenderer.getRenderDistance(), this.structureBlockBlockEntityRenderer.getRenderDistance());
    }

    @Override
    public boolean isInRenderDistance(TestInstanceBlockEntity testInstanceBlockEntity, Vec3d vec3d) {
        return this.beaconBlockEntityRenderer.isInRenderDistance(testInstanceBlockEntity, vec3d) || this.structureBlockBlockEntityRenderer.isInRenderDistance(testInstanceBlockEntity, vec3d);
    }
}

