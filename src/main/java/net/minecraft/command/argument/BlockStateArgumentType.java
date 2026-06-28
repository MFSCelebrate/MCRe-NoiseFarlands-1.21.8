/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;

public class BlockStateArgumentType
implements ArgumentType<BlockStateArgument> {
    final static private Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
    final private RegistryWrapper<Block> registryWrapper;

    public BlockStateArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.BLOCK);
    }

    public static BlockStateArgumentType blockState(CommandRegistryAccess commandRegistryAccess) {
        return new BlockStateArgumentType(commandRegistryAccess);
    }

    public BlockStateArgument net_minecraft_command_argument_BlockStateArgument_parse(StringReader stringReader) throws CommandSyntaxException {
        BlockArgumentParser.BlockResult blockResult = BlockArgumentParser.block(this.registryWrapper, stringReader, true);
        return new BlockStateArgument(blockResult.blockState(), blockResult.properties().keySet(), blockResult.nbt());
    }

    public static BlockStateArgument getBlockState(CommandContext<ServerCommandSource> context, String name) {
        return (BlockStateArgument)context.getArgument(name, BlockStateArgument.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return BlockArgumentParser.getSuggestions(this.registryWrapper, builder, false, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.net_minecraft_command_argument_BlockStateArgument_parse(reader);
    }
}

