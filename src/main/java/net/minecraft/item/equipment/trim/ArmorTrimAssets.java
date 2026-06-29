/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.minecraft.item.equipment.trim;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimAssets(AssetId base, Map<RegistryKey<EquipmentAsset>, AssetId> overrides) {
    final static public String field_56322 = "_";
    final static public MapCodec<ArmorTrimAssets> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)AssetId.CODEC.fieldOf("asset_name").forGetter(ArmorTrimAssets::base), (App)Codec.unboundedMap(RegistryKey.createCodec(EquipmentAssetKeys.REGISTRY_KEY), AssetId.CODEC).optionalFieldOf("override_armor_assets", Map.of()).forGetter(ArmorTrimAssets::overrides)).apply((Applicative)instance, ArmorTrimAssets::new));
    final static public PacketCodec<ByteBuf, ArmorTrimAssets> PACKET_CODEC = PacketCodec.tuple(AssetId.PACKET_CODEC, ArmorTrimAssets::base, PacketCodecs.map(Object2ObjectOpenHashMap::new, RegistryKey.createPacketCodec(EquipmentAssetKeys.REGISTRY_KEY), AssetId.PACKET_CODEC), ArmorTrimAssets::overrides, ArmorTrimAssets::new);
    final static public ArmorTrimAssets QUARTZ = ArmorTrimAssets.of("quartz");
    final static public ArmorTrimAssets IRON = ArmorTrimAssets.of("iron", Map.of(EquipmentAssetKeys.IRON, "iron_darker"));
    final static public ArmorTrimAssets NETHERITE = ArmorTrimAssets.of("netherite", Map.of(EquipmentAssetKeys.NETHERITE, "netherite_darker"));
    final static public ArmorTrimAssets REDSTONE = ArmorTrimAssets.of("redstone");
    final static public ArmorTrimAssets COPPER = ArmorTrimAssets.of("copper");
    final static public ArmorTrimAssets GOLD = ArmorTrimAssets.of("gold", Map.of(EquipmentAssetKeys.GOLD, "gold_darker"));
    final static public ArmorTrimAssets EMERALD = ArmorTrimAssets.of("emerald");
    final static public ArmorTrimAssets DIAMOND = ArmorTrimAssets.of("diamond", Map.of(EquipmentAssetKeys.DIAMOND, "diamond_darker"));
    final static public ArmorTrimAssets LAPIS = ArmorTrimAssets.of("lapis");
    final static public ArmorTrimAssets AMETHYST = ArmorTrimAssets.of("amethyst");
    final static public ArmorTrimAssets RESIN = ArmorTrimAssets.of("resin");

    public static ArmorTrimAssets of(String suffix) {
        return new ArmorTrimAssets(new AssetId(suffix), Map.of());
    }

    public static ArmorTrimAssets of(String suffix, Map<RegistryKey<EquipmentAsset>, String> overrides) {
        return new ArmorTrimAssets(new AssetId(suffix), Map.copyOf(Maps.transformValues(overrides, AssetId::new)));
    }

    public AssetId getAssetId(RegistryKey<EquipmentAsset> equipmentAsset) {
        return this.overrides.getOrDefault(equipmentAsset, this.base);
    }

    public record AssetId(String suffix) {
        final static public Codec<AssetId> CODEC = Codecs.IDENTIFIER_PATH.xmap(AssetId::new, AssetId::suffix);
        final static public PacketCodec<ByteBuf, AssetId> PACKET_CODEC = PacketCodecs.STRING.xmap(AssetId::new, AssetId::suffix);

        public AssetId {
            if (!Identifier.isPathValid(string)) {
                throw new IllegalArgumentException("Invalid string to use as a resource path element: " + string);
            }
        }
    }
}

