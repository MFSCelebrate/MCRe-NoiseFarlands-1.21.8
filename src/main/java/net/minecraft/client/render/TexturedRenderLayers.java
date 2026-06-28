/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TexturedRenderLayers {
    final static public Identifier SHULKER_BOXES_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/shulker_boxes.png");
    final static public Identifier BEDS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/beds.png");
    final static public Identifier BANNER_PATTERNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/banner_patterns.png");
    final static public Identifier SHIELD_PATTERNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/shield_patterns.png");
    final static public Identifier SIGNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/signs.png");
    final static public Identifier CHEST_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/chest.png");
    final static public Identifier ARMOR_TRIMS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/armor_trims.png");
    final static public Identifier DECORATED_POT_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/decorated_pot.png");
    final static private RenderLayer SHULKER_BOXES_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SHULKER_BOXES_ATLAS_TEXTURE);
    final static private RenderLayer BEDS_RENDER_LAYER = RenderLayer.getEntitySolid(BEDS_ATLAS_TEXTURE);
    final static private RenderLayer BANNER_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(BANNER_PATTERNS_ATLAS_TEXTURE);
    final static private RenderLayer SHIELD_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(SHIELD_PATTERNS_ATLAS_TEXTURE);
    final static private RenderLayer SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SIGNS_ATLAS_TEXTURE);
    final static private RenderLayer CHEST_RENDER_LAYER = RenderLayer.getEntityCutout(CHEST_ATLAS_TEXTURE);
    final static private RenderLayer ARMOR_TRIMS_RENDER_LAYER = RenderLayer.getArmorCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
    final static private RenderLayer ARMOR_TRIMS_DECAL_RENDER_LAYER = RenderLayer.createArmorDecalCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
    final static private RenderLayer ENTITY_SOLID = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    final static private RenderLayer ENTITY_CUTOUT = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    final static private RenderLayer ITEM_ENTITY_TRANSLUCENT_CULL = RenderLayer.getItemEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    final static public SpriteMapper ITEM_SPRITE_MAPPER = new SpriteMapper(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, "item");
    final static public SpriteMapper BLOCK_SPRITE_MAPPER = new SpriteMapper(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, "block");
    final static public SpriteMapper BANNER_PATTERN_SPRITE_MAPPER = new SpriteMapper(BANNER_PATTERNS_ATLAS_TEXTURE, "entity/banner");
    final static public SpriteMapper SHIELD_PATTERN_SPRITE_MAPPER = new SpriteMapper(SHIELD_PATTERNS_ATLAS_TEXTURE, "entity/shield");
    final static public SpriteMapper CHEST_SPRITE_MAPPER = new SpriteMapper(CHEST_ATLAS_TEXTURE, "entity/chest");
    final static public SpriteMapper DECORATED_POT_SPRITE_MAPPER = new SpriteMapper(DECORATED_POT_ATLAS_TEXTURE, "entity/decorated_pot");
    final static public SpriteMapper BED_SPRITE_MAPPER = new SpriteMapper(BEDS_ATLAS_TEXTURE, "entity/bed");
    final static public SpriteMapper SHULKER_SPRITE_MAPPER = new SpriteMapper(SHULKER_BOXES_ATLAS_TEXTURE, "entity/shulker");
    final static public SpriteMapper SIGN_SPRITE_MAPPER = new SpriteMapper(SIGNS_ATLAS_TEXTURE, "entity/signs");
    final static public SpriteMapper HANGING_SIGN_SPRITE_MAPPER = new SpriteMapper(SIGNS_ATLAS_TEXTURE, "entity/signs/hanging");
    final static public SpriteIdentifier SHULKER_TEXTURE_ID = SHULKER_SPRITE_MAPPER.mapVanilla("shulker");
    final static public List<SpriteIdentifier> COLORED_SHULKER_BOXES_TEXTURES = (List)Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getIndex)).map(TexturedRenderLayers::createShulkerBoxTextureId).collect(ImmutableList.toImmutableList());
    final static public Map<WoodType, SpriteIdentifier> SIGN_TYPE_TEXTURES = WoodType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createSignTextureId));
    final static public Map<WoodType, SpriteIdentifier> HANGING_SIGN_TYPE_TEXTURES = WoodType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createHangingSignTextureId));
    final static public SpriteIdentifier BANNER_BASE = BANNER_PATTERN_SPRITE_MAPPER.mapVanilla("base");
    final static public SpriteIdentifier SHIELD_BASE = SHIELD_PATTERN_SPRITE_MAPPER.mapVanilla("base");
    final static private Map<Identifier, SpriteIdentifier> BANNER_PATTERN_TEXTURES = new HashMap<Identifier, SpriteIdentifier>();
    final static private Map<Identifier, SpriteIdentifier> SHIELD_PATTERN_TEXTURES = new HashMap<Identifier, SpriteIdentifier>();
    final static public Map<RegistryKey<DecoratedPotPattern>, SpriteIdentifier> DECORATED_POT_PATTERN_TEXTURES = Registries.DECORATED_POT_PATTERN.streamEntries().collect(Collectors.toMap(RegistryEntry.Reference::registryKey, pattern -> DECORATED_POT_SPRITE_MAPPER.map(((DecoratedPotPattern)pattern.value()).assetId())));
    final static public SpriteIdentifier DECORATED_POT_BASE = DECORATED_POT_SPRITE_MAPPER.mapVanilla("decorated_pot_base");
    final static public SpriteIdentifier DECORATED_POT_SIDE = DECORATED_POT_SPRITE_MAPPER.mapVanilla("decorated_pot_side");
    final static private SpriteIdentifier[] BED_TEXTURES = (SpriteIdentifier[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getIndex)).map(TexturedRenderLayers::createBedTextureId).toArray(SpriteIdentifier[]::new);
    final static public SpriteIdentifier TRAPPED_CHEST = CHEST_SPRITE_MAPPER.mapVanilla("trapped");
    final static public SpriteIdentifier TRAPPED_CHEST_LEFT = CHEST_SPRITE_MAPPER.mapVanilla("trapped_left");
    final static public SpriteIdentifier TRAPPED_CHEST_RIGHT = CHEST_SPRITE_MAPPER.mapVanilla("trapped_right");
    final static public SpriteIdentifier CHRISTMAS_CHEST = CHEST_SPRITE_MAPPER.mapVanilla("christmas");
    final static public SpriteIdentifier CHRISTMAS_CHEST_LEFT = CHEST_SPRITE_MAPPER.mapVanilla("christmas_left");
    final static public SpriteIdentifier CHRISTMAS_CHEST_RIGHT = CHEST_SPRITE_MAPPER.mapVanilla("christmas_right");
    final static public SpriteIdentifier CHEST = CHEST_SPRITE_MAPPER.mapVanilla("normal");
    final static public SpriteIdentifier CHEST_LEFT = CHEST_SPRITE_MAPPER.mapVanilla("normal_left");
    final static public SpriteIdentifier CHEST_RIGHT = CHEST_SPRITE_MAPPER.mapVanilla("normal_right");
    final static public SpriteIdentifier ENDER_CHEST = CHEST_SPRITE_MAPPER.mapVanilla("ender");

    public static RenderLayer getBannerPatterns() {
        return BANNER_PATTERNS_RENDER_LAYER;
    }

    public static RenderLayer getShieldPatterns() {
        return SHIELD_PATTERNS_RENDER_LAYER;
    }

    public static RenderLayer getBeds() {
        return BEDS_RENDER_LAYER;
    }

    public static RenderLayer getShulkerBoxes() {
        return SHULKER_BOXES_RENDER_LAYER;
    }

    public static RenderLayer getSign() {
        return SIGN_RENDER_LAYER;
    }

    public static RenderLayer getHangingSign() {
        return SIGN_RENDER_LAYER;
    }

    public static RenderLayer getChest() {
        return CHEST_RENDER_LAYER;
    }

    public static RenderLayer getArmorTrims(boolean decal) {
        return decal ? ARMOR_TRIMS_DECAL_RENDER_LAYER : ARMOR_TRIMS_RENDER_LAYER;
    }

    public static RenderLayer getEntitySolid() {
        return ENTITY_SOLID;
    }

    public static RenderLayer getEntityCutout() {
        return ENTITY_CUTOUT;
    }

    public static RenderLayer getItemEntityTranslucentCull() {
        return ITEM_ENTITY_TRANSLUCENT_CULL;
    }

    public static SpriteIdentifier getBedTextureId(DyeColor color) {
        return BED_TEXTURES[color.getIndex()];
    }

    public static Identifier createColorId(DyeColor color) {
        return Identifier.ofVanilla(color.getId());
    }

    public static SpriteIdentifier createBedTextureId(DyeColor color) {
        return BED_SPRITE_MAPPER.map(TexturedRenderLayers.createColorId(color));
    }

    public static SpriteIdentifier getShulkerBoxTextureId(DyeColor color) {
        return COLORED_SHULKER_BOXES_TEXTURES.get(color.getIndex());
    }

    public static Identifier createShulkerId(DyeColor color) {
        return Identifier.ofVanilla("shulker_" + color.getId());
    }

    public static SpriteIdentifier createShulkerBoxTextureId(DyeColor color) {
        return SHULKER_SPRITE_MAPPER.map(TexturedRenderLayers.createShulkerId(color));
    }

    private static SpriteIdentifier createSignTextureId(WoodType type) {
        return SIGN_SPRITE_MAPPER.mapVanilla(type.name());
    }

    private static SpriteIdentifier createHangingSignTextureId(WoodType type) {
        return HANGING_SIGN_SPRITE_MAPPER.mapVanilla(type.name());
    }

    public static SpriteIdentifier getSignTextureId(WoodType signType) {
        return SIGN_TYPE_TEXTURES.get(signType);
    }

    public static SpriteIdentifier getHangingSignTextureId(WoodType signType) {
        return HANGING_SIGN_TYPE_TEXTURES.get(signType);
    }

    public static SpriteIdentifier getBannerPatternTextureId(RegistryEntry<BannerPattern> pattern) {
        return BANNER_PATTERN_TEXTURES.computeIfAbsent(pattern.value().assetId(), BANNER_PATTERN_SPRITE_MAPPER::map);
    }

    public static SpriteIdentifier getShieldPatternTextureId(RegistryEntry<BannerPattern> pattern) {
        return SHIELD_PATTERN_TEXTURES.computeIfAbsent(pattern.value().assetId(), SHIELD_PATTERN_SPRITE_MAPPER::map);
    }

    @Nullable
    public static SpriteIdentifier getDecoratedPotPatternTextureId(@Nullable RegistryKey<DecoratedPotPattern> potPatternKey) {
        if (potPatternKey == null) {
            return null;
        }
        return DECORATED_POT_PATTERN_TEXTURES.get(potPatternKey);
    }

    public static SpriteIdentifier getChestTextureId(BlockEntity blockEntity, ChestType type, boolean christmas) {
        if (blockEntity instanceof EnderChestBlockEntity) {
            return ENDER_CHEST;
        }
        if (christmas) {
            return TexturedRenderLayers.getChestTextureId(type, CHRISTMAS_CHEST, CHRISTMAS_CHEST_LEFT, CHRISTMAS_CHEST_RIGHT);
        }
        if (blockEntity instanceof TrappedChestBlockEntity) {
            return TexturedRenderLayers.getChestTextureId(type, TRAPPED_CHEST, TRAPPED_CHEST_LEFT, TRAPPED_CHEST_RIGHT);
        }
        return TexturedRenderLayers.getChestTextureId(type, CHEST, CHEST_LEFT, CHEST_RIGHT);
    }

    private static SpriteIdentifier getChestTextureId(ChestType type, SpriteIdentifier single, SpriteIdentifier left, SpriteIdentifier right) {
        switch (type) {
            case LEFT: {
                return left;
            }
            case RIGHT: {
                return right;
            }
        }
        return single;
    }
}

