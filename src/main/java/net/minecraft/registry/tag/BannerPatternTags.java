/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BannerPatternTags {
    final static public TagKey<BannerPattern> NO_ITEM_REQUIRED = BannerPatternTags.of("no_item_required");
    final static public TagKey<BannerPattern> FLOWER_PATTERN_ITEM = BannerPatternTags.of("pattern_item/flower");
    final static public TagKey<BannerPattern> CREEPER_PATTERN_ITEM = BannerPatternTags.of("pattern_item/creeper");
    final static public TagKey<BannerPattern> SKULL_PATTERN_ITEM = BannerPatternTags.of("pattern_item/skull");
    final static public TagKey<BannerPattern> MOJANG_PATTERN_ITEM = BannerPatternTags.of("pattern_item/mojang");
    final static public TagKey<BannerPattern> GLOBE_PATTERN_ITEM = BannerPatternTags.of("pattern_item/globe");
    final static public TagKey<BannerPattern> PIGLIN_PATTERN_ITEM = BannerPatternTags.of("pattern_item/piglin");
    final static public TagKey<BannerPattern> FLOW_PATTERN_ITEM = BannerPatternTags.of("pattern_item/flow");
    final static public TagKey<BannerPattern> GUSTER_PATTERN_ITEM = BannerPatternTags.of("pattern_item/guster");
    final static public TagKey<BannerPattern> FIELD_MASONED_PATTERN_ITEM = BannerPatternTags.of("pattern_item/field_masoned");
    final static public TagKey<BannerPattern> BORDURE_INDENTED_PATTERN_ITEM = BannerPatternTags.of("pattern_item/bordure_indented");

    private BannerPatternTags() {
    }

    private static TagKey<BannerPattern> of(String id) {
        return TagKey.of(RegistryKeys.BANNER_PATTERN, Identifier.ofVanilla(id));
    }
}

