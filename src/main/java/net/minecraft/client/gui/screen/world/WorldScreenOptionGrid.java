/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class WorldScreenOptionGrid {
    final static private int BUTTON_WIDTH = 44;
    final private List<Option> options;
    final private LayoutWidget layout;

    WorldScreenOptionGrid(List<Option> options, LayoutWidget layout) {
        this.options = options;
        this.layout = layout;
    }

    public LayoutWidget getLayout() {
        return this.layout;
    }

    public void refresh() {
        this.options.forEach(Option::refresh);
    }

    public static Builder builder(int width) {
        return new Builder(width);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final int width;
        final private List<OptionBuilder> options = new ArrayList<OptionBuilder>();
        int marginLeft;
        int rowSpacing = 4;
        int rows;
        Optional<TooltipBoxDisplay> tooltipBoxDisplay = Optional.empty();

        public Builder(int width) {
            this.width = width;
        }

        void incrementRows() {
            ++this.rows;
        }

        public OptionBuilder add(Text text, BooleanSupplier getter, Consumer<Boolean> setter) {
            OptionBuilder optionBuilder = new OptionBuilder(text, getter, setter, 44);
            this.options.add(optionBuilder);
            return optionBuilder;
        }

        public Builder marginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder setRowSpacing(int rowSpacing) {
            this.rowSpacing = rowSpacing;
            return this;
        }

        public WorldScreenOptionGrid build() {
            GridWidget gridWidget = new GridWidget().setRowSpacing(this.rowSpacing);
            gridWidget.add(EmptyWidget.ofWidth(this.width - 44), 0, 0);
            gridWidget.add(EmptyWidget.ofWidth(44), 0, 1);
            ArrayList<Option> list = new ArrayList<Option>();
            this.rows = 0;
            for (OptionBuilder optionBuilder : this.options) {
                list.add(optionBuilder.build(this, gridWidget, 0));
            }
            gridWidget.refreshPositions();
            WorldScreenOptionGrid worldScreenOptionGrid = new WorldScreenOptionGrid(list, gridWidget);
            worldScreenOptionGrid.refresh();
            return worldScreenOptionGrid;
        }

        public Builder withTooltipBox(int maxInfoRows, boolean alwaysMaxHeight) {
            this.tooltipBoxDisplay = Optional.of(new TooltipBoxDisplay(maxInfoRows, alwaysMaxHeight));
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class TooltipBoxDisplay
    extends Record {
        final int maxInfoRows;
        final boolean alwaysMaxHeight;

        TooltipBoxDisplay(int i, boolean bl) {
            this.maxInfoRows = i;
            this.alwaysMaxHeight = bl;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TooltipBoxDisplay.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TooltipBoxDisplay.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TooltipBoxDisplay.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this, object);
        }

        public int maxInfoRows() {
            return this.maxInfoRows;
        }

        public boolean alwaysMaxHeight() {
            return this.alwaysMaxHeight;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Option(CyclingButtonWidget<Boolean> button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
        public void refresh() {
            this.button.setValue(this.getter.getAsBoolean());
            if (this.toggleable != null) {
                this.button.active = this.toggleable.getAsBoolean();
            }
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Option.class, "button;stateSupplier;isActiveCondition", "button", "getter", "toggleable"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Option.class, "button;stateSupplier;isActiveCondition", "button", "getter", "toggleable"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Option.class, "button;stateSupplier;isActiveCondition", "button", "getter", "toggleable"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class OptionBuilder {
        final private Text text;
        final private BooleanSupplier getter;
        final private Consumer<Boolean> setter;
        @Nullable
        private Text tooltip;
        @Nullable
        private BooleanSupplier toggleable;
        final private int buttonWidth;

        OptionBuilder(Text text, BooleanSupplier getter, Consumer<Boolean> setter, int buttonWidth) {
            this.text = text;
            this.getter = getter;
            this.setter = setter;
            this.buttonWidth = buttonWidth;
        }

        public OptionBuilder toggleable(BooleanSupplier toggleable) {
            this.toggleable = toggleable;
            return this;
        }

        public OptionBuilder tooltip(Text tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        Option build(Builder gridBuilder, GridWidget gridWidget, int row) {
            boolean bl;
            gridBuilder.incrementRows();
            TextWidget textWidget = new TextWidget(this.text, MinecraftClient.getInstance().textRenderer).alignLeft();
            gridWidget.add(textWidget, gridBuilder.rows, row, gridWidget.copyPositioner().net_minecraft_client_gui_widget_Positioner_relative(0.0f, 0.5f).net_minecraft_client_gui_widget_Positioner_marginLeft(gridBuilder.marginLeft));
            Optional<TooltipBoxDisplay> optional = gridBuilder.tooltipBoxDisplay;
            CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
            builder.omitKeyText();
            boolean bl2 = bl = this.tooltip != null && optional.isEmpty();
            if (bl) {
                Tooltip tooltip = Tooltip.of(this.tooltip);
                builder.tooltip((T value) -> tooltip);
            }
            if (this.tooltip != null && !bl) {
                builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage(), this.tooltip));
            } else {
                builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage()));
            }
            CyclingButtonWidget<Boolean> cyclingButtonWidget = builder.build(0, 0, this.buttonWidth, 20, Text.empty(), (button, value) -> this.setter.accept((Boolean)value));
            if (this.toggleable != null) {
                cyclingButtonWidget.active = this.toggleable.getAsBoolean();
            }
            gridWidget.add(cyclingButtonWidget, gridBuilder.rows, row + 1, gridWidget.copyPositioner().alignRight());
            if (this.tooltip != null) {
                optional.ifPresent(tooltipBoxDisplay -> {
                    MutableText text = this.tooltip.copy().formatted(Formatting.GRAY);
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    MultilineTextWidget multilineTextWidget = new MultilineTextWidget(text, textRenderer);
                    multilineTextWidget.setMaxWidth(builder.width - builder.marginLeft - this.buttonWidth);
                    multilineTextWidget.setMaxRows(tooltipBoxDisplay.maxInfoRows());
                    gridBuilder.incrementRows();
                    int j = tooltipBoxDisplay.alwaysMaxHeight ? textRenderer.fontHeight * tooltipBoxDisplay.maxInfoRows - multilineTextWidget.getHeight() : 0;
                    gridWidget.add(multilineTextWidget, builder.rows, row, gridWidget.copyPositioner().net_minecraft_client_gui_widget_Positioner_marginTop(-builder.rowSpacing).net_minecraft_client_gui_widget_Positioner_marginBottom(j));
                });
            }
            return new Option(cyclingButtonWidget, this.getter, this.toggleable);
        }
    }
}

