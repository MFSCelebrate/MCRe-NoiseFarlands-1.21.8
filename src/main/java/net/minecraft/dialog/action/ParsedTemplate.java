/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.dialog.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import net.minecraft.command.MacroInvocation;

public class ParsedTemplate {
    final static public Codec<ParsedTemplate> CODEC = Codec.STRING.comapFlatMap(ParsedTemplate::parse, parsedTemplate -> parsedTemplate.raw);
    final static public Codec<String> NAME_CODEC = Codec.STRING.validate(name -> MacroInvocation.isValidMacroName(name) ? DataResult.success((Object)name) : DataResult.error(() -> name + " is not a valid input name"));
    final private String raw;
    final private MacroInvocation parsed;

    private ParsedTemplate(String raw, MacroInvocation parsed) {
        this.raw = raw;
        this.parsed = parsed;
    }

    private static DataResult<ParsedTemplate> parse(String raw) {
        MacroInvocation macroInvocation;
        try {
            macroInvocation = MacroInvocation.parse(raw);
        }
        catch (Exception exception) {
            return DataResult.error(() -> "Failed to parse template " + raw + ": " + exception.getMessage());
        }
        return DataResult.success((Object)new ParsedTemplate(raw, macroInvocation));
    }

    public String apply(Map<String, String> args) {
        List<String> list = this.parsed.variables().stream().map(variable -> args.getOrDefault(variable, "")).toList();
        return this.parsed.apply(list);
    }
}

