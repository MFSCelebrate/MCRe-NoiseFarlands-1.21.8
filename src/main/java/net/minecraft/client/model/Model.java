/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class Model {
    final protected ModelPart root;
    final protected Function<Identifier, RenderLayer> layerFactory;
    final private List<ModelPart> parts;

    public Model(ModelPart root, Function<Identifier, RenderLayer> layerFactory) {
        this.root = root;
        this.layerFactory = layerFactory;
        this.parts = root.traverse();
    }

    public final RenderLayer getLayer(Identifier texture) {
        return this.layerFactory.apply(texture);
    }

    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.getRootPart().render(matrices, vertices, light, overlay, color);
    }

    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, -1);
    }

    public final ModelPart getRootPart() {
        return this.root;
    }

    public final List<ModelPart> getParts() {
        return this.parts;
    }

    public final void resetTransforms() {
        for (ModelPart modelPart : this.parts) {
            modelPart.resetTransform();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SinglePartModel
    extends Model {
        public SinglePartModel(ModelPart part, Function<Identifier, RenderLayer> layerFactory) {
            super(part, layerFactory);
        }
    }
}

