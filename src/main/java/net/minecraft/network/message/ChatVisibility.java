/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public final class ChatVisibility
extends Enum<ChatVisibility>
implements TranslatableOption {
    final static public ChatVisibility FULL = new ChatVisibility(0, "options.chat.visibility.full");
    final static public ChatVisibility SYSTEM = new ChatVisibility(1, "options.chat.visibility.system");
    final static public ChatVisibility HIDDEN = new ChatVisibility(2, "options.chat.visibility.hidden");
    final static private IntFunction<ChatVisibility> BY_ID;
    final private int id;
    final private String translationKey;
    final static private ChatVisibility[] field_7537;

    public static ChatVisibility[] values() {
        return (ChatVisibility[])field_7537.clone();
    }

    public static ChatVisibility valueOf(String string) {
        return Enum.valueOf(ChatVisibility.class, string);
    }

    private ChatVisibility(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static ChatVisibility byId(int id) {
        return BY_ID.apply(id);
    }

    private static ChatVisibility[] method_36660() {
        return new ChatVisibility[]{FULL, SYSTEM, HIDDEN};
    }

    static {
        field_7537 = ChatVisibility.method_36660();
        BY_ID = ValueLists.createIndexToValueFunction(ChatVisibility::getId, ChatVisibility.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

