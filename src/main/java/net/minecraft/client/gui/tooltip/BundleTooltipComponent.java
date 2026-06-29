/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.math.Fraction
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.tooltip;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BundleTooltipComponent
implements TooltipComponent {
    final static private Identifier BUNDLE_PROGRESS_BAR_BORDER_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_border");
    final static private Identifier BUNDLE_PROGRESS_BAR_FILL_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_fill");
    final static private Identifier BUNDLE_PROGRESS_BAR_FULL_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_full");
    final static private Identifier BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.ofVanilla("container/bundle/slot_highlight_back");
    final static private Identifier BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.ofVanilla("container/bundle/slot_highlight_front");
    final static private Identifier BUNDLE_SLOT_BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/slot_background");
    final static private int SLOTS_PER_ROW = 4;
    final static private int SLOT_DIMENSION = 24;
    final static private int ROW_WIDTH = 96;
    final static private int field_52816 = 13;
    final static private int field_52817 = 96;
    final static private int field_52818 = 1;
    final static private int PROGRESS_BAR_WIDTH = 94;
    final static private int field_52820 = 4;
    final static private Text BUNDLE_FULL = Text.translatable("item.minecraft.bundle.full");
    final static private Text BUNDLE_EMPTY = Text.translatable("item.minecraft.bundle.empty");
    final static private Text BUNDLE_EMPTY_DESCRIPTION = Text.translatable("item.minecraft.bundle.empty.description");
    final private BundleContentsComponent bundleContents;

    public BundleTooltipComponent(BundleContentsComponent bundleContents) {
        this.bundleContents = bundleContents;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return this.bundleContents.isEmpty() ? BundleTooltipComponent.getHeightOfEmpty(textRenderer) : this.getHeightOfNonEmpty();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 96;
    }

    @Override
    public boolean isSticky() {
        return true;
    }

    private static int getHeightOfEmpty(TextRenderer textRenderer) {
        return BundleTooltipComponent.getDescriptionHeight(textRenderer) + 13 + 8;
    }

    private int getHeightOfNonEmpty() {
        return this.getRowsHeight() + 13 + 8;
    }

    private int getRowsHeight() {
        return this.getRows() * 24;
    }

    private int getXMargin(int width) {
        return (width - 96) / 2;
    }

    private int getRows() {
        return MathHelper.ceilDiv(this.getNumVisibleSlots(), 4);
    }

    private int getNumVisibleSlots() {
        return Math.min(12, this.bundleContents.size());
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        if (this.bundleContents.isEmpty()) {
            this.drawEmptyTooltip(textRenderer, x, y, width, height, context);
        } else {
            this.drawNonEmptyTooltip(textRenderer, x, y, width, height, context);
        }
    }

    private void drawEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        BundleTooltipComponent.drawEmptyDescription(x + this.getXMargin(width), y, textRenderer, context);
        this.drawProgressBar(x + this.getXMargin(width), y + BundleTooltipComponent.getDescriptionHeight(textRenderer) + 4, textRenderer, context);
    }

    private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        boolean bl = this.bundleContents.size() > 12;
        List<ItemStack> list = this.firstStacksInContents(this.bundleContents.getNumberOfStacksShown());
        int i = x + this.getXMargin(width) + 96;
        int j = y + this.getRows() * 24;
        int k = 1;
        for (int l = 1; l <= this.getRows(); ++l) {
            for (int m = 1; m <= 4; ++m) {
                int n = i - m * 24;
                int o = j - l * 24;
                if (BundleTooltipComponent.shouldDrawExtraItemsCount(bl, m, l)) {
                    BundleTooltipComponent.drawExtraItemsCount(n, o, this.numContentItemsAfter(list), textRenderer, context);
                    continue;
                }
                if (!BundleTooltipComponent.shouldDrawItem(list, k)) continue;
                this.drawItem(k, n, o, list, k, textRenderer, context);
                ++k;
            }
        }
        this.drawSelectedItemTooltip(textRenderer, context, x, y, width);
        this.drawProgressBar(x + this.getXMargin(width), y + this.getRowsHeight() + 4, textRenderer, context);
    }

    private List<ItemStack> firstStacksInContents(int numberOfStacksShown) {
        int i = Math.min(this.bundleContents.size(), numberOfStacksShown);
        return this.bundleContents.stream().toList().subList(0, i);
    }

    private static boolean shouldDrawExtraItemsCount(boolean hasMoreItems, int column, int row) {
        return hasMoreItems && column * row == 1;
    }

    private static boolean shouldDrawItem(List<ItemStack> items, int itemIndex) {
        return items.size() >= itemIndex;
    }

    private int numContentItemsAfter(List<ItemStack> items) {
        return this.bundleContents.stream().skip(items.size()).mapToInt(ItemStack::getCount).sum();
    }

    private void drawItem(int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext drawContext) {
        int i = stacks.size() - index;
        boolean bl = i == this.bundleContents.getSelectedStackIndex();
        ItemStack itemStack = stacks.get(i);
        if (bl) {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE, x, y, 24, 24);
        } else {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_BACKGROUND_TEXTURE, x, y, 24, 24);
        }
        drawContext.drawItem(itemStack, x + 4, y + 4, seed);
        drawContext.drawStackOverlay(textRenderer, itemStack, x + 4, y + 4);
        if (bl) {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, 24, 24);
        }
    }

    private static void drawExtraItemsCount(int x, int y, int numExtra, TextRenderer textRenderer, DrawContext drawContext) {
        drawContext.drawCenteredTextWithShadow(textRenderer, "+" + numExtra, x + 12, y + 10, Colors.WHITE);
    }

    private void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y, int width) {
        if (this.bundleContents.hasSelectedStack()) {
            ItemStack itemStack = this.bundleContents.get(this.bundleContents.getSelectedStackIndex());
            Text text = itemStack.getFormattedName();
            int i = textRenderer.getWidth(text.asOrderedText());
            int j = x + width / 2 - 12;
            TooltipComponent tooltipComponent = TooltipComponent.of(text.asOrderedText());
            drawContext.drawTooltipImmediately(textRenderer, List.of(tooltipComponent), j - i / 2, y - 15, HoveredTooltipPositioner.INSTANCE, itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }

    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getProgressBarFillTexture(), x + 1, y, this.getProgressBarFill(), 13);
        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_PROGRESS_BAR_BORDER_TEXTURE, x, y, 96, 13);
        Text text = this.getProgressBarLabel();
        if (text != null) {
            drawContext.drawCenteredTextWithShadow(textRenderer, text, x + 48, y + 3, Colors.WHITE);
        }
    }

    private static void drawEmptyDescription(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
        drawContext.drawWrappedTextWithShadow(textRenderer, BUNDLE_EMPTY_DESCRIPTION, x, y, 96, -5592406);
    }

    private static int getDescriptionHeight(TextRenderer textRenderer) {
        return textRenderer.wrapLines(BUNDLE_EMPTY_DESCRIPTION, 96).size() * textRenderer.fontHeight;
    }

    private int getProgressBarFill() {
        return MathHelper.clamp(MathHelper.multiplyFraction(this.bundleContents.getOccupancy(), 94), 0, 94);
    }

    private Identifier getProgressBarFillTexture() {
        return this.bundleContents.getOccupancy().compareTo(Fraction.ONE) >= 0 ? BUNDLE_PROGRESS_BAR_FULL_TEXTURE : BUNDLE_PROGRESS_BAR_FILL_TEXTURE;
    }

    @Nullable
    private Text getProgressBarLabel() {
        if (this.bundleContents.isEmpty()) {
            return BUNDLE_EMPTY;
        }
        if (this.bundleContents.getOccupancy().compareTo(Fraction.ONE) >= 0) {
            return BUNDLE_FULL;
        }
        return null;
    }
}

