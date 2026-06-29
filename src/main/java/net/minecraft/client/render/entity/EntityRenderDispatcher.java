/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.EntityDebugInfo;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class EntityRenderDispatcher
implements SynchronousResourceReloader {
    final static private RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(Identifier.ofVanilla("textures/misc/shadow.png"));
    final static private float field_43377 = 32.0f;
    final static private float field_43378 = 0.5f;
    private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers = Map.of();
    final public TextureManager textureManager;
    private World world;
    public Camera camera;
    private Quaternionf rotation;
    public Entity targetedEntity;
    final private ItemModelManager itemModelManager;
    final private MapRenderer mapRenderer;
    final private BlockRenderManager blockRenderManager;
    final private HeldItemRenderer heldItemRenderer;
    final private TextRenderer textRenderer;
    final public GameOptions gameOptions;
    final private Supplier<LoadedEntityModels> entityModelsGetter;
    final private EquipmentModelLoader equipmentModelLoader;
    private boolean renderShadows = true;
    private boolean renderHitboxes;

    public <E extends Entity> int getLight(E entity, float tickProgress) {
        return this.getRenderer((EntityRenderState)((Object)entity)).getLight(entity, tickProgress);
    }

    public EntityRenderDispatcher(MinecraftClient client, TextureManager textureManager, ItemModelManager itemModelManager, ItemRenderer itemRenderer, MapRenderer mapRenderer, BlockRenderManager blockRenderManager, TextRenderer textRenderer, GameOptions gameOptions, Supplier<LoadedEntityModels> entityModelsGetter, EquipmentModelLoader equipmentModelLoader) {
        this.textureManager = textureManager;
        this.itemModelManager = itemModelManager;
        this.mapRenderer = mapRenderer;
        this.heldItemRenderer = new HeldItemRenderer(client, this, itemRenderer, itemModelManager);
        this.blockRenderManager = blockRenderManager;
        this.textRenderer = textRenderer;
        this.gameOptions = gameOptions;
        this.entityModelsGetter = entityModelsGetter;
        this.equipmentModelLoader = equipmentModelLoader;
    }

    public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
            SkinTextures.Model model = abstractClientPlayerEntity.getSkinTextures().model();
            EntityRenderer<? extends PlayerEntity, ?> entityRenderer = this.modelRenderers.get((Object)model);
            if (entityRenderer != null) {
                return entityRenderer;
            }
            return this.modelRenderers.get((Object)SkinTextures.Model.WIDE);
        }
        return this.renderers.get(entity.getType());
    }

    public <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S state) {
        if (state instanceof PlayerEntityRenderState) {
            PlayerEntityRenderState playerEntityRenderState = (PlayerEntityRenderState)state;
            SkinTextures.Model model = playerEntityRenderState.skinTextures.model();
            EntityRenderer<? extends PlayerEntity, ?> entityRenderer = this.modelRenderers.get((Object)model);
            if (entityRenderer != null) {
                return entityRenderer;
            }
            return this.modelRenderers.get((Object)SkinTextures.Model.WIDE);
        }
        return this.renderers.get(state.entityType);
    }

    public void configure(World world, Camera camera, Entity target) {
        this.world = world;
        this.camera = camera;
        this.rotation = camera.getRotation();
        this.targetedEntity = target;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setRenderHitboxes(boolean renderHitboxes) {
        this.renderHitboxes = renderHitboxes;
    }

    public boolean shouldRenderHitboxes() {
        return this.renderHitboxes;
    }

    public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double x, double y, double z) {
        EntityRenderer<?, E> entityRenderer = this.getRenderer((EntityRenderState)((Object)entity));
        return entityRenderer.shouldRender(entity, frustum, x, y, z);
    }

    public <E extends Entity> void render(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderer<?, E> entityRenderer = this.getRenderer((EntityRenderState)((Object)entity));
        this.render(entity, x, y, z, tickProgress, matrices, vertexConsumers, light, entityRenderer);
    }

    private <E extends Entity, S extends EntityRenderState> void render(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer) {
        S entityRenderState;
        try {
            entityRenderState = renderer.getAndUpdateRenderState(entity, tickProgress);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Extracting render state for an entity in world");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being extracted");
            entity.populateCrashReport(crashReportSection);
            CrashReportSection crashReportSection2 = this.addRendererDetails(x, y, z, renderer, crashReport);
            crashReportSection2.add("Delta", Float.valueOf(tickProgress));
            throw new CrashException(crashReport);
        }
        try {
            this.render(entityRenderState, x, y, z, matrices, vertexConsumers, light, renderer);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering entity in world");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
            entity.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public <S extends EntityRenderState> void render(S state, double x, double y, double z, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderer<?, S> entityRenderer = this.getRenderer(state);
        this.render(state, x, y, z, matrices, vertexConsumers, light, entityRenderer);
    }

    private <S extends EntityRenderState> void render(S state, double x, double y, double z, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<?, S> renderer) {
        try {
            double h;
            float i;
            float g;
            Vec3d vec3d = renderer.getPositionOffset(state);
            double d = x + vec3d.getX();
            double e = y + vec3d.getY();
            double f = z + vec3d.getZ();
            matrices.push();
            matrices.translate(d, e, f);
            renderer.render(state, matrices, vertexConsumers, light);
            if (state.onFire) {
                this.renderFire(matrices, vertexConsumers, state, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
            }
            if (state instanceof PlayerEntityRenderState) {
                matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
            }
            if (this.gameOptions.getEntityShadows().getValue().booleanValue() && this.renderShadows && !state.invisible && (g = renderer.getShadowRadius(state)) > 0.0f && (i = (float)((1.0 - (h = state.squaredDistanceToCamera) / 256.0) * (double)renderer.getShadowOpacity(state))) > 0.0f) {
                EntityRenderDispatcher.renderShadow(matrices, vertexConsumers, state, i, this.world, Math.min(g, 32.0f));
            }
            if (!(state instanceof PlayerEntityRenderState)) {
                matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
            }
            if (state.hitbox != null) {
                this.renderHitboxes(matrices, state, state.hitbox, vertexConsumers);
            }
            matrices.pop();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering entity in world");
            CrashReportSection crashReportSection = crashReport.addElement("EntityRenderState being rendered");
            state.addCrashReportDetails(crashReportSection);
            this.addRendererDetails(x, y, z, renderer, crashReport);
            throw new CrashException(crashReport);
        }
    }

    private <S extends EntityRenderState> CrashReportSection addRendererDetails(double x, double y, double z, EntityRenderer<?, S> renderer, CrashReport crashReport) {
        CrashReportSection crashReportSection = crashReport.addElement("Renderer details");
        crashReportSection.add("Assigned renderer", renderer);
        crashReportSection.add("Location", CrashReportSection.createPositionString((HeightLimitView)this.world, x, y, z));
        return crashReportSection;
    }

    private void renderHitboxes(MatrixStack matrices, EntityRenderState state, EntityHitboxAndView hitbox, VertexConsumerProvider vertexConsumers) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        EntityRenderDispatcher.renderHitboxes(matrices, hitbox, vertexConsumer, state.standingEyeHeight);
        EntityDebugInfo entityDebugInfo = state.debugInfo;
        if (entityDebugInfo != null) {
            if (entityDebugInfo.missing()) {
                EntityHitbox entityHitbox = (EntityHitbox)hitbox.hitboxes().getFirst();
                DebugRenderer.drawString(matrices, vertexConsumers, "Missing", state.x, entityHitbox.y1() + 1.5, state.z, Colors.RED);
            } else if (entityDebugInfo.hitboxes() != null) {
                matrices.push();
                matrices.translate(entityDebugInfo.serverEntityX() - state.x, entityDebugInfo.serverEntityY() - state.y, entityDebugInfo.serverEntityZ() - state.z);
                EntityRenderDispatcher.renderHitboxes(matrices, entityDebugInfo.hitboxes(), vertexConsumer, entityDebugInfo.eyeHeight());
                Vec3d vec3d = new Vec3d(entityDebugInfo.deltaMovementX(), entityDebugInfo.deltaMovementY(), entityDebugInfo.deltaMovementZ());
                VertexRendering.drawVector(matrices, vertexConsumer, new Vector3f(), vec3d, -256);
                matrices.pop();
            }
        }
    }

    private static void renderHitboxes(MatrixStack matrices, EntityHitboxAndView hitbox, VertexConsumer vertexConsumer, float standingEyeHeight) {
        for (EntityHitbox entityHitbox : hitbox.hitboxes()) {
            EntityRenderDispatcher.renderHitbox(matrices, vertexConsumer, entityHitbox);
        }
        Vec3d vec3d = new Vec3d(hitbox.viewX(), hitbox.viewY(), hitbox.viewZ());
        VertexRendering.drawVector(matrices, vertexConsumer, new Vector3f(0.0f, standingEyeHeight, 0.0f), vec3d.multiply(2.0), -16776961);
    }

    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertexConsumer, EntityHitbox hitbox) {
        matrices.push();
        matrices.translate(hitbox.offsetX(), hitbox.offsetY(), hitbox.offsetZ());
        VertexRendering.drawBox(matrices, vertexConsumer, hitbox.x0(), hitbox.y0(), hitbox.z0(), hitbox.x1(), hitbox.y1(), hitbox.z1(), hitbox.red(), hitbox.green(), hitbox.blue(), 1.0f);
        matrices.pop();
    }

    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation) {
        Sprite sprite = ModelBaker.FIRE_0.getSprite();
        Sprite sprite2 = ModelBaker.FIRE_1.getSprite();
        matrices.push();
        float f = renderState.width * 1.4f;
        matrices.scale(f, f, f);
        float g = 0.5f;
        float h = 0.0f;
        float i = renderState.height / f;
        float j = 0.0f;
        matrices.multiply((Quaternionfc)rotation);
        matrices.translate(0.0f, 0.0f, 0.3f - (float)((int)i) * 0.02f);
        float k = 0.0f;
        int l = 0;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
        MatrixStack.Entry entry = matrices.peek();
        while (i > 0.0f) {
            Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
            float m = sprite3.getMinU();
            float n = sprite3.getMinV();
            float o = sprite3.getMaxU();
            float p = sprite3.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, -g - 0.0f, 0.0f - j, k, o, p);
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, g - 0.0f, 0.0f - j, k, m, p);
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, g - 0.0f, 1.4f - j, k, m, n);
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, -g - 0.0f, 1.4f - j, k, o, n);
            i -= 0.45f;
            j -= 0.45f;
            g *= 0.9f;
            k -= 0.03f;
            ++l;
        }
        matrices.pop();
    }

    private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(entry, x, y, z).color(Colors.WHITE).texture(u, v).overlay(0, 10).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(entry, 0.0f, 1.0f, 0.0f);
    }

    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius) {
        float f = Math.min(opacity / 0.5f, radius);
        int i = MathHelper.floor(renderState.x - (double)radius);
        int j = MathHelper.floor(renderState.x + (double)radius);
        int k = MathHelper.floor(renderState.y - (double)f);
        int l = MathHelper.floor(renderState.y);
        int m = MathHelper.floor(renderState.z - (double)radius);
        int n = MathHelper.floor(renderState.z + (double)radius);
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_LAYER);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int o = m; o <= n; ++o) {
            for (int p = i; p <= j; ++p) {
                mutable.set(p, 0, o);
                Chunk chunk = world.getChunk(mutable);
                for (int q = k; q <= l; ++q) {
                    mutable.net_minecraft_util_math_BlockPos$Mutable_setY(q);
                    float g = opacity - (float)(renderState.y - (double)mutable.getY()) * 0.5f;
                    EntityRenderDispatcher.renderShadowPart(entry, vertexConsumer, chunk, world, mutable, renderState.x, renderState.y, renderState.z, radius, g);
                }
            }
        }
    }

    private static void renderShadowPart(MatrixStack.Entry entry, VertexConsumer vertices, Chunk chunk, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity) {
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_down();
        BlockState blockState = chunk.getBlockState(blockPos);
        if (blockState.getRenderType() == BlockRenderType.INVISIBLE || world.getLightLevel(pos) <= 3) {
            return;
        }
        if (!blockState.isFullCube(chunk, blockPos)) {
            return;
        }
        VoxelShape voxelShape = blockState.getOutlineShape(chunk, blockPos);
        if (voxelShape.isEmpty()) {
            return;
        }
        float f = LightmapTextureManager.getBrightness(world.getDimension(), world.getLightLevel(pos));
        float g = opacity * 0.5f * f;
        if (g >= 0.0f) {
            if (g > 1.0f) {
                g = 1.0f;
            }
            int i = ColorHelper.getArgb(MathHelper.floor(g * 255.0f), 255, 255, 255);
            Box box = voxelShape.getBoundingBox();
            double d = (double)pos.getX() + box.minX;
            double e = (double)pos.getX() + box.maxX;
            double h = (double)pos.getY() + box.minY;
            double j = (double)pos.getZ() + box.minZ;
            double k = (double)pos.getZ() + box.maxZ;
            float l = (float)(d - x);
            float m = (float)(e - x);
            float n = (float)(h - y);
            float o = (float)(j - z);
            float p = (float)(k - z);
            float q = -l / 2.0f / radius + 0.5f;
            float r = -m / 2.0f / radius + 0.5f;
            float s = -o / 2.0f / radius + 0.5f;
            float t = -p / 2.0f / radius + 0.5f;
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, l, n, o, q, s);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, l, n, p, q, t);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, m, n, p, r, t);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, m, n, o, r, s);
        }
    }

    private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, int color, float x, float y, float z, float u, float v) {
        Vector3f vector3f = entry.getPositionMatrix().transformPosition(x, y, z, new Vector3f());
        vertices.vertex(vector3f.x(), vector3f.y(), vector3f.z(), color, u, v, OverlayTexture.DEFAULT_UV, 0xF000F0, 0.0f, 1.0f, 0.0f);
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    public double getSquaredDistanceToCamera(Entity entity) {
        return this.camera.getPos().squaredDistanceTo(entity.getPos());
    }

    public double getSquaredDistanceToCamera(double x, double y, double z) {
        return this.camera.getPos().squaredDistanceTo(x, y, z);
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public HeldItemRenderer getHeldItemRenderer() {
        return this.heldItemRenderer;
    }

    @Override
    public void reload(ResourceManager manager) {
        EntityRendererFactory.Context context = new EntityRendererFactory.Context(this, this.itemModelManager, this.mapRenderer, this.blockRenderManager, manager, this.entityModelsGetter.get(), this.equipmentModelLoader, this.textRenderer);
        this.renderers = EntityRenderers.reloadEntityRenderers(context);
        this.modelRenderers = EntityRenderers.reloadPlayerRenderers(context);
    }
}

