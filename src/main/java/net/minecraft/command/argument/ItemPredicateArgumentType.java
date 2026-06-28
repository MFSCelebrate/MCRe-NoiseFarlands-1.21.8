/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemPredicateParsing;
import net.minecraft.command.argument.ParserBackedArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ItemPredicateArgumentType
extends ParserBackedArgumentType<ItemStackPredicateArgument> {
    final static private Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
    final static DynamicCommandExceptionType INVALID_ITEM_ID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.stringifiedTranslatable("argument.item.id.invalid", id));
    final static DynamicCommandExceptionType UNKNOWN_ITEM_TAG_EXCEPTION = new DynamicCommandExceptionType(tag -> Text.stringifiedTranslatable("arguments.item.tag.unknown", tag));
    final static DynamicCommandExceptionType UNKNOWN_ITEM_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(component -> Text.stringifiedTranslatable("arguments.item.component.unknown", component));
    final static Dynamic2CommandExceptionType MALFORMED_ITEM_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType((componentId, component) -> Text.stringifiedTranslatable("arguments.item.component.malformed", componentId, component));
    final static DynamicCommandExceptionType UNKNOWN_ITEM_PREDICATE_EXCEPTION = new DynamicCommandExceptionType(predicate -> Text.stringifiedTranslatable("arguments.item.predicate.unknown", predicate));
    final static Dynamic2CommandExceptionType MALFORMED_ITEM_PREDICATE_EXCEPTION = new Dynamic2CommandExceptionType((predicateId, predicate) -> Text.stringifiedTranslatable("arguments.item.predicate.malformed", predicateId, predicate));
    final static private Identifier COUNT_ID = Identifier.ofVanilla("count");
    final static Map<Identifier, ComponentCheck> SPECIAL_COMPONENT_CHECKS = Stream.of(new ComponentCheck(COUNT_ID, stack -> true, (Decoder<? extends Predicate<ItemStack>>)NumberRange.IntRange.CODEC.map(range -> stack -> range.test(stack.getCount())))).collect(Collectors.toUnmodifiableMap(ComponentCheck::id, check -> check));
    final static Map<Identifier, SubPredicateCheck> SPECIAL_SUB_PREDICATE_CHECKS = Stream.of(new SubPredicateCheck(COUNT_ID, (Decoder<? extends Predicate<ItemStack>>)NumberRange.IntRange.CODEC.map(range -> stack -> range.test(stack.getCount())))).collect(Collectors.toUnmodifiableMap(SubPredicateCheck::id, check -> check));

    public ItemPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
        super(ItemPredicateParsing.createParser(new Context(commandRegistryAccess)).map(list -> Util.allOf(list)::test));
    }

    public static ItemPredicateArgumentType itemPredicate(CommandRegistryAccess commandRegistryAccess) {
        return new ItemPredicateArgumentType(commandRegistryAccess);
    }

    public static ItemStackPredicateArgument getItemStackPredicate(CommandContext<ServerCommandSource> context, String name) {
        return (ItemStackPredicateArgument)context.getArgument(name, ItemStackPredicateArgument.class);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    static class Context
    implements ItemPredicateParsing.Callbacks<Predicate<ItemStack>, ComponentCheck, SubPredicateCheck> {
        final private RegistryWrapper.WrapperLookup registries;
        final private RegistryWrapper.Impl<Item> itemRegistryWrapper;
        final private RegistryWrapper.Impl<ComponentType<?>> dataComponentTypeRegistryWrapper;
        final private RegistryWrapper.Impl<ComponentPredicate.Type<?>> itemSubPredicateTypeRegistryWrapper;

        Context(RegistryWrapper.WrapperLookup registries) {
            this.registries = registries;
            this.itemRegistryWrapper = registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.ITEM);
            this.dataComponentTypeRegistryWrapper = registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.DATA_COMPONENT_TYPE);
            this.itemSubPredicateTypeRegistryWrapper = registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE);
        }

        @Override
        public Predicate<ItemStack> itemMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
            RegistryEntry.Reference<Item> reference = this.itemRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> INVALID_ITEM_ID_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
            return stack -> stack.itemMatches(reference);
        }

        @Override
        public Predicate<ItemStack> tagMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
            RegistryEntryList registryEntryList = this.itemRegistryWrapper.getOptional(TagKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> UNKNOWN_ITEM_TAG_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
            return stack -> stack.isIn(registryEntryList);
        }

        @Override
        public ComponentCheck net_minecraft_command_argument_ItemPredicateArgumentType$ComponentCheck_componentCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
            ComponentCheck componentCheck = SPECIAL_COMPONENT_CHECKS.get(identifier);
            if (componentCheck != null) {
                return componentCheck;
            }
            ComponentType componentType = this.dataComponentTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_TYPE, identifier)).map(RegistryEntry::value).orElseThrow(() -> UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
            return ComponentCheck.read(immutableStringReader, identifier, componentType);
        }

        @Override
        public Predicate<ItemStack> componentMatchPredicate(ImmutableStringReader immutableStringReader, ComponentCheck componentCheck, Dynamic<?> dynamic) throws CommandSyntaxException {
            return componentCheck.createPredicate(immutableStringReader, RegistryOps.withRegistry(dynamic, this.registries));
        }

        @Override
        public Predicate<ItemStack> componentPresencePredicate(ImmutableStringReader immutableStringReader, ComponentCheck componentCheck) {
            return componentCheck.presenceChecker;
        }

        @Override
        public SubPredicateCheck net_minecraft_command_argument_ItemPredicateArgumentType$SubPredicateCheck_subPredicateCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
            SubPredicateCheck subPredicateCheck = SPECIAL_SUB_PREDICATE_CHECKS.get(identifier);
            if (subPredicateCheck != null) {
                return subPredicateCheck;
            }
            return this.itemSubPredicateTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE, identifier)).map(SubPredicateCheck::new).orElseThrow(() -> UNKNOWN_ITEM_PREDICATE_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
        }

        @Override
        public Predicate<ItemStack> subPredicatePredicate(ImmutableStringReader immutableStringReader, SubPredicateCheck subPredicateCheck, Dynamic<?> dynamic) throws CommandSyntaxException {
            return subPredicateCheck.createPredicate(immutableStringReader, RegistryOps.withRegistry(dynamic, this.registries));
        }

        @Override
        public Stream<Identifier> streamItemIds() {
            return this.itemRegistryWrapper.streamKeys().map(RegistryKey::getValue);
        }

        @Override
        public Stream<Identifier> streamTags() {
            return this.itemRegistryWrapper.streamTagKeys().map(TagKey::id);
        }

        @Override
        public Stream<Identifier> streamComponentIds() {
            return Stream.concat(SPECIAL_COMPONENT_CHECKS.keySet().stream(), this.dataComponentTypeRegistryWrapper.streamEntries().filter(entry -> !((ComponentType)entry.value()).shouldSkipSerialization()).map(entry -> entry.registryKey().getValue()));
        }

        @Override
        public Stream<Identifier> streamSubPredicateIds() {
            return Stream.concat(SPECIAL_SUB_PREDICATE_CHECKS.keySet().stream(), this.itemSubPredicateTypeRegistryWrapper.streamKeys().map(RegistryKey::getValue));
        }

        @Override
        public Predicate<ItemStack> negate(Predicate<ItemStack> predicate) {
            return predicate.negate();
        }

        @Override
        public Predicate<ItemStack> anyOf(List<Predicate<ItemStack>> list) {
            return Util.anyOf(list);
        }

        @Override
        public Object anyOf(List predicates) {
            return this.anyOf(predicates);
        }

        @Override
        public Object subPredicatePredicate(ImmutableStringReader reader, Object check, Dynamic dynamic) throws CommandSyntaxException {
            return this.subPredicatePredicate(reader, (SubPredicateCheck)check, dynamic);
        }

        @Override
        public Object java_lang_Object_subPredicateCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
            return this.net_minecraft_command_argument_ItemPredicateArgumentType$SubPredicateCheck_subPredicateCheck(reader, id);
        }

        @Override
        public Object java_lang_Object_componentCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
            return this.net_minecraft_command_argument_ItemPredicateArgumentType$ComponentCheck_componentCheck(reader, id);
        }

        @Override
        public Object tagMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
            return this.tagMatchPredicate(reader, id);
        }

        @Override
        public Object itemMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
            return this.itemMatchPredicate(reader, id);
        }
    }

    public static interface ItemStackPredicateArgument
    extends Predicate<ItemStack> {
    }

    static final class ComponentCheck
    extends Record {
        final private Identifier id;
        final Predicate<ItemStack> presenceChecker;
        final private Decoder<? extends Predicate<ItemStack>> valueChecker;

        ComponentCheck(Identifier identifier, Predicate<ItemStack> predicate, Decoder<? extends Predicate<ItemStack>> decoder) {
            this.id = identifier;
            this.presenceChecker = predicate;
            this.valueChecker = decoder;
        }

        public static <T> ComponentCheck read(ImmutableStringReader reader, Identifier id, ComponentType<T> type) throws CommandSyntaxException {
            Codec<T> codec = type.getCodec();
            if (codec == null) {
                throw UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, (Object)id);
            }
            return new ComponentCheck(id, stack -> stack.contains(type), (Decoder<? extends Predicate<ItemStack>>)codec.map(expected -> stack -> {
                Object object2 = stack.get(type);
                return Objects.equals(expected, object2);
            }));
        }

        public Predicate<ItemStack> createPredicate(ImmutableStringReader reader, Dynamic<?> value) throws CommandSyntaxException {
            DataResult dataResult = this.valueChecker.parse(value);
            return (Predicate)dataResult.getOrThrow(error -> MALFORMED_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, (Object)this.id.toString(), error));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ComponentCheck.class, "id;presenceChecker;valueChecker", "id", "presenceChecker", "valueChecker"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ComponentCheck.class, "id;presenceChecker;valueChecker", "id", "presenceChecker", "valueChecker"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ComponentCheck.class, "id;presenceChecker;valueChecker", "id", "presenceChecker", "valueChecker"}, this, object);
        }

        public Identifier id() {
            return this.id;
        }

        public Predicate<ItemStack> presenceChecker() {
            return this.presenceChecker;
        }

        public Decoder<? extends Predicate<ItemStack>> valueChecker() {
            return this.valueChecker;
        }
    }

    record SubPredicateCheck(Identifier id, Decoder<? extends Predicate<ItemStack>> type) {
        public SubPredicateCheck(RegistryEntry.Reference<ComponentPredicate.Type<?>> type) {
            this(type.registryKey().getValue(), (Decoder<? extends Predicate<ItemStack>>)type.value().getPredicateCodec().map(predicate -> predicate::test));
        }

        public Predicate<ItemStack> createPredicate(ImmutableStringReader reader, Dynamic<?> value) throws CommandSyntaxException {
            DataResult dataResult = this.type.parse(value);
            return (Predicate)dataResult.getOrThrow(error -> MALFORMED_ITEM_PREDICATE_EXCEPTION.createWithContext(reader, (Object)this.id.toString(), error));
        }
    }
}

