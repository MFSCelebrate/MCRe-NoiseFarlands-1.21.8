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
import net.minecraft.dialog.type.ServerLinksDialog;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ServerLinksDialogScreen
extends ColumnsDialogScreen<ServerLinksDialog> {
    public ServerLinksDialogScreen(@Nullable Screen parent, ServerLinksDialog dialog, DialogNetworkAccess networkAccess) {
        super(parent, dialog, networkAccess);
    }

    @Override
    protected Stream<DialogActionButtonData> streamActionButtonData(ServerLinksDialog serverLinksDialog, DialogNetworkAccess dialogNetworkAccess) {
        return dialogNetworkAccess.getServerLinks().entries().stream().map(entry -> ServerLinksDialogScreen.createButton(serverLinksDialog, entry));
    }

    private static DialogActionButtonData createButton(ServerLinksDialog dialog, ServerLinks.Entry entry) {
        return new DialogActionButtonData(new DialogButtonData(entry.getText(), dialog.buttonWidth()), Optional.of(new SimpleDialogAction(new ClickEvent.OpenUrl(entry.link()))));
    }
}

