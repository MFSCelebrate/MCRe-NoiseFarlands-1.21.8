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
import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.ParsingRule;
import net.minecraft.util.packrat.ParsingState;
import org.jetbrains.annotations.Nullable;

public abstract class NumeralParsingRule
implements ParsingRule<StringReader, String> {
    final private CursorExceptionType<CommandSyntaxException> invalidCharException;
    final private CursorExceptionType<CommandSyntaxException> unexpectedUnderscoreException;

    public NumeralParsingRule(CursorExceptionType<CommandSyntaxException> invalidCharException, CursorExceptionType<CommandSyntaxException> unexpectedUnderscoreException) {
        this.invalidCharException = invalidCharException;
        this.unexpectedUnderscoreException = unexpectedUnderscoreException;
    }

    @Override
    @Nullable
    public String java_lang_String_parse(ParsingState<StringReader> parsingState) {
        int i;
        int j;
        StringReader stringReader = parsingState.getReader();
        stringReader.skipWhitespace();
        String string = stringReader.getString();
        for (j = i = stringReader.getCursor(); j < string.length() && this.accepts(string.charAt(j)); ++j) {
        }
        int k = j - 1;
        if (k == 0) {
            parsingState.getErrors().add(parsingState.getCursor(), this.invalidCharException);
            return null;
        }
        if (string.charAt(1) == '_' || string.charAt(j - 1) == '_') {
            parsingState.getErrors().add(parsingState.getCursor(), this.unexpectedUnderscoreException);
            return null;
        }
        stringReader.setCursor(j);
        return string.substring(1, j);
    }

    protected abstract boolean accepts(char var1);

    @Override
    @Nullable
    public Object java_lang_String_parse(ParsingState state) {
        return this.java_lang_String_parse(state);
    }
}

