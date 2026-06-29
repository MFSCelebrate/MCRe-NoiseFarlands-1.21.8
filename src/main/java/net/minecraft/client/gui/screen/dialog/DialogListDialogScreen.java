/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DialogListDialogScreen
extends ColumnsDialogScreen<DialogListDialog> {
    public DialogListDialogScreen(@Nullable Screen parent, DialogListDialog dialog, DialogNetworkAccess networkAccess) {
        super(parent, dialog, networkAccess);
    }

    @Override
    protected Stream<DialogActionButtonData> streamActionButtonData(DialogListDialog dialogListDialog, DialogNetworkAccess dialogNetworkAccess) {
        return dialogListDialog.dialogs().stream().map(entry -> DialogListDialogScreen.createButton(dialogListDialog, entry));
    }

    private static DialogActionButtonData createButton(DialogListDialog dialog, RegistryEntry<Dialog> entry) {
        return new DialogActionButtonData(new DialogButtonData(entry.value().common().getExternalTitle(), dialog.buttonWidth()), Optional.of(new SimpleDialogAction(new ClickEvent.ShowDialog(entry))));
    }
}

