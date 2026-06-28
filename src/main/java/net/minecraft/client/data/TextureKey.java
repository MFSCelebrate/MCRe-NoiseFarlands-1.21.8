/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class TextureKey {
    final static public TextureKey ALL = TextureKey.of("all");
    final static public TextureKey TEXTURE = TextureKey.of("texture", ALL);
    final static public TextureKey PARTICLE = TextureKey.of("particle", TEXTURE);
    final static public TextureKey END = TextureKey.of("end", ALL);
    final static public TextureKey BOTTOM = TextureKey.of("bottom", END);
    final static public TextureKey TOP = TextureKey.of("top", END);
    final static public TextureKey FRONT = TextureKey.of("front", ALL);
    final static public TextureKey BACK = TextureKey.of("back", ALL);
    final static public TextureKey SIDE = TextureKey.of("side", ALL);
    final static public TextureKey NORTH = TextureKey.of("north", SIDE);
    final static public TextureKey SOUTH = TextureKey.of("south", SIDE);
    final static public TextureKey EAST = TextureKey.of("east", SIDE);
    final static public TextureKey WEST = TextureKey.of("west", SIDE);
    final static public TextureKey UP = TextureKey.of("up");
    final static public TextureKey DOWN = TextureKey.of("down");
    final static public TextureKey CROSS = TextureKey.of("cross");
    final static public TextureKey CROSS_EMISSIVE = TextureKey.of("cross_emissive");
    final static public TextureKey PLANT = TextureKey.of("plant");
    final static public TextureKey WALL = TextureKey.of("wall", ALL);
    final static public TextureKey RAIL = TextureKey.of("rail");
    final static public TextureKey WOOL = TextureKey.of("wool");
    final static public TextureKey PATTERN = TextureKey.of("pattern");
    final static public TextureKey PANE = TextureKey.of("pane");
    final static public TextureKey EDGE = TextureKey.of("edge");
    final static public TextureKey FAN = TextureKey.of("fan");
    final static public TextureKey STEM = TextureKey.of("stem");
    final static public TextureKey UPPERSTEM = TextureKey.of("upperstem");
    final static public TextureKey CROP = TextureKey.of("crop");
    final static public TextureKey DIRT = TextureKey.of("dirt");
    final static public TextureKey FIRE = TextureKey.of("fire");
    final static public TextureKey LANTERN = TextureKey.of("lantern");
    final static public TextureKey PLATFORM = TextureKey.of("platform");
    final static public TextureKey UNSTICKY = TextureKey.of("unsticky");
    final static public TextureKey TORCH = TextureKey.of("torch");
    final static public TextureKey LAYER0 = TextureKey.of("layer0");
    final static public TextureKey LAYER1 = TextureKey.of("layer1");
    final static public TextureKey LAYER2 = TextureKey.of("layer2");
    final static public TextureKey LIT_LOG = TextureKey.of("lit_log");
    final static public TextureKey CANDLE = TextureKey.of("candle");
    final static public TextureKey INSIDE = TextureKey.of("inside");
    final static public TextureKey CONTENT = TextureKey.of("content");
    final static public TextureKey INNER_TOP = TextureKey.of("inner_top");
    final static public TextureKey FLOWERBED = TextureKey.of("flowerbed");
    final static public TextureKey TENTACLES = TextureKey.of("tentacles");
    final private String name;
    @Nullable
    final private TextureKey parent;

    public static TextureKey of(String name) {
        return new TextureKey(name, null);
    }

    public static TextureKey of(String name, TextureKey parent) {
        return new TextureKey(name, parent);
    }

    private TextureKey(String name, @Nullable TextureKey parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public TextureKey getParent() {
        return this.parent;
    }

    public String toString() {
        return "#" + this.name;
    }
}

