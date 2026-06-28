/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.dialog;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class InputControlHandlers {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Map<MapCodec<? extends InputControl>, InputControlHandler<?>> INPUT_CONTROL_HANDLERS = new HashMap();

    private static <T extends InputControl> void register(MapCodec<T> inputControlCodec, InputControlHandler<? super T> inputControlHandler) {
        INPUT_CONTROL_HANDLERS.put(inputControlCodec, inputControlHandler);
    }

    @Nullable
    private static <T extends InputControl> InputControlHandler<T> getHandler(T inputControl) {
        return INPUT_CONTROL_HANDLERS.get(inputControl.getCodec());
    }

    public static <T extends InputControl> void addControl(T inputControl, Screen screen, InputControlHandler.Output output) {
        InputControlHandler<T> inputControlHandler = InputControlHandlers.getHandler(inputControl);
        if (inputControlHandler == null) {
            LOGGER.warn("Unrecognized input control {}", inputControl);
            return;
        }
        inputControlHandler.addControl(inputControl, screen, output);
    }

    public static void bootstrap() {
        InputControlHandlers.register(TextInputControl.CODEC, new TextInputControlHandler());
        InputControlHandlers.register(SingleOptionInputControl.CODEC, new SimpleOptionInputControlHandler());
        InputControlHandlers.register(BooleanInputControl.CODEC, new BooleanInputControlHandler());
        InputControlHandlers.register(NumberRangeInputControl.CODEC, new NumberRangeInputControlHandler());
    }

    @Environment(value=EnvType.CLIENT)
    static class TextInputControlHandler
    implements InputControlHandler<TextInputControl> {
        TextInputControlHandler() {
        }

        @Override
        public void addControl(TextInputControl textInputControl, Screen screen, InputControlHandler.Output output) {
            Supplier<String> supplier;
            ClickableWidget widget;
            TextRenderer textRenderer = screen.getTextRenderer();
            if (textInputControl.multiline().isPresent()) {
                TextInputControl.Multiline multiline = textInputControl.multiline().get();
                int i = multiline.height().orElseGet(() -> {
                    int i = multiline.maxLines().orElse(4);
                    return Math.min(textRenderer.fontHeight * i + 8, 512);
                });
                EditBoxWidget editBoxWidget = EditBoxWidget.builder().build(textRenderer, textInputControl.width(), i, ScreenTexts.EMPTY);
                editBoxWidget.setMaxLength(textInputControl.maxLength());
                multiline.maxLines().ifPresent(editBoxWidget::setMaxLines);
                editBoxWidget.setText(textInputControl.initial());
                widget = editBoxWidget;
                supplier = editBoxWidget::getText;
            } else {
                TextFieldWidget textFieldWidget = new TextFieldWidget(textRenderer, textInputControl.width(), 20, textInputControl.label());
                textFieldWidget.setMaxLength(textInputControl.maxLength());
                textFieldWidget.setText(textInputControl.initial());
                widget = textFieldWidget;
                supplier = textFieldWidget::getText;
            }
            TextFieldWidget widget2 = textInputControl.labelVisible() ? LayoutWidgets.createLabeledWidget(textRenderer, widget, textInputControl.label()) : widget;
            output.accept(widget2, new DialogAction.ValueGetter(){

                @Override
                public String get() {
                    return NbtString.method_72226((String)supplier.get());
                }

                @Override
                public NbtElement getAsNbt() {
                    return NbtString.of((String)supplier.get());
                }
            });
        }

        @Override
        public void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((TextInputControl)inputControl, screen, output);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class SimpleOptionInputControlHandler
    implements InputControlHandler<SingleOptionInputControl> {
        SimpleOptionInputControlHandler() {
        }

        @Override
        public void addControl(SingleOptionInputControl singleOptionInputControl, Screen screen, InputControlHandler.Output output) {
            CyclingButtonWidget.Builder<SingleOptionInputControl.Entry> builder = CyclingButtonWidget.builder(SingleOptionInputControl.Entry::getDisplay).values((Collection<SingleOptionInputControl.Entry>)singleOptionInputControl.entries()).optionTextOmitted(!singleOptionInputControl.labelVisible());
            Optional<SingleOptionInputControl.Entry> optional = singleOptionInputControl.getInitialEntry();
            if (optional.isPresent()) {
                builder = builder.initially(optional.get());
            }
            CyclingButtonWidget<SingleOptionInputControl.Entry> cyclingButtonWidget = builder.build(0, 0, singleOptionInputControl.width(), 20, singleOptionInputControl.label());
            output.accept(cyclingButtonWidget, DialogAction.ValueGetter.of(() -> ((SingleOptionInputControl.Entry)cyclingButtonWidget.getValue()).id()));
        }

        @Override
        public void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((SingleOptionInputControl)inputControl, screen, output);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BooleanInputControlHandler
    implements InputControlHandler<BooleanInputControl> {
        BooleanInputControlHandler() {
        }

        @Override
        public void addControl(final BooleanInputControl booleanInputControl, Screen screen, InputControlHandler.Output output) {
            TextRenderer textRenderer = screen.getTextRenderer();
            final CheckboxWidget checkboxWidget = CheckboxWidget.builder(booleanInputControl.label(), textRenderer).checked(booleanInputControl.initial()).build();
            output.accept(checkboxWidget, new DialogAction.ValueGetter(){

                @Override
                public String get() {
                    return checkboxWidget.isChecked() ? booleanInputControl.onTrue() : booleanInputControl.onFalse();
                }

                @Override
                public NbtElement getAsNbt() {
                    return NbtByte.of(checkboxWidget.isChecked());
                }
            });
        }

        @Override
        public void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((BooleanInputControl)inputControl, screen, output);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class NumberRangeInputControlHandler
    implements InputControlHandler<NumberRangeInputControl> {
        NumberRangeInputControlHandler() {
        }

        @Override
        public void addControl(NumberRangeInputControl numberRangeInputControl, Screen screen, InputControlHandler.Output output) {
            float f = numberRangeInputControl.rangeInfo().getInitialSliderProgress();
            final RangeSliderWidget rangeSliderWidget = new RangeSliderWidget(numberRangeInputControl, f);
            output.accept(rangeSliderWidget, new DialogAction.ValueGetter(){

                @Override
                public String get() {
                    return rangeSliderWidget.getLabel();
                }

                @Override
                public NbtElement getAsNbt() {
                    return NbtFloat.of(rangeSliderWidget.getActualValue());
                }
            });
        }

        @Override
        public void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((NumberRangeInputControl)inputControl, screen, output);
        }

        @Environment(value=EnvType.CLIENT)
        static class RangeSliderWidget
        extends SliderWidget {
            final private NumberRangeInputControl inputControl;

            RangeSliderWidget(NumberRangeInputControl inputControl, double value) {
                super(0, 0, inputControl.width(), 20, RangeSliderWidget.getFormattedLabel(inputControl, value), value);
                this.inputControl = inputControl;
            }

            @Override
            protected void updateMessage() {
                this.setMessage(RangeSliderWidget.getFormattedLabel(this.inputControl, this.value));
            }

            @Override
            protected void applyValue() {
            }

            public String getLabel() {
                return RangeSliderWidget.getLabel(this.inputControl, this.value);
            }

            public float getActualValue() {
                return RangeSliderWidget.getActualValue(this.inputControl, this.value);
            }

            private static float getActualValue(NumberRangeInputControl inputControl, double sliderProgress) {
                return inputControl.rangeInfo().sliderProgressToValue((float)sliderProgress);
            }

            private static String getLabel(NumberRangeInputControl inputControl, double sliderProgress) {
                return RangeSliderWidget.valueToString(RangeSliderWidget.getActualValue(inputControl, sliderProgress));
            }

            private static Text getFormattedLabel(NumberRangeInputControl inputControl, double sliderProgress) {
                return inputControl.getFormattedLabel(RangeSliderWidget.getLabel(inputControl, sliderProgress));
            }

            private static String valueToString(float value) {
                int i = (int)value;
                if ((float)i == value) {
                    return Integer.toString(i);
                }
                return Float.toString(value);
            }
        }
    }
}

