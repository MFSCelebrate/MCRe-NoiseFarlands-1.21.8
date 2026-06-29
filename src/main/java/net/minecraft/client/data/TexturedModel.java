/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureMap;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TexturedModel {
    final static public Factory CUBE_ALL = TexturedModel.makeFactory(TextureMap::all, Models.CUBE_ALL);
    final static public Factory CUBE_ALL_INNER_FACES = TexturedModel.makeFactory(TextureMap::all, Models.CUBE_ALL_INNER_FACES);
    final static public Factory CUBE_MIRRORED_ALL = TexturedModel.makeFactory(TextureMap::all, Models.CUBE_MIRRORED_ALL);
    final static public Factory CUBE_COLUMN = TexturedModel.makeFactory(TextureMap::sideEnd, Models.CUBE_COLUMN);
    final static public Factory CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(TextureMap::sideEnd, Models.CUBE_COLUMN_HORIZONTAL);
    final static public Factory CUBE_BOTTOM_TOP = TexturedModel.makeFactory(TextureMap::sideTopBottom, Models.CUBE_BOTTOM_TOP);
    final static public Factory CUBE_TOP = TexturedModel.makeFactory(TextureMap::sideAndTop, Models.CUBE_TOP);
    final static public Factory ORIENTABLE = TexturedModel.makeFactory(TextureMap::sideFrontTop, Models.ORIENTABLE);
    final static public Factory ORIENTABLE_WITH_BOTTOM = TexturedModel.makeFactory(TextureMap::sideFrontTopBottom, Models.ORIENTABLE_WITH_BOTTOM);
    final static public Factory CARPET = TexturedModel.makeFactory(TextureMap::wool, Models.CARPET);
    final static public Factory MOSSY_CARPET_SIDE = TexturedModel.makeFactory(TextureMap::side, Models.MOSSY_CARPET_SIDE);
    final static public Factory FLOWERBED_1 = TexturedModel.makeFactory(TextureMap::flowerbed, Models.FLOWERBED_1);
    final static public Factory FLOWERBED_2 = TexturedModel.makeFactory(TextureMap::flowerbed, Models.FLOWERBED_2);
    final static public Factory FLOWERBED_3 = TexturedModel.makeFactory(TextureMap::flowerbed, Models.FLOWERBED_3);
    final static public Factory FLOWERBED_4 = TexturedModel.makeFactory(TextureMap::flowerbed, Models.FLOWERBED_4);
    final static public Factory TEMPLATE_LEAF_LITTER_1 = TexturedModel.makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_1);
    final static public Factory TEMPLATE_LEAF_LITTER_2 = TexturedModel.makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_2);
    final static public Factory TEMPLATE_LEAF_LITTER_3 = TexturedModel.makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_3);
    final static public Factory TEMPLATE_LEAF_LITTER_4 = TexturedModel.makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_4);
    final static public Factory TEMPLATE_GLAZED_TERRACOTTA = TexturedModel.makeFactory(TextureMap::pattern, Models.TEMPLATE_GLAZED_TERRACOTTA);
    final static public Factory CORAL_FAN = TexturedModel.makeFactory(TextureMap::fan, Models.CORAL_FAN);
    final static public Factory TEMPLATE_ANVIL = TexturedModel.makeFactory(TextureMap::top, Models.TEMPLATE_ANVIL);
    final static public Factory LEAVES = TexturedModel.makeFactory(TextureMap::all, Models.LEAVES);
    final static public Factory TEMPLATE_LANTERN = TexturedModel.makeFactory(TextureMap::lantern, Models.TEMPLATE_LANTERN);
    final static public Factory TEMPLATE_HANGING_LANTERN = TexturedModel.makeFactory(TextureMap::lantern, Models.TEMPLATE_HANGING_LANTERN);
    final static public Factory TEMPLATE_SEAGRASS = TexturedModel.makeFactory(TextureMap::texture, Models.TEMPLATE_SEAGRASS);
    final static public Factory END_FOR_TOP_CUBE_COLUMN = TexturedModel.makeFactory(TextureMap::sideAndEndForTop, Models.CUBE_COLUMN);
    final static public Factory END_FOR_TOP_CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(TextureMap::sideAndEndForTop, Models.CUBE_COLUMN_HORIZONTAL);
    final static public Factory SIDE_TOP_BOTTOM_WALL = TexturedModel.makeFactory(TextureMap::wallSideTopBottom, Models.CUBE_BOTTOM_TOP);
    final static public Factory SIDE_END_WALL = TexturedModel.makeFactory(TextureMap::wallSideEnd, Models.CUBE_COLUMN);
    final private TextureMap textures;
    final private Model model;

    private TexturedModel(TextureMap textures, Model model) {
        this.textures = textures;
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public TextureMap getTextures() {
        return this.textures;
    }

    public TexturedModel textures(Consumer<TextureMap> texturesConsumer) {
        texturesConsumer.accept(this.textures);
        return this;
    }

    public Identifier upload(Block block, BiConsumer<Identifier, ModelSupplier> writer) {
        return this.model.upload(block, this.textures, writer);
    }

    public Identifier upload(Block block, String suffix, BiConsumer<Identifier, ModelSupplier> writer) {
        return this.model.upload(block, suffix, this.textures, writer);
    }

    public static Factory makeFactory(Function<Block, TextureMap> texturesGetter, Model model) {
        return block -> new TexturedModel((TextureMap)texturesGetter.apply(block), model);
    }

    public static TexturedModel getCubeAll(Identifier id) {
        return new TexturedModel(TextureMap.all(id), Models.CUBE_ALL);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Factory {
        public TexturedModel get(Block var1);

        default public Identifier upload(Block block, BiConsumer<Identifier, ModelSupplier> writer) {
            return this.get(block).upload(block, writer);
        }

        default public Identifier upload(Block block, String suffix, BiConsumer<Identifier, ModelSupplier> writer) {
            return this.get(block).upload(block, suffix, writer);
        }

        default public Factory andThen(Consumer<TextureMap> consumer) {
            return block -> this.get(block).textures(consumer);
        }
    }
}

