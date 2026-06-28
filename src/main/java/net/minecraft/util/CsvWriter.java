/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

public class CsvWriter {
    final static private String CRLF = "\r\n";
    final static private String COMMA = ",";
    final private Writer writer;
    final private int column;

    CsvWriter(Writer writer, List<String> columns) throws IOException {
        this.writer = writer;
        this.column = columns.size();
        this.printRow(columns.stream());
    }

    public static Header makeHeader() {
        return new Header();
    }

    public void printRow(Object ... columns) throws IOException {
        if (columns.length != this.column) {
            throw new IllegalArgumentException("Invalid number of columns, expected " + this.column + ", but got " + columns.length);
        }
        this.printRow(Stream.of(columns));
    }

    private void printRow(Stream<?> columns) throws IOException {
        this.writer.write(columns.map(CsvWriter::escape).collect(Collectors.joining(COMMA)) + CRLF);
    }

    private static String escape(@Nullable Object o) {
        return StringEscapeUtils.escapeCsv((String)(o != null ? o.toString() : "[null]"));
    }

    public static class Header {
        final private List<String> columns = Lists.newArrayList();

        public Header addColumn(String name) {
            this.columns.add(name);
            return this;
        }

        public CsvWriter startBody(Writer writer) throws IOException {
            return new CsvWriter(writer, this.columns);
        }
    }
}

