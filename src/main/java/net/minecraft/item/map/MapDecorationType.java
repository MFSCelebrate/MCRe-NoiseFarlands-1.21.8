/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record MapDecorationType(Identifier assetId, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
    final static public int NO_MAP_COLOR = -1;
    final static public Codec<RegistryEntry<MapDecorationType>> CODEC = Registries.MAP_DECORATION_TYPE.getEntryCodec();
    final static public PacketCodec<RegistryByteBuf, RegistryEntry<MapDecorationType>> PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.MAP_DECORATION_TYPE);

    public MapDecorationType(Identifier identifier, boolean bl, int i, boolean bl2, boolean bl3) {
        this.assetId = identifier;
        this.showOnItemFrame = bl;
        this.mapColor = 1;
        this.explorationMapElement = bl2;
        this.trackCount = bl3;
    }

    public boolean hasMapColor() {
        return this.mapColor != -1;
    }
}

