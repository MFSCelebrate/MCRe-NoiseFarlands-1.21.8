/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.debug;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.render.debug.BreezeDebugRenderer;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.client.render.debug.ChunkDebugRenderer;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.CollisionDebugRenderer;
import net.minecraft.client.render.debug.GameEventDebugRenderer;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.LightDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.OctreeDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.RedstoneUpdateOrderDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.SupportingBlockDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.render.debug.WaterDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class DebugRenderer {
    final public PathfindingDebugRenderer pathfindingDebugRenderer = new PathfindingDebugRenderer();
    final public Renderer waterDebugRenderer;
    final public Renderer chunkBorderDebugRenderer;
    final public Renderer heightmapDebugRenderer;
    final public Renderer collisionDebugRenderer;
    final public Renderer supportingBlockDebugRenderer;
    final public NeighborUpdateDebugRenderer neighborUpdateDebugRenderer;
    final public RedstoneUpdateOrderDebugRenderer redstoneUpdateOrderDebugRenderer;
    final public StructureDebugRenderer structureDebugRenderer;
    final public Renderer skyLightDebugRenderer;
    final public Renderer worldGenAttemptDebugRenderer;
    final public Renderer blockOutlineDebugRenderer;
    final public Renderer chunkLoadingDebugRenderer;
    final public VillageDebugRenderer villageDebugRenderer;
    final public VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    final public BeeDebugRenderer beeDebugRenderer;
    final public RaidCenterDebugRenderer raidCenterDebugRenderer;
    final public GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    final public GameTestDebugRenderer gameTestDebugRenderer;
    final public GameEventDebugRenderer gameEventDebugRenderer;
    final public LightDebugRenderer lightDebugRenderer;
    final public BreezeDebugRenderer breezeDebugRenderer;
    final public ChunkDebugRenderer chunkDebugRenderer;
    final public OctreeDebugRenderer octreeDebugRenderer;
    private boolean showChunkBorder;
    private boolean showOctree;

    public DebugRenderer(MinecraftClient client) {
        this.waterDebugRenderer = new WaterDebugRenderer(client);
        this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(client);
        this.heightmapDebugRenderer = new HeightmapDebugRenderer(client);
        this.collisionDebugRenderer = new CollisionDebugRenderer(client);
        this.supportingBlockDebugRenderer = new SupportingBlockDebugRenderer(client);
        this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(client);
        this.redstoneUpdateOrderDebugRenderer = new RedstoneUpdateOrderDebugRenderer(client);
        this.structureDebugRenderer = new StructureDebugRenderer(client);
        this.skyLightDebugRenderer = new SkyLightDebugRenderer(client);
        this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer();
        this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(client);
        this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(client);
        this.villageDebugRenderer = new VillageDebugRenderer(client);
        this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
        this.beeDebugRenderer = new BeeDebugRenderer(client);
        this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(client);
        this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(client);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
        this.gameEventDebugRenderer = new GameEventDebugRenderer(client);
        this.lightDebugRenderer = new LightDebugRenderer(client, LightType.SKY);
        this.breezeDebugRenderer = new BreezeDebugRenderer(client);
        this.chunkDebugRenderer = new ChunkDebugRenderer(client);
        this.octreeDebugRenderer = new OctreeDebugRenderer(client);
    }

    public void reset() {
        this.pathfindingDebugRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderDebugRenderer.clear();
        this.heightmapDebugRenderer.clear();
        this.collisionDebugRenderer.clear();
        this.supportingBlockDebugRenderer.clear();
        this.neighborUpdateDebugRenderer.clear();
        this.structureDebugRenderer.clear();
        this.skyLightDebugRenderer.clear();
        this.worldGenAttemptDebugRenderer.clear();
        this.blockOutlineDebugRenderer.clear();
        this.chunkLoadingDebugRenderer.clear();
        this.villageDebugRenderer.clear();
        this.villageSectionsDebugRenderer.clear();
        this.beeDebugRenderer.clear();
        this.raidCenterDebugRenderer.clear();
        this.goalSelectorDebugRenderer.clear();
        this.gameTestDebugRenderer.clear();
        this.gameEventDebugRenderer.clear();
        this.lightDebugRenderer.clear();
        this.breezeDebugRenderer.clear();
        this.chunkDebugRenderer.clear();
    }

    public boolean toggleShowChunkBorder() {
        this.showChunkBorder = !this.showChunkBorder;
        return this.showChunkBorder;
    }

    public boolean toggleShowOctree() {
        this.showOctree = !this.showOctree;
        return this.showOctree;
    }

    public void render(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            this.chunkBorderDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        }
        if (this.showOctree) {
            this.octreeDebugRenderer.render(matrices, frustum, vertexConsumers, cameraX, cameraY, cameraZ);
        }
        this.gameTestDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    public void renderLate(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        this.chunkDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity entity, int maxDistance) {
        int i;
        Box box;
        Vec3d vec3d2;
        Vec3d vec3d3;
        if (entity == null) {
            return Optional.empty();
        }
        Vec3d vec3d = entity.getEyePos();
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3 = vec3d.add(vec3d2 = entity.getRotationVec(1.0f).multiply(maxDistance)), box = entity.getBoundingBox().stretch(vec3d2).expand(1.0), EntityPredicates.CAN_HIT, i = maxDistance * maxDistance);
        if (entityHitResult == null) {
            return Optional.empty();
        }
        if (vec3d.squaredDistanceTo(entityHitResult.getPos()) > (double)i) {
            return Optional.empty();
        }
        return Optional.of(entityHitResult.getEntity());
    }

    public static void drawBlockBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, float red, float green, float blue, float alpha) {
        DebugRenderer.drawBox(matrices, vertexConsumers, pos, pos.net_minecraft_util_math_BlockPos_add(1, 1, 1), red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos1, BlockPos pos2, float red, float green, float blue, float alpha) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos().negate();
        Box box = Box.enclosing(pos1, pos2).offset(vec3d);
        DebugRenderer.drawBox(matrices, vertexConsumers, box, red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, float expand, float red, float green, float blue, float alpha) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos().negate();
        Box box = new Box(pos).offset(vec3d).expand(expand);
        DebugRenderer.drawBox(matrices, vertexConsumers, box, red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float red, float green, float blue, float alpha) {
        DebugRenderer.drawBox(matrices, vertexConsumers, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
        VertexRendering.drawFilledBox(matrices, vertexConsumer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, int x, int y, int z, int color) {
        DebugRenderer.drawString(matrices, vertexConsumers, string, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, color);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color) {
        DebugRenderer.drawString(matrices, vertexConsumers, string, x, y, z, color, 0.02f);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color, float size) {
        DebugRenderer.drawString(matrices, vertexConsumers, string, x, y, z, color, size, true, 0.0f, false);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color, float size, boolean center, float offset, boolean visibleThroughObjects) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Camera camera = minecraftClient.gameRenderer.getCamera();
        if (!camera.isReady() || minecraftClient.getEntityRenderDispatcher().gameOptions == null) {
            return;
        }
        TextRenderer textRenderer = minecraftClient.textRenderer;
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        matrices.push();
        matrices.translate((float)(x - d), (float)(y - e) + 0.07f, (float)(z - f));
        matrices.multiply((Quaternionfc)camera.getRotation());
        matrices.scale(size, -size, size);
        float g = center ? (float)(-textRenderer.getWidth(string)) / 2.0f : 0.0f;
        textRenderer.draw(string, g -= offset / size, 0.0f, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, visibleThroughObjects ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        matrices.pop();
    }

    private static Vec3d hueToRgb(float hue) {
        float f = 5.99999f;
        int i = (int)(MathHelper.clamp(hue, 0.0f, 1.0f) * 5.99999f);
        float g = hue * 5.99999f - (float)i;
        return switch (i) {
            case 0 -> new Vec3d(1.0, g, 0.0);
            case 1 -> new Vec3d(1.0f - g, 1.0, 0.0);
            case 2 -> new Vec3d(0.0, 1.0, g);
            case 3 -> new Vec3d(0.0, 1.0 - (double)g, 1.0);
            case 4 -> new Vec3d(g, 0.0, 1.0);
            case 5 -> new Vec3d(1.0, 0.0, 1.0 - (double)g);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    private static Vec3d shiftHue(float r, float g, float b, float dHue) {
        Vec3d vec3d = DebugRenderer.hueToRgb(dHue).multiply(r);
        Vec3d vec3d2 = DebugRenderer.hueToRgb((dHue + 0.33333334f) % 1.0f).multiply(g);
        Vec3d vec3d3 = DebugRenderer.hueToRgb((dHue + 0.6666667f) % 1.0f).multiply(b);
        Vec3d vec3d4 = vec3d.add(vec3d2).add(vec3d3);
        double d = Math.max(Math.max(1.0, vec3d4.x), Math.max(vec3d4.y, vec3d4.z));
        return new Vec3d(vec3d4.x / d, vec3d4.y / d, vec3d4.z / d);
    }

    public static void drawVoxelShapeOutlines(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha, boolean bl) {
        List<Box> list = shape.getBoundingBoxes();
        if (list.isEmpty()) {
            return;
        }
        int i = bl ? list.size() : list.size() * 8;
        VertexRendering.drawOutline(matrices, vertexConsumer, VoxelShapes.cuboid(list.get(0)), offsetX, offsetY, offsetZ, ColorHelper.fromFloats(alpha, red, green, blue));
        for (int j = 1; j < list.size(); ++j) {
            Box box = list.get(j);
            float f = (float)j / (float)i;
            Vec3d vec3d = DebugRenderer.shiftHue(red, green, blue, f);
            VertexRendering.drawOutline(matrices, vertexConsumer, VoxelShapes.cuboid(box), offsetX, offsetY, offsetZ, ColorHelper.fromFloats(alpha, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Renderer {
        public void render(MatrixStack var1, VertexConsumerProvider var2, double var3, double var5, double var7);

        default public void clear() {
        }
    }
}

