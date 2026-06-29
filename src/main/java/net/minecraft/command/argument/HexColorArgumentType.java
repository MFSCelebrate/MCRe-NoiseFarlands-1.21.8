/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class HexColorArgumentType
implements ArgumentType<Integer> {
    final static private Collection<String> EXAMPLES = Arrays.asList("F00", "FF0000");
    final static public DynamicCommandExceptionType INVALID_HEX_COLOR_EXCEPTION = new DynamicCommandExceptionType(hexColor -> Text.stringifiedTranslatable("argument.hexcolor.invalid", hexColor));

    private HexColorArgumentType() {
    }

    public static HexColorArgumentType hexColor() {
        return new HexColorArgumentType();
    }

    public static Integer getArgbColor(CommandContext<ServerCommandSource> context, String hex) {
        return (Integer)context.getArgument(hex, Integer.class);
    }

    public Integer java_lang_Integer_parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        return switch (string.length()) {
            case 3 -> ColorHelper.getArgb(Integer.valueOf(MessageFormat.format("{0}{0}", Character.valueOf(string.charAt(0))), 16), Integer.valueOf(MessageFormat.format("{0}{0}", Character.valueOf(string.charAt(1))), 16), Integer.valueOf(MessageFormat.format("{0}{0}", Character.valueOf(string.charAt(2))), 16));
            case 6 -> ColorHelper.getArgb(Integer.valueOf(string.substring(0, 2), 16), Integer.valueOf(string.substring(2, 4), 16), Integer.valueOf(string.substring(4, 6), 16));
            default -> throw INVALID_HEX_COLOR_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)string);
        };
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EXAMPLES, builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.java_lang_Integer_parse(reader);
    }
}

