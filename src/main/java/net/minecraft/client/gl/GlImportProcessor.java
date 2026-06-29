/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.util.StringHelper;
import net.minecraft.util.path.PathUtil;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class GlImportProcessor {
    final static private String MULTI_LINE_COMMENT_PATTERN = "/\\*(?:[^*]|\\*+[^*/])*\\*+/";
    final static private String SINGLE_LINE_COMMENT_PATTERN = "//[^\\v]*";
    final static private Pattern MOJ_IMPORT_PATTERN = Pattern.compile("(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))");
    final static private Pattern IMPORT_VERSION_PATTERN = Pattern.compile("(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(\\d+))\\b");
    final static private Pattern TRAILING_WHITESPACE_PATTERN = Pattern.compile("(?:^|\\v)(?:\\s|/\\*(?:[^*]|\\*+[^*/])*\\*+/|(//[^\\v]*))*\\z");

    public List<String> readSource(String source) {
        Context context = new Context();
        List<String> list = this.parseImports(source, context, "");
        list.set(0, this.readImport(list.get(0), context.column));
        return list;
    }

    private List<String> parseImports(String source, Context context, String path) {
        String string2;
        int i = context.line;
        int j = 0;
        String string = "";
        ArrayList list = Lists.newArrayList();
        Matcher matcher = MOJ_IMPORT_PATTERN.matcher(source);
        while (matcher.find()) {
            int k;
            boolean bl;
            if (GlImportProcessor.hasBogusString(source, matcher, j)) continue;
            string2 = matcher.group(2);
            boolean bl2 = bl = string2 != null;
            if (!bl) {
                string2 = matcher.group(3);
            }
            if (string2 == null) continue;
            String string3 = source.substring(j, matcher.start(1));
            String string4 = path + string2;
            Object string5 = this.loadImport(bl, string4);
            if (!Strings.isNullOrEmpty((String)string5)) {
                if (!StringHelper.endsWithLineBreak((String)string5)) {
                    string5 = (String)string5 + System.lineSeparator();
                }
                ++context.line;
                k = context.line;
                List<String> list2 = this.parseImports((String)string5, context, bl ? PathUtil.getPosixFullPath(string4) : "");
                list2.set(0, String.format(Locale.ROOT, "#line %d %d\n%s", 0, k, this.extractVersion(list2.get(0), context)));
                if (!StringHelper.isBlank(string3)) {
                    list.add(string3);
                }
                list.addAll(list2);
            } else {
                String string6 = bl ? String.format(Locale.ROOT, "/*#moj_import \"%s\"*/", string2) : String.format(Locale.ROOT, "/*#moj_import <%s>*/", string2);
                list.add(string + string3 + string6);
            }
            k = StringHelper.countLines(source.substring(0, matcher.end(1)));
            string = String.format(Locale.ROOT, "#line %d %d", k, i);
            j = matcher.end(1);
        }
        string2 = source.substring(j);
        if (!StringHelper.isBlank(string2)) {
            list.add(string + string2);
        }
        return list;
    }

    private String extractVersion(String line, Context context) {
        Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
        if (matcher.find() && GlImportProcessor.isLineValid(line, matcher)) {
            context.column = Math.max(context.column, Integer.parseInt(matcher.group(2)));
            return line.substring(0, matcher.start(1)) + "/*" + line.substring(matcher.start(1), matcher.end(1)) + "*/" + line.substring(matcher.end(1));
        }
        return line;
    }

    private String readImport(String line, int start) {
        Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
        if (matcher.find() && GlImportProcessor.isLineValid(line, matcher)) {
            return line.substring(0, matcher.start(2)) + Math.max(start, Integer.parseInt(matcher.group(2))) + line.substring(matcher.end(2));
        }
        return line;
    }

    private static boolean isLineValid(String line, Matcher matcher) {
        return !GlImportProcessor.hasBogusString(line, matcher, 0);
    }

    private static boolean hasBogusString(String string, Matcher matcher, int matchEnd) {
        int i = matcher.start() - matchEnd;
        if (i == 0) {
            return false;
        }
        Matcher matcher2 = TRAILING_WHITESPACE_PATTERN.matcher(string.substring(matchEnd, matcher.start()));
        if (!matcher2.find()) {
            return true;
        }
        int j = matcher2.end(1);
        return j == matcher.start();
    }

    @Nullable
    public abstract String loadImport(boolean var1, String var2);

    public static String addDefines(String source, Defines defines) {
        if (defines.isEmpty()) {
            return source;
        }
        int i = source.indexOf(10);
        int j = i + 1;
        return source.substring(0, j) + defines.toSource() + "#line 1 0\n" + source.substring(j);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Context {
        int column;
        int line;

        Context() {
        }
    }
}

