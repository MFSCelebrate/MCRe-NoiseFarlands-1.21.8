/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class IdentifierArgumentType
implements ArgumentType<Identifier> {
    final static private Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");

    public static IdentifierArgumentType identifier() {
        return new IdentifierArgumentType();
    }

    public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String name) {
        return (Identifier)context.getArgument(name, Identifier.class);
    }

    public Identifier net_minecraft_util_Identifier_parse(StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.net_minecraft_util_Identifier_parse(reader);
    }
}

