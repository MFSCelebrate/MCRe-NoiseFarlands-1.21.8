/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.loot.provider.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.EnchantmentLevelLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.ScoreLootNumberProvider;
import net.minecraft.loot.provider.number.StorageLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootNumberProviderTypes {
    final static private Codec<LootNumberProvider> BASE_CODEC = Registries.LOOT_NUMBER_PROVIDER_TYPE.getCodec().dispatch(LootNumberProvider::getType, LootNumberProviderType::codec);
    final static public Codec<LootNumberProvider> CODEC = Codec.lazyInitialized(() -> {
        Codec codec = Codec.withAlternative(BASE_CODEC, (Codec)UniformLootNumberProvider.CODEC.codec());
        return Codec.either(ConstantLootNumberProvider.INLINE_CODEC, (Codec)codec).xmap(Either::unwrap, provider -> {
            Either either;
            if (provider instanceof ConstantLootNumberProvider) {
                ConstantLootNumberProvider constantLootNumberProvider = (ConstantLootNumberProvider)provider;
                either = Either.left((Object)constantLootNumberProvider);
            } else {
                either = Either.right((Object)provider);
            }
            return either;
        });
    });
    final static public LootNumberProviderType CONSTANT = LootNumberProviderTypes.register("constant", ConstantLootNumberProvider.CODEC);
    final static public LootNumberProviderType UNIFORM = LootNumberProviderTypes.register("uniform", UniformLootNumberProvider.CODEC);
    final static public LootNumberProviderType BINOMIAL = LootNumberProviderTypes.register("binomial", BinomialLootNumberProvider.CODEC);
    final static public LootNumberProviderType SCORE = LootNumberProviderTypes.register("score", ScoreLootNumberProvider.CODEC);
    final static public LootNumberProviderType STORAGE = LootNumberProviderTypes.register("storage", StorageLootNumberProvider.CODEC);
    final static public LootNumberProviderType ENCHANTMENT_LEVEL = LootNumberProviderTypes.register("enchantment_level", EnchantmentLevelLootNumberProvider.CODEC);

    private static LootNumberProviderType register(String id, MapCodec<? extends LootNumberProvider> codec) {
        return Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, Identifier.ofVanilla(id), new LootNumberProviderType(codec));
    }
}

