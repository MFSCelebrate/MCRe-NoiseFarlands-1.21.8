/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity.equipment;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EquipmentRenderer {
    final static private int field_54178 = 0;
    final private EquipmentModelLoader equipmentModelLoader;
    final private Function<LayerTextureKey, Identifier> layerTextures;
    final private Function<TrimSpriteKey, Sprite> trimSprites;

    public EquipmentRenderer(EquipmentModelLoader equipmentModelLoader, SpriteAtlasTexture armorTrimsAtlas) {
        this.equipmentModelLoader = equipmentModelLoader;
        this.layerTextures = Util.memoize(key -> key.layer.getFullTextureId(key.layerType));
        this.trimSprites = Util.memoize(key -> armorTrimsAtlas.getSprite(key.getTexture()));
    }

    public void render(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        this.render(layerType, assetKey, model, stack, matrices, vertexConsumers, light, null);
    }

    public void render(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture) {
        List<EquipmentModel.Layer> list = this.equipmentModelLoader.get(assetKey).getLayers(layerType);
        if (list.isEmpty()) {
            return;
        }
        int i = DyedColorComponent.getColor(stack, 0);
        boolean bl = stack.hasGlint();
        for (EquipmentModel.Layer layer : list) {
            int j = EquipmentRenderer.getDyeColor(layer, i);
            if (j == 0) continue;
            Identifier identifier = layer.usePlayerTexture() && texture != null ? texture : this.layerTextures.apply(new LayerTextureKey(layerType, layer));
            VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(identifier), bl);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, j);
            bl = false;
        }
        ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
        if (armorTrim != null) {
            Sprite sprite = this.trimSprites.apply(new TrimSpriteKey(armorTrim, layerType, assetKey));
            VertexConsumer vertexConsumer2 = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(armorTrim.pattern().value().decal())));
            model.render(matrices, vertexConsumer2, light, OverlayTexture.DEFAULT_UV);
        }
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != 0 ? dyeColor : 1;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    static final class LayerTextureKey
    extends Record {
        final EquipmentModel.LayerType layerType;
        final EquipmentModel.Layer layer;

        LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
            this.layerType = layerType;
            this.layer = layer;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this, object);
        }

        public EquipmentModel.LayerType layerType() {
            return this.layerType;
        }

        public EquipmentModel.Layer layer() {
            return this.layer;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
        public Identifier getTexture() {
            return this.trim.getTextureId(this.layerType.getTrimsDirectory(), this.equipmentAssetId);
        }
    }
}

