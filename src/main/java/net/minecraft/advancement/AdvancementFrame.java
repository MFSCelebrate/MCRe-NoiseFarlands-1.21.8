/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public final class AdvancementFrame
extends Enum<AdvancementFrame>
implements StringIdentifiable {
    final static public AdvancementFrame TASK = new AdvancementFrame("task", Formatting.GREEN);
    final static public AdvancementFrame CHALLENGE = new AdvancementFrame("challenge", Formatting.DARK_PURPLE);
    final static public AdvancementFrame GOAL = new AdvancementFrame("goal", Formatting.GREEN);
    final static public Codec<AdvancementFrame> CODEC;
    final private String id;
    final private Formatting titleFormat;
    final private Text toastText;
    final static private AdvancementFrame[] field_1253;

    public static AdvancementFrame[] values() {
        return (AdvancementFrame[])field_1253.clone();
    }

    public static AdvancementFrame valueOf(String string) {
        return Enum.valueOf(AdvancementFrame.class, string);
    }

    private AdvancementFrame(String id, Formatting titleFormat) {
        this.id = id;
        this.titleFormat = titleFormat;
        this.toastText = Text.translatable("advancements.toast." + id);
    }

    public Formatting getTitleFormat() {
        return this.titleFormat;
    }

    public Text getToastText() {
        return this.toastText;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public MutableText getChatAnnouncementText(AdvancementEntry advancementEntry, ServerPlayerEntity player) {
        return Text.translatable("chat.type.advancement." + this.id, player.getDisplayName(), Advancement.getNameFromIdentity(advancementEntry));
    }

    private static AdvancementFrame[] method_36593() {
        return new AdvancementFrame[]{TASK, CHALLENGE, GOAL};
    }

    static {
        field_1253 = AdvancementFrame.method_36593();
        CODEC = StringIdentifiable.createCodec(AdvancementFrame::values);
    }
}

