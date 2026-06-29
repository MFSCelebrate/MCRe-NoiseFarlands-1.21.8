/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogListDialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.MultiActionDialogScreen;
import net.minecraft.client.gui.screen.dialog.ServerLinksDialogScreen;
import net.minecraft.client.gui.screen.dialog.SimpleDialogScreen;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.dialog.type.ServerLinksDialog;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DialogScreens {
    final static private Map<MapCodec<? extends Dialog>, Factory<?>> DIALOG_SCREEN_FACTORIES = new HashMap();

    private static <T extends Dialog> void register(MapCodec<T> dialogCodec, Factory<? super T> factory) {
        DIALOG_SCREEN_FACTORIES.put(dialogCodec, factory);
    }

    @Nullable
    public static <T extends Dialog> DialogScreen<T> create(T dialog, @Nullable Screen parent, DialogNetworkAccess networkAccess) {
        Factory<?> factory = DIALOG_SCREEN_FACTORIES.get(dialog.getCodec());
        if (factory != null) {
            return factory.create(parent, dialog, networkAccess);
        }
        return null;
    }

    public static void bootstrap() {
        DialogScreens.register(ConfirmationDialog.CODEC, SimpleDialogScreen::new);
        DialogScreens.register(NoticeDialog.CODEC, SimpleDialogScreen::new);
        DialogScreens.register(DialogListDialog.CODEC, DialogListDialogScreen::new);
        DialogScreens.register(MultiActionDialog.CODEC, MultiActionDialogScreen::new);
        DialogScreens.register(ServerLinksDialog.CODEC, ServerLinksDialogScreen::new);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Factory<T extends Dialog> {
        public DialogScreen<T> create(@Nullable Screen var1, T var2, DialogNetworkAccess var3);
    }
}

