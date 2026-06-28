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
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class ChestModelRenderer
implements SimpleSpecialModelRenderer {
    final static public Identifier CHRISTMAS_ID = Identifier.ofVanilla("christmas");
    final static public Identifier NORMAL_ID = Identifier.ofVanilla("normal");
    final static public Identifier TRAPPED_ID = Identifier.ofVanilla("trapped");
    final static public Identifier ENDER_ID = Identifier.ofVanilla("ender");
    final private ChestBlockModel model;
    final private SpriteIdentifier textureId;
    final private float openness;

    public ChestModelRenderer(ChestBlockModel model, SpriteIdentifier textureId, float openness) {
        this.model = model;
        this.textureId = textureId;
        this.openness = openness;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        VertexConsumer vertexConsumer = this.textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        this.model.setLockAndLidPitch(this.openness);
        this.model.render(matrices, vertexConsumer, light, overlay);
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        this.model.setLockAndLidPitch(this.openness);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier texture, float openness) implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), (App)Codec.FLOAT.optionalFieldOf("openness", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::openness)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(Identifier texture) {
            this(texture, 0.0f);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            ChestBlockModel chestBlockModel = new ChestBlockModel(entityModels.getModelPart(EntityModelLayers.CHEST));
            SpriteIdentifier spriteIdentifier = TexturedRenderLayers.CHEST_SPRITE_MAPPER.map(this.texture);
            return new ChestModelRenderer(chestBlockModel, spriteIdentifier, this.openness);
        }
    }
}

