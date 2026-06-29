/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.AndLootFunction;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateLootFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.loot.function.FilteredLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.ModifyContentsLootFunction;
import net.minecraft.loot.function.ReferenceLootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.function.SetBannerPatternLootFunction;
import net.minecraft.loot.function.SetBookCoverLootFunction;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetCustomDataLootFunction;
import net.minecraft.loot.function.SetCustomModelDataLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.function.SetFireworkExplosionLootFunction;
import net.minecraft.loot.function.SetFireworksLootFunction;
import net.minecraft.loot.function.SetInstrumentLootFunction;
import net.minecraft.loot.function.SetItemLootFunction;
import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetOminousBottleAmplifierLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.function.SetWritableBookPagesLootFunction;
import net.minecraft.loot.function.SetWrittenBookPagesLootFunction;
import net.minecraft.loot.function.ToggleTooltipsLootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class LootFunctionTypes {
    final static public BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
    final static public Codec<LootFunction> BASE_CODEC = Registries.LOOT_FUNCTION_TYPE.getCodec().dispatch("function", LootFunction::getType, LootFunctionType::codec);
    final static public Codec<LootFunction> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(BASE_CODEC, AndLootFunction.INLINE_CODEC));
    final static public Codec<RegistryEntry<LootFunction>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.ITEM_MODIFIER, CODEC);
    final static public LootFunctionType<SetCountLootFunction> SET_COUNT = LootFunctionTypes.register("set_count", SetCountLootFunction.CODEC);
    final static public LootFunctionType<SetItemLootFunction> SET_ITEM = LootFunctionTypes.register("set_item", SetItemLootFunction.CODEC);
    final static public LootFunctionType<EnchantWithLevelsLootFunction> ENCHANT_WITH_LEVELS = LootFunctionTypes.register("enchant_with_levels", EnchantWithLevelsLootFunction.CODEC);
    final static public LootFunctionType<EnchantRandomlyLootFunction> ENCHANT_RANDOMLY = LootFunctionTypes.register("enchant_randomly", EnchantRandomlyLootFunction.CODEC);
    final static public LootFunctionType<SetEnchantmentsLootFunction> SET_ENCHANTMENTS = LootFunctionTypes.register("set_enchantments", SetEnchantmentsLootFunction.CODEC);
    final static public LootFunctionType<SetCustomDataLootFunction> SET_CUSTOM_DATA = LootFunctionTypes.register("set_custom_data", SetCustomDataLootFunction.CODEC);
    final static public LootFunctionType<SetComponentsLootFunction> SET_COMPONENTS = LootFunctionTypes.register("set_components", SetComponentsLootFunction.CODEC);
    final static public LootFunctionType<FurnaceSmeltLootFunction> FURNACE_SMELT = LootFunctionTypes.register("furnace_smelt", FurnaceSmeltLootFunction.CODEC);
    final static public LootFunctionType<EnchantedCountIncreaseLootFunction> ENCHANTED_COUNT_INCREASE = LootFunctionTypes.register("enchanted_count_increase", EnchantedCountIncreaseLootFunction.CODEC);
    final static public LootFunctionType<SetDamageLootFunction> SET_DAMAGE = LootFunctionTypes.register("set_damage", SetDamageLootFunction.CODEC);
    final static public LootFunctionType<SetAttributesLootFunction> SET_ATTRIBUTES = LootFunctionTypes.register("set_attributes", SetAttributesLootFunction.CODEC);
    final static public LootFunctionType<SetNameLootFunction> SET_NAME = LootFunctionTypes.register("set_name", SetNameLootFunction.CODEC);
    final static public LootFunctionType<ExplorationMapLootFunction> EXPLORATION_MAP = LootFunctionTypes.register("exploration_map", ExplorationMapLootFunction.CODEC);
    final static public LootFunctionType<SetStewEffectLootFunction> SET_STEW_EFFECT = LootFunctionTypes.register("set_stew_effect", SetStewEffectLootFunction.CODEC);
    final static public LootFunctionType<CopyNameLootFunction> COPY_NAME = LootFunctionTypes.register("copy_name", CopyNameLootFunction.CODEC);
    final static public LootFunctionType<SetContentsLootFunction> SET_CONTENTS = LootFunctionTypes.register("set_contents", SetContentsLootFunction.CODEC);
    final static public LootFunctionType<ModifyContentsLootFunction> MODIFY_CONTENTS = LootFunctionTypes.register("modify_contents", ModifyContentsLootFunction.CODEC);
    final static public LootFunctionType<FilteredLootFunction> FILTERED = LootFunctionTypes.register("filtered", FilteredLootFunction.CODEC);
    final static public LootFunctionType<LimitCountLootFunction> LIMIT_COUNT = LootFunctionTypes.register("limit_count", LimitCountLootFunction.CODEC);
    final static public LootFunctionType<ApplyBonusLootFunction> APPLY_BONUS = LootFunctionTypes.register("apply_bonus", ApplyBonusLootFunction.CODEC);
    final static public LootFunctionType<SetLootTableLootFunction> SET_LOOT_TABLE = LootFunctionTypes.register("set_loot_table", SetLootTableLootFunction.CODEC);
    final static public LootFunctionType<ExplosionDecayLootFunction> EXPLOSION_DECAY = LootFunctionTypes.register("explosion_decay", ExplosionDecayLootFunction.CODEC);
    final static public LootFunctionType<SetLoreLootFunction> SET_LORE = LootFunctionTypes.register("set_lore", SetLoreLootFunction.CODEC);
    final static public LootFunctionType<FillPlayerHeadLootFunction> FILL_PLAYER_HEAD = LootFunctionTypes.register("fill_player_head", FillPlayerHeadLootFunction.CODEC);
    final static public LootFunctionType<CopyNbtLootFunction> COPY_CUSTOM_DATA = LootFunctionTypes.register("copy_custom_data", CopyNbtLootFunction.CODEC);
    final static public LootFunctionType<CopyStateLootFunction> COPY_STATE = LootFunctionTypes.register("copy_state", CopyStateLootFunction.CODEC);
    final static public LootFunctionType<SetBannerPatternLootFunction> SET_BANNER_PATTERN = LootFunctionTypes.register("set_banner_pattern", SetBannerPatternLootFunction.CODEC);
    final static public LootFunctionType<SetPotionLootFunction> SET_POTION = LootFunctionTypes.register("set_potion", SetPotionLootFunction.CODEC);
    final static public LootFunctionType<SetInstrumentLootFunction> SET_INSTRUMENT = LootFunctionTypes.register("set_instrument", SetInstrumentLootFunction.CODEC);
    final static public LootFunctionType<ReferenceLootFunction> REFERENCE = LootFunctionTypes.register("reference", ReferenceLootFunction.CODEC);
    final static public LootFunctionType<AndLootFunction> SEQUENCE = LootFunctionTypes.register("sequence", AndLootFunction.CODEC);
    final static public LootFunctionType<CopyComponentsLootFunction> COPY_COMPONENTS = LootFunctionTypes.register("copy_components", CopyComponentsLootFunction.CODEC);
    final static public LootFunctionType<SetFireworksLootFunction> SET_FIREWORKS = LootFunctionTypes.register("set_fireworks", SetFireworksLootFunction.CODEC);
    final static public LootFunctionType<SetFireworkExplosionLootFunction> SET_FIREWORK_EXPLOSION = LootFunctionTypes.register("set_firework_explosion", SetFireworkExplosionLootFunction.CODEC);
    final static public LootFunctionType<SetBookCoverLootFunction> SET_BOOK_COVER = LootFunctionTypes.register("set_book_cover", SetBookCoverLootFunction.CODEC);
    final static public LootFunctionType<SetWrittenBookPagesLootFunction> SET_WRITTEN_BOOK_PAGES = LootFunctionTypes.register("set_written_book_pages", SetWrittenBookPagesLootFunction.CODEC);
    final static public LootFunctionType<SetWritableBookPagesLootFunction> SET_WRITABLE_BOOK_PAGES = LootFunctionTypes.register("set_writable_book_pages", SetWritableBookPagesLootFunction.CODEC);
    final static public LootFunctionType<ToggleTooltipsLootFunction> TOGGLE_TOOLTIPS = LootFunctionTypes.register("toggle_tooltips", ToggleTooltipsLootFunction.CODEC);
    final static public LootFunctionType<SetOminousBottleAmplifierLootFunction> SET_OMINOUS_BOTTLE_AMPLIFIER = LootFunctionTypes.register("set_ominous_bottle_amplifier", SetOminousBottleAmplifierLootFunction.CODEC);
    final static public LootFunctionType<SetCustomModelDataLootFunction> SET_CUSTOM_MODEL_DATA = LootFunctionTypes.register("set_custom_model_data", SetCustomModelDataLootFunction.CODEC);

    private static <T extends LootFunction> LootFunctionType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, Identifier.ofVanilla(id), new LootFunctionType<T>(codec));
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> join(List<? extends BiFunction<ItemStack, LootContext, ItemStack>> terms) {
        List<? extends BiFunction<ItemStack, LootContext, ItemStack>> list = List.copyOf(terms);
        return switch (list.size()) {
            case 0 -> NOOP;
            case 1 -> list.get(0);
            case 2 -> {
                BiFunction<ItemStack, LootContext, ItemStack> biFunction = list.get(0);
                BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = list.get(1);
                yield (stack, context) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply((ItemStack)stack, (LootContext)context), (LootContext)context);
            }
            default -> (stack, context) -> {
                for (BiFunction biFunction : list) {
                    stack = (ItemStack)biFunction.apply(stack, context);
                }
                return stack;
            };
        };
    }
}

