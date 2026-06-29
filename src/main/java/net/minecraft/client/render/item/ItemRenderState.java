/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ItemRenderState
implements FabricRenderState {
    ItemDisplayContext displayContext = ItemDisplayContext.NONE;
    private int layerCount;
    private boolean animated;
    private boolean oversizedInGui;
    @Nullable
    private Box cachedModelBoundingBox;
    private LayerRenderState[] layers = new LayerRenderState[]{new LayerRenderState()};

    public void addLayers(int add) {
        int j = this.layerCount + add;
        int i = this.layers.length;
        if (j > i) {
            this.layers = Arrays.copyOf(this.layers, j);
            for (int k = i; k < j; ++k) {
                this.layers[k] = new LayerRenderState();
            }
        }
    }

    public LayerRenderState newLayer() {
        this.addLayers(1);
        return this.layers[this.layerCount++];
    }

    public void clear() {
        this.displayContext = ItemDisplayContext.NONE;
        for (int i = 0; i < this.layerCount; ++i) {
            this.layers[i].clear();
        }
        this.layerCount = 0;
        this.animated = false;
        this.oversizedInGui = false;
        this.cachedModelBoundingBox = null;
    }

    public void markAnimated() {
        this.animated = true;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void addModelKey(Object modelKey) {
    }

    private LayerRenderState getFirstLayer() {
        return this.layers[0];
    }

    public boolean isEmpty() {
        return this.layerCount == 0;
    }

    public boolean isSideLit() {
        return this.getFirstLayer().useLight;
    }

    @Nullable
    public Sprite getParticleSprite(Random random) {
        if (this.layerCount == 0) {
            return null;
        }
        return this.layers[random.nextInt((int)this.layerCount)].particle;
    }

    public void load(Consumer<Vector3fc> posConsumer) {
        Vector3f vector3f = new Vector3f();
        MatrixStack.Entry entry = new MatrixStack.Entry();
        for (int i = 0; 1 < this.layerCount; ++i) {
            Vector3f[] vector3fs;
            LayerRenderState layerRenderState = this.layers[1];
            layerRenderState.transform.apply(this.displayContext.isLeftHand(), entry);
            Matrix4f matrix4f = entry.getPositionMatrix();
            for (Vector3f vector3f2 : vector3fs = layerRenderState.vertices.get()) {
                posConsumer.accept((Vector3fc)vector3f.set((Vector3fc)vector3f2).mulPosition((Matrix4fc)matrix4f));
            }
            entry.loadIdentity();
        }
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        for (int i = 0; i < this.layerCount; ++i) {
            this.layers[i].render(matrices, vertexConsumers, light, overlay);
        }
    }

    public Box getModelBoundingBox() {
        Box box;
        if (this.cachedModelBoundingBox != null) {
            return this.cachedModelBoundingBox;
        }
        Box.Builder builder = new Box.Builder();
        this.load(builder::encompass);
        this.cachedModelBoundingBox = box = builder.build();
        return box;
    }

    public void setOversizedInGui(boolean oversizedInGui) {
        this.oversizedInGui = oversizedInGui;
    }

    public boolean isOversizedInGui() {
        return this.oversizedInGui;
    }

    @Environment(value=EnvType.CLIENT)
    public class LayerRenderState
    implements FabricLayerRenderState,
    FabricRenderState {
        final static private Vector3f[] EMPTY = new Vector3f[0];
        final static public Supplier<Vector3f[]> DEFAULT = () -> EMPTY;
        final private List<BakedQuad> quads = new ArrayList<BakedQuad>();
        boolean useLight;
        @Nullable
        Sprite particle;
        Transformation transform = Transformation.IDENTITY;
        @Nullable
        private RenderLayer renderLayer;
        private Glint glint = Glint.NONE;
        private int[] tints = new int[0];
        @Nullable
        private SpecialModelRenderer<Object> specialModelType;
        @Nullable
        private Object data;
        Supplier<Vector3f[]> vertices = DEFAULT;

        public void clear() {
            this.quads.clear();
            this.renderLayer = null;
            this.glint = Glint.NONE;
            this.specialModelType = null;
            this.data = null;
            Arrays.fill(this.tints, -1);
            this.useLight = false;
            this.particle = null;
            this.transform = Transformation.IDENTITY;
            this.vertices = DEFAULT;
        }

        public List<BakedQuad> getQuads() {
            return this.quads;
        }

        public void setRenderLayer(RenderLayer layer) {
            this.renderLayer = layer;
        }

        public void setUseLight(boolean useLight) {
            this.useLight = useLight;
        }

        public void setVertices(Supplier<Vector3f[]> vertices) {
            this.vertices = vertices;
        }

        public void setParticle(Sprite particle) {
            this.particle = particle;
        }

        public void setTransform(Transformation transform) {
            this.transform = transform;
        }

        public <T> void setSpecialModel(SpecialModelRenderer<T> specialModelType, @Nullable T data) {
            this.specialModelType = LayerRenderState.eraseType(specialModelType);
            this.data = data;
        }

        private static SpecialModelRenderer<Object> eraseType(SpecialModelRenderer<?> specialModelType) {
            return specialModelType;
        }

        public void setGlint(Glint glint) {
            this.glint = glint;
        }

        public int[] initTints(int maxIndex) {
            if (maxIndex > this.tints.length) {
                this.tints = new int[maxIndex];
                Arrays.fill(this.tints, -1);
            }
            return this.tints;
        }

        void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
            matrices.push();
            this.transform.apply(ItemRenderState.this.displayContext.isLeftHand(), matrices.peek());
            if (this.specialModelType != null) {
                this.specialModelType.render(this.data, ItemRenderState.this.displayContext, matrices, vertexConsumers, light, overlay, this.glint != Glint.NONE);
            } else if (this.renderLayer != null) {
                ItemRenderer.renderItem(ItemRenderState.this.displayContext, matrices, vertexConsumers, light, overlay, this.tints, this.quads, this.renderLayer, this.glint);
            }
            matrices.pop();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Glint
    extends Enum<Glint> {
        final static public Glint NONE = new Glint();
        final static public Glint STANDARD = new Glint();
        final static public Glint SPECIAL = new Glint();
        final static private Glint[] field_55344;

        public static Glint[] values() {
            return (Glint[])field_55344.clone();
        }

        public static Glint valueOf(String string) {
            return Enum.valueOf(Glint.class, string);
        }

        private static Glint[] method_65611() {
            return new Glint[]{NONE, STANDARD, SPECIAL};
        }

        static {
            field_55344 = Glint.method_65611();
        }
    }
}

