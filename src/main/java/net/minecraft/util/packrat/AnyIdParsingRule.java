/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.packrat.ParsingRule;
import net.minecraft.util.packrat.ParsingState;
import org.jetbrains.annotations.Nullable;

public class AnyIdParsingRule
implements ParsingRule<StringReader, Identifier> {
    final static public ParsingRule<StringReader, Identifier> INSTANCE = new AnyIdParsingRule();

    private AnyIdParsingRule() {
    }

    @Override
    @Nullable
    public Identifier net_minecraft_util_Identifier_parse(ParsingState<StringReader> parsingState) {
        parsingState.getReader().skipWhitespace();
        try {
            return Identifier.fromCommandInputNonEmpty(parsingState.getReader());
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    @Override
    @Nullable
    public Object java_lang_Object_parse(ParsingState state) {
        return this.java_lang_Object_parse(state);
    }
}

