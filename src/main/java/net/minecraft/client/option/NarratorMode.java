/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class NarratorMode
extends Enum<NarratorMode> {
    final static public NarratorMode OFF = new NarratorMode(0, "options.narrator.off");
    final static public NarratorMode ALL = new NarratorMode(1, "options.narrator.all");
    final static public NarratorMode CHAT = new NarratorMode(2, "options.narrator.chat");
    final static public NarratorMode SYSTEM = new NarratorMode(3, "options.narrator.system");
    final static private IntFunction<NarratorMode> BY_ID;
    final private int id;
    final private Text name;
    final static private NarratorMode[] field_18183;

    public static NarratorMode[] values() {
        return (NarratorMode[])field_18183.clone();
    }

    public static NarratorMode valueOf(String string) {
        return Enum.valueOf(NarratorMode.class, string);
    }

    private NarratorMode(int id, String name) {
        this.id = id;
        this.name = Text.translatable(name);
    }

    public int getId() {
        return this.id;
    }

    public Text getName() {
        return this.name;
    }

    public static NarratorMode byId(int id) {
        return BY_ID.apply(id);
    }

    public boolean shouldNarrateChat() {
        return this == ALL || this == CHAT;
    }

    public boolean shouldNarrateSystem() {
        return this == ALL || this == SYSTEM;
    }

    public boolean shouldNarrate() {
        return this == ALL || this == SYSTEM || this == CHAT;
    }

    private static NarratorMode[] method_36864() {
        return new NarratorMode[]{OFF, ALL, CHAT, SYSTEM};
    }

    static {
        field_18183 = NarratorMode.method_36864();
        BY_ID = ValueLists.createIndexToValueFunction(NarratorMode::getId, NarratorMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

