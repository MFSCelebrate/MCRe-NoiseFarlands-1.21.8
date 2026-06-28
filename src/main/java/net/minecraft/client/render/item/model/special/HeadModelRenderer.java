/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class HeadModelRenderer
implements SimpleSpecialModelRenderer {
    final private SkullBlockEntityModel model;
    final private float animation;
    final private RenderLayer renderLayer;

    public HeadModelRenderer(SkullBlockEntityModel model, float animation, RenderLayer renderLayer) {
        this.model = model;
        this.animation = animation;
        this.renderLayer = renderLayer;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        SkullBlockEntityRenderer.renderSkull(null, 180.0f, this.animation, matrices, vertexConsumers, light, this.model, this.renderLayer);
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.setHeadRotation(this.animation, 180.0f, 0.0f);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(SkullBlock.SkullType kind, Optional<Identifier> textureOverride, float animation) implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(Unbaked::kind), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride), (App)Codec.FLOAT.optionalFieldOf("animation", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::animation)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(SkullBlock.SkullType kind) {
            this(kind, Optional.empty(), 0.0f);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        @Nullable
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(entityModels, this.kind);
            Identifier identifier = this.textureOverride.map(id -> id.withPath(texture -> "textures/entity/" + texture + ".png")).orElse(null);
            if (skullBlockEntityModel == null) {
                return null;
            }
            RenderLayer renderLayer = SkullBlockEntityRenderer.getCutoutRenderLayer(this.kind, identifier);
            return new HeadModelRenderer(skullBlockEntityModel, this.animation, renderLayer);
        }
    }
}

