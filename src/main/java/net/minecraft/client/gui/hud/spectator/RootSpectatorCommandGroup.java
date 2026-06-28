/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeamTeleportSpectatorMenu;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RootSpectatorCommandGroup
implements SpectatorMenuCommandGroup {
    final static private Text PROMPT_TEXT = Text.translatable("spectatorMenu.root.prompt");
    final private List<SpectatorMenuCommand> elements = Lists.newArrayList();

    public RootSpectatorCommandGroup() {
        this.elements.add(new TeleportSpectatorMenu());
        this.elements.add(new TeamTeleportSpectatorMenu());
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }

    @Override
    public Text getPrompt() {
        return PROMPT_TEXT;
    }
}

