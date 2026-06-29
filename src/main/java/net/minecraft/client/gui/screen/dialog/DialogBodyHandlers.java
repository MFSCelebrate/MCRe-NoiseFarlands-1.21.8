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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandler;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ItemStackWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class DialogBodyHandlers {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Map<MapCodec<? extends DialogBody>, DialogBodyHandler<?>> DIALOG_BODY_HANDLERS = new HashMap();

    private static <B extends DialogBody> void register(MapCodec<B> dialogBodyCodec, DialogBodyHandler<? super B> dialogBodyHandler) {
        DIALOG_BODY_HANDLERS.put(dialogBodyCodec, dialogBodyHandler);
    }

    @Nullable
    private static <B extends DialogBody> DialogBodyHandler<B> getHandler(B dialogBody) {
        return DIALOG_BODY_HANDLERS.get(dialogBody.getTypeCodec());
    }

    @Nullable
    public static <B extends DialogBody> Widget createWidget(DialogScreen<?> dialogScreen, B dialogBody) {
        DialogBodyHandler<B> dialogBodyHandler = DialogBodyHandlers.getHandler(dialogBody);
        if (dialogBodyHandler == null) {
            LOGGER.warn("Unrecognized dialog body {}", dialogBody);
            return null;
        }
        return dialogBodyHandler.createWidget(dialogScreen, dialogBody);
    }

    public static void bootstrap() {
        DialogBodyHandlers.register(PlainMessageDialogBody.CODEC, new PlainMessageDialogBodyHandler());
        DialogBodyHandlers.register(ItemDialogBody.CODEC, new ItemDialogBodyHandler());
    }

    static void runActionFromStyle(DialogScreen<?> dialogScreen, @Nullable Style style) {
        ClickEvent clickEvent;
        if (style != null && (clickEvent = style.getClickEvent()) != null) {
            dialogScreen.runAction(Optional.of(clickEvent));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PlainMessageDialogBodyHandler
    implements DialogBodyHandler<PlainMessageDialogBody> {
        PlainMessageDialogBodyHandler() {
        }

        @Override
        public Widget createWidget(DialogScreen<?> dialogScreen, PlainMessageDialogBody plainMessageDialogBody) {
            return new NarratedMultilineTextWidget(plainMessageDialogBody.width(), plainMessageDialogBody.contents(), dialogScreen.getTextRenderer(), false, false, 4).setStyleConfig(true, style -> DialogBodyHandlers.runActionFromStyle(dialogScreen, style)).setCentered(true);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ItemDialogBodyHandler
    implements DialogBodyHandler<ItemDialogBody> {
        ItemDialogBodyHandler() {
        }

        @Override
        public Widget createWidget(DialogScreen<?> dialogScreen, ItemDialogBody itemDialogBody) {
            if (itemDialogBody.description().isPresent()) {
                PlainMessageDialogBody plainMessageDialogBody = itemDialogBody.description().get();
                DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(2);
                directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
                ItemStackWidget itemStackWidget = new ItemStackWidget(MinecraftClient.getInstance(), 0, 0, itemDialogBody.width(), itemDialogBody.height(), ScreenTexts.EMPTY, itemDialogBody.item(), itemDialogBody.showDecorations(), itemDialogBody.showTooltip());
                directionalLayoutWidget.add(itemStackWidget);
                directionalLayoutWidget.add(new NarratedMultilineTextWidget(plainMessageDialogBody.width(), plainMessageDialogBody.contents(), dialogScreen.getTextRenderer(), false, false, 4).setStyleConfig(true, style -> DialogBodyHandlers.runActionFromStyle(dialogScreen, style)));
                return directionalLayoutWidget;
            }
            return new ItemStackWidget(MinecraftClient.getInstance(), 0, 0, itemDialogBody.width(), itemDialogBody.height(), itemDialogBody.item().getName(), itemDialogBody.item(), itemDialogBody.showDecorations(), itemDialogBody.showTooltip());
        }
    }
}

