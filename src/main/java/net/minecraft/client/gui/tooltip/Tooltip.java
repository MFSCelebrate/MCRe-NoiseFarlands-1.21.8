/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.tooltip;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Tooltip
implements Narratable {
    final static private int ROW_LENGTH = 170;
    final private Text content;
    @Nullable
    private List<OrderedText> lines;
    @Nullable
    private Language language;
    @Nullable
    final private Text narration;

    private Tooltip(Text content, @Nullable Text narration) {
        this.content = content;
        this.narration = narration;
    }

    public static Tooltip of(Text content, @Nullable Text narration) {
        return new Tooltip(content, narration);
    }

    public static Tooltip of(Text content) {
        return new Tooltip(content, content);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        if (this.narration != null) {
            builder.put(NarrationPart.HINT, this.narration);
        }
    }

    public List<OrderedText> getLines(MinecraftClient client) {
        Language language = Language.getInstance();
        if (this.lines == null || language != this.language) {
            this.lines = Tooltip.wrapLines(client, this.content);
            this.language = language;
        }
        return this.lines;
    }

    public static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
        return client.textRenderer.wrapLines(text, 170);
    }
}

