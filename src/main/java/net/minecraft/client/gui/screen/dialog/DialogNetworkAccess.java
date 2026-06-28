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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface DialogNetworkAccess {
    public void disconnect(Text var1);

    public void runClickEventCommand(String var1, @Nullable Screen var2);

    public void showDialog(RegistryEntry<Dialog> var1, @Nullable Screen var2);

    public void sendCustomClickActionPacket(Identifier var1, Optional<NbtElement> var2);

    public ServerLinks getServerLinks();
}

