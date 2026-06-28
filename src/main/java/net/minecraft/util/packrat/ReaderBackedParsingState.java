/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 */
package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import net.minecraft.util.packrat.ParseErrorList;
import net.minecraft.util.packrat.ParsingStateImpl;

public class ReaderBackedParsingState
extends ParsingStateImpl<StringReader> {
    final private StringReader reader;

    public ReaderBackedParsingState(ParseErrorList<StringReader> errors, StringReader reader) {
        super(errors);
        this.reader = reader;
    }

    @Override
    public StringReader com_mojang_brigadier_StringReader_getReader() {
        return this.reader;
    }

    @Override
    public int getCursor() {
        return this.reader.getCursor();
    }

    @Override
    public void setCursor(int cursor) {
        this.reader.setCursor(cursor);
    }

    @Override
    public Object java_lang_Object_getReader() {
        return this.com_mojang_brigadier_StringReader_getReader();
    }
}

