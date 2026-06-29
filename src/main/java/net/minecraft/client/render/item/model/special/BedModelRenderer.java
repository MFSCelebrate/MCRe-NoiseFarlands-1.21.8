/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class BedModelRenderer
implements SimpleSpecialModelRenderer {
    final private BedBlockEntityRenderer blockEntityRenderer;
    final private SpriteIdentifier textureId;

    public BedModelRenderer(BedBlockEntityRenderer blockEntityRenderer, SpriteIdentifier textureId) {
        this.blockEntityRenderer = blockEntityRenderer;
        this.textureId = textureId;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        this.blockEntityRenderer.renderAsItem(matrices, vertexConsumers, light, overlay, this.textureId);
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        this.blockEntityRenderer.collectVertices(vertices);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(DyeColor color) {
            this(TexturedRenderLayers.createColorId(color));
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new BedModelRenderer(new BedBlockEntityRenderer(entityModels), TexturedRenderLayers.BED_SPRITE_MAPPER.map(this.texture));
        }
    }
}

