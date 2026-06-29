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
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class HangingSignModelRenderer
implements SimpleSpecialModelRenderer {
    final private Model model;
    final private SpriteIdentifier texture;

    public HangingSignModelRenderer(Model model, SpriteIdentifier texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        HangingSignBlockEntityRenderer.renderAsItem(matrices, vertexConsumers, light, overlay, this.model, this.texture);
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        HangingSignBlockEntityRenderer.setAngles(matrixStack, 0.0f);
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(WoodType woodType, Optional<Identifier> texture) implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(WoodType woodType) {
            this(woodType, Optional.empty());
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            Model model = HangingSignBlockEntityRenderer.createModel(entityModels, this.woodType, HangingSignBlockEntityRenderer.AttachmentType.CEILING_MIDDLE);
            SpriteIdentifier spriteIdentifier = this.texture.map(TexturedRenderLayers.HANGING_SIGN_SPRITE_MAPPER::map).orElseGet(() -> TexturedRenderLayers.getHangingSignTextureId(this.woodType));
            return new HangingSignModelRenderer(model, spriteIdentifier);
        }
    }
}

