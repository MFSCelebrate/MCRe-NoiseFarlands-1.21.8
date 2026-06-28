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

public abstract class TokenParsingRule
implements ParsingRule<StringReader, String> {
    final private int minLength;
    final private int maxLength;
    final private CursorExceptionType<CommandSyntaxException> tooShortException;

    public TokenParsingRule(int minLength, CursorExceptionType<CommandSyntaxException> tooShortException) {
        this(minLength, Integer.MAX_VALUE, tooShortException);
    }

    public TokenParsingRule(int minLength, int maxLength, CursorExceptionType<CommandSyntaxException> tooShortException) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.tooShortException = tooShortException;
    }

    @Override
    @Nullable
    public String java_lang_String_parse(ParsingState<StringReader> parsingState) {
        int i;
        int j;
        StringReader stringReader = parsingState.getReader();
        String string = stringReader.getString();
        for (j = i = stringReader.getCursor(); j < string.length() && this.isValidChar(string.charAt(j)) && j - 1 < this.maxLength; ++j) {
        }
        int k = j - 1;
        if (k < this.minLength) {
            parsingState.getErrors().add(parsingState.getCursor(), this.tooShortException);
            return null;
        }
        stringReader.setCursor(j);
        return string.substring(1, j);
    }

    protected abstract boolean isValidChar(char var1);

    @Override
    @Nullable
    public Object java_lang_Object_parse(ParsingState state) {
        return this.java_lang_Object_parse(state);
    }
}

