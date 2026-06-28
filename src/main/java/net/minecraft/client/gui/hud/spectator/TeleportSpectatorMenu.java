/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class TeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    final static private Identifier TELEPORT_TO_PLAYER_TEXTURE = Identifier.ofVanilla("spectator/teleport_to_player");
    final static private Comparator<PlayerListEntry> ORDERING = Comparator.comparing(a -> a.getProfile().getId());
    final static private Text TELEPORT_TEXT = Text.translatable("spectatorMenu.teleport");
    final static private Text PROMPT_TEXT = Text.translatable("spectatorMenu.teleport.prompt");
    final private List<SpectatorMenuCommand> elements;

    public TeleportSpectatorMenu() {
        this(MinecraftClient.getInstance().getNetworkHandler().getListedPlayerListEntries());
    }

    public TeleportSpectatorMenu(Collection<PlayerListEntry> entries) {
        this.elements = entries.stream().filter(entry -> entry.getGameMode() != GameMode.SPECTATOR).sorted(ORDERING).map(entry -> new TeleportToSpecificPlayerSpectatorCommand(entry.getProfile())).toList();
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }

    @Override
    public Text getPrompt() {
        return PROMPT_TEXT;
    }

    @Override
    public void use(SpectatorMenu menu) {
        menu.selectElement(this);
    }

    @Override
    public Text getName() {
        return TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(DrawContext context, float brightness, float alpha) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TELEPORT_TO_PLAYER_TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
    }

    @Override
    public boolean isEnabled() {
        return !this.elements.isEmpty();
    }
}

