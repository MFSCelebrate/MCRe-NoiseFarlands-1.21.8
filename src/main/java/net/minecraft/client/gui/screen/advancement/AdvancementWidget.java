/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementWidget {
    final static private Identifier TITLE_BOX_TEXTURE = Identifier.ofVanilla("advancements/title_box");
    final static private int field_32286 = 26;
    final static private int field_32287 = 0;
    final static private int field_32288 = 200;
    final static private int field_32289 = 26;
    final static private int ICON_OFFSET_X = 8;
    final static private int ICON_OFFSET_Y = 5;
    final static private int ICON_SIZE = 26;
    final static private int field_32293 = 3;
    final static private int field_32294 = 5;
    final static private int TITLE_OFFSET_X = 32;
    final static private int TITLE_OFFSET_Y = 9;
    final static private int field_55103 = 8;
    final static private int TITLE_MAX_WIDTH = 163;
    final static private int field_55104 = 80;
    final static private int[] SPLIT_OFFSET_CANDIDATES = new int[]{0, 10, -10, 25, -25};
    final private AdvancementTab tab;
    final private PlacedAdvancement advancement;
    final private AdvancementDisplay display;
    final private List<OrderedText> title;
    final private int width;
    final private List<OrderedText> description;
    final private MinecraftClient client;
    @Nullable
    private AdvancementWidget parent;
    final private List<AdvancementWidget> children = Lists.newArrayList();
    @Nullable
    private AdvancementProgress progress;
    final private int x;
    final private int y;

    public AdvancementWidget(AdvancementTab tab, MinecraftClient client, PlacedAdvancement advancement, AdvancementDisplay display) {
        this.tab = tab;
        this.advancement = advancement;
        this.display = display;
        this.client = client;
        this.title = client.textRenderer.wrapLines(display.getTitle(), 163);
        this.x = MathHelper.floor(display.getX() * 28.0f);
        this.y = MathHelper.floor(display.getY() * 27.0f);
        int i = Math.max(this.title.stream().mapToInt(client.textRenderer::getWidth).max().orElse(0), 80);
        int j = this.getProgressWidth();
        int k = 29 + i + j;
        this.description = Language.getInstance().reorder(this.wrapDescription(Texts.setStyleIfAbsent(display.getDescription().copy(), Style.EMPTY.withColor(display.getFrame().getTitleFormat())), k));
        for (OrderedText orderedText : this.description) {
            k = Math.max(k, client.textRenderer.getWidth(orderedText));
        }
        this.width = k + 3 + 5;
    }

    private int getProgressWidth() {
        int i = this.advancement.getAdvancement().requirements().getLength();
        if (i <= 1) {
            return 0;
        }
        int j = 8;
        MutableText text = Text.translatable("advancements.progress", i, i);
        return this.client.textRenderer.getWidth(text) + 8;
    }

    private static float getMaxWidth(TextHandler textHandler, List<StringVisitable> lines) {
        return (float)lines.stream().mapToDouble(textHandler::getWidth).max().orElse(0.0);
    }

    private List<StringVisitable> wrapDescription(Text text, int width) {
        TextHandler textHandler = this.client.textRenderer.getTextHandler();
        List<StringVisitable> list = null;
        float f = Float.MAX_VALUE;
        for (int i : SPLIT_OFFSET_CANDIDATES) {
            List<StringVisitable> list2 = textHandler.wrapLines(text, width - i, Style.EMPTY);
            float g = Math.abs(AdvancementWidget.getMaxWidth(textHandler, list2) - (float)width);
            if (g <= 10.0f) {
                return list2;
            }
            if (!(g < f)) continue;
            f = g;
            list = list2;
        }
        return list;
    }

    @Nullable
    private AdvancementWidget getParent(PlacedAdvancement advancement) {
        while ((advancement = advancement.getParent()) != null && advancement.getAdvancement().display().isEmpty()) {
        }
        if (advancement == null || advancement.getAdvancement().display().isEmpty()) {
            return null;
        }
        return this.tab.getWidget(advancement.getAdvancementEntry());
    }

    public void renderLines(DrawContext context, int x, int y, boolean border) {
        if (this.parent != null) {
            int n;
            int i = x + this.parent.x + 13;
            int j = x + this.parent.x + 26 + 4;
            int k = y + this.parent.y + 13;
            int l = x + this.x + 13;
            int m = y + this.y + 13;
            int n2 = n = border ? Colors.BLACK : Colors.WHITE;
            if (border) {
                context.drawHorizontalLine(j, i, k - 1, n);
                context.drawHorizontalLine(j + 1, i, k, n);
                context.drawHorizontalLine(j, i, k + 1, n);
                context.drawHorizontalLine(l, j - 1, m - 1, n);
                context.drawHorizontalLine(l, j - 1, m, n);
                context.drawHorizontalLine(l, j - 1, m + 1, n);
                context.drawVerticalLine(j - 1, m, k, n);
                context.drawVerticalLine(j + 1, m, k, n);
            } else {
                context.drawHorizontalLine(j, i, k, n);
                context.drawHorizontalLine(l, j, m, n);
                context.drawVerticalLine(j, m, k, n);
            }
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderLines(context, x, y, border);
        }
    }

    public void renderWidgets(DrawContext context, int x, int y) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus advancementObtainedStatus = f >= 1.0f ? AdvancementObtainedStatus.OBTAINED : AdvancementObtainedStatus.UNOBTAINED;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getFrameTexture(this.display.getFrame()), x + this.x + 3, y + this.y, 26, 26);
            context.drawItemWithoutEntity(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderWidgets(context, x, y);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public void setProgress(AdvancementProgress progress) {
        this.progress = progress;
    }

    public void addChild(AdvancementWidget widget) {
        this.children.add(widget);
    }

    public void drawTooltip(DrawContext context, int originX, int originY, float alpha, int x, int y) {
        AdvancementObtainedStatus advancementObtainedStatus3;
        AdvancementObtainedStatus advancementObtainedStatus2;
        AdvancementObtainedStatus advancementObtainedStatus;
        TextRenderer textRenderer = this.client.textRenderer;
        int i = textRenderer.fontHeight * this.title.size() + 9 + 8;
        int j = originY + this.y + (26 - i) / 2;
        int k = j + i;
        int l = this.description.size() * textRenderer.fontHeight;
        int m = 6 + l;
        boolean bl = x + originX + this.x + this.width + 26 >= this.tab.getScreen().width;
        Text text = this.progress == null ? null : this.progress.getProgressBarFraction();
        int n = text == null ? 0 : textRenderer.getWidth(text);
        boolean bl2 = k + m >= 113;
        float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
        int o = MathHelper.floor(f * (float)this.width);
        if (f >= 1.0f) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
        } else if (o < 2) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (o > this.width - 2) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        }
        int p = this.width - o;
        int q = bl ? originX + this.x - this.width + 26 + 6 : originX + this.x;
        int r = i + m;
        if (!this.description.isEmpty()) {
            if (bl2) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TITLE_BOX_TEXTURE, q, k - r, this.width, r);
            } else {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TITLE_BOX_TEXTURE, q, j, this.width, r);
            }
        }
        if (advancementObtainedStatus != advancementObtainedStatus2) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getBoxTexture(), 200, i, 0, 0, q, j, o, i);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus2.getBoxTexture(), 200, i, 200 - p, 0, q + o, j, p, i);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getBoxTexture(), q, j, this.width, i);
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus3.getFrameTexture(this.display.getFrame()), originX + this.x + 3, originY + this.y, 26, 26);
        int s = q + 5;
        if (bl) {
            this.drawText(context, this.title, s, j + 9, -1);
            if (text != null) {
                context.drawTextWithShadow(textRenderer, text, originX + this.x - n, j + 9, Colors.WHITE);
            }
        } else {
            this.drawText(context, this.title, originX + this.x + 32, j + 9, -1);
            if (text != null) {
                context.drawTextWithShadow(textRenderer, text, originX + this.x + this.width - n - 5, j + 9, Colors.WHITE);
            }
        }
        if (bl2) {
            this.drawText(context, this.description, s, j - l + 1, -16711936);
        } else {
            this.drawText(context, this.description, s, k, -16711936);
        }
        context.drawItemWithoutEntity(this.display.getIcon(), originX + this.x + 8, originY + this.y + 5);
    }

    private void drawText(DrawContext context, List<OrderedText> text, int x, int y, int color) {
        TextRenderer textRenderer = this.client.textRenderer;
        for (int i = 0; i < text.size(); ++i) {
            context.drawTextWithShadow(textRenderer, text.get(i), x, y + i * textRenderer.fontHeight, color);
        }
    }

    public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int i = originX + this.x;
        int j = i + 26;
        int k = originY + this.y;
        int l = k + 26;
        return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
    }

    public void addToTree() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}

