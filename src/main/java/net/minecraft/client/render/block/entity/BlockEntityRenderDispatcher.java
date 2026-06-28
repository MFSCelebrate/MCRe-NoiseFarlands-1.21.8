/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderDispatcher
implements SynchronousResourceReloader {
    private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
    final private TextRenderer textRenderer;
    final private Supplier<LoadedEntityModels> entityModelsGetter;
    public World world;
    public Camera camera;
    public HitResult crosshairTarget;
    final private BlockRenderManager blockRenderManager;
    final private ItemModelManager itemModelManager;
    final private ItemRenderer itemRenderer;
    final private EntityRenderDispatcher entityRenderDispatcher;

    public BlockEntityRenderDispatcher(TextRenderer textRenderer, Supplier<LoadedEntityModels> entityModelsGetter, BlockRenderManager blockRenderManager, ItemModelManager itemModelManager, ItemRenderer itemRenderer, EntityRenderDispatcher entityRenderDispatcher) {
        this.itemRenderer = itemRenderer;
        this.itemModelManager = itemModelManager;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.textRenderer = textRenderer;
        this.entityModelsGetter = entityModelsGetter;
        this.blockRenderManager = blockRenderManager;
    }

    @Nullable
    public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
        return this.renderers.get(blockEntity.getType());
    }

    public void configure(World world, Camera camera, HitResult crosshairTarget) {
        if (this.world != world) {
            this.setWorld(world);
        }
        this.camera = camera;
        this.crosshairTarget = crosshairTarget;
    }

    public <E extends BlockEntity> void render(E blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer == null) {
            return;
        }
        if (!blockEntity.hasWorld() || !blockEntity.getType().supports(blockEntity.getCachedState())) {
            return;
        }
        if (!blockEntityRenderer.isInRenderDistance(blockEntity, this.camera.getPos())) {
            return;
        }
        try {
            BlockEntityRenderDispatcher.render(blockEntityRenderer, blockEntity, tickProgress, matrices, vertexConsumers, this.camera.getPos());
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering Block Entity");
            CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
            blockEntity.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    private static <T extends BlockEntity> void render(BlockEntityRenderer<T> renderer, T blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos) {
        World world = blockEntity.getWorld();
        int i = world != null ? WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos()) : 0xF000F0;
        renderer.render(blockEntity, tickProgress, matrices, vertexConsumers, i, OverlayTexture.DEFAULT_UV, cameraPos);
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    @Override
    public void reload(ResourceManager manager) {
        BlockEntityRendererFactory.Context context = new BlockEntityRendererFactory.Context(this, this.blockRenderManager, this.itemModelManager, this.itemRenderer, this.entityRenderDispatcher, this.entityModelsGetter.get(), this.textRenderer);
        this.renderers = BlockEntityRendererFactories.reload(context);
    }
}

