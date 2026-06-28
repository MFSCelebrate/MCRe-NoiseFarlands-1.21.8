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

public class UnquotedStringParsingRule
implements ParsingRule<StringReader, String> {
    final private int minLength;
    final private CursorExceptionType<CommandSyntaxException> tooShortException;

    public UnquotedStringParsingRule(int minLength, CursorExceptionType<CommandSyntaxException> tooShortException) {
        this.minLength = minLength;
        this.tooShortException = tooShortException;
    }

    @Override
    @Nullable
    public String java_lang_String_parse(ParsingState<StringReader> parsingState) {
        parsingState.getReader().skipWhitespace();
        int i = parsingState.getCursor();
        String string = parsingState.getReader().readUnquotedString();
        if (string.length() < this.minLength) {
            parsingState.getErrors().add(i, this.tooShortException);
            return null;
        }
        return string;
    }

    @Override
    @Nullable
    public Object java_lang_Object_parse(ParsingState state) {
        return this.java_lang_Object_parse(state);
    }
}

