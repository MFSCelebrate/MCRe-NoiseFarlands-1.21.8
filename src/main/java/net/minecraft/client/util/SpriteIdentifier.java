/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.util;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpriteIdentifier {
    final static public Comparator<SpriteIdentifier> COMPARATOR = Comparator.comparing(SpriteIdentifier::getAtlasId).thenComparing(SpriteIdentifier::getTextureId);
    final private Identifier atlas;
    final private Identifier texture;
    @Nullable
    private RenderLayer layer;

    public SpriteIdentifier(Identifier atlas, Identifier texture) {
        this.atlas = atlas;
        this.texture = texture;
    }

    public Identifier getAtlasId() {
        return this.atlas;
    }

    public Identifier getTextureId() {
        return this.texture;
    }

    public Sprite getSprite() {
        return MinecraftClient.getInstance().getSpriteAtlas(this.getAtlasId()).apply(this.getTextureId());
    }

    public RenderLayer getRenderLayer(Function<Identifier, RenderLayer> layerFactory) {
        if (this.layer == null) {
            this.layer = layerFactory.apply(this.atlas);
        }
        return this.layer;
    }

    public VertexConsumer getVertexConsumer(VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory) {
        return this.getSprite().getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(this.getRenderLayer(layerFactory)));
    }

    public VertexConsumer getVertexConsumer(VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory, boolean solid, boolean glint) {
        return this.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getItemGlintConsumer(vertexConsumers, this.getRenderLayer(layerFactory), solid, glint));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpriteIdentifier spriteIdentifier = (SpriteIdentifier)o;
        return this.atlas.equals(spriteIdentifier.atlas) && this.texture.equals(spriteIdentifier.texture);
    }

    public int hashCode() {
        return Objects.hash(this.atlas, this.texture);
    }

    public String toString() {
        return "Material{atlasLocation=" + String.valueOf(this.atlas) + ", texture=" + String.valueOf(this.texture) + "}";
    }
}

