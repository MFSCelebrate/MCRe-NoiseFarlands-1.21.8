/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class TeamTeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    final static private Identifier TEXTURE = Identifier.ofVanilla("spectator/teleport_to_team");
    final static private Text TEAM_TELEPORT_TEXT = Text.translatable("spectatorMenu.team_teleport");
    final static private Text PROMPT_TEXT = Text.translatable("spectatorMenu.team_teleport.prompt");
    final private List<SpectatorMenuCommand> commands;

    public TeamTeleportSpectatorMenu() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.commands = TeamTeleportSpectatorMenu.getCommands(minecraftClient, minecraftClient.world.net_minecraft_scoreboard_Scoreboard_getScoreboard());
    }

    private static List<SpectatorMenuCommand> getCommands(MinecraftClient client, Scoreboard scoreboard) {
        return scoreboard.getTeams().stream().flatMap(team -> TeleportToSpecificTeamCommand.create(client, team).stream()).toList();
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.commands;
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
        return TEAM_TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(DrawContext context, float brightness, float alpha) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
    }

    @Override
    public boolean isEnabled() {
        return !this.commands.isEmpty();
    }

    @Environment(value=EnvType.CLIENT)
    static class TeleportToSpecificTeamCommand
    implements SpectatorMenuCommand {
        final private Team team;
        final private Supplier<SkinTextures> skinTexturesSupplier;
        final private List<PlayerListEntry> scoreboardEntries;

        private TeleportToSpecificTeamCommand(Team team, List<PlayerListEntry> scoreboardEntries, Supplier<SkinTextures> skinTexturesSupplier) {
            this.team = team;
            this.scoreboardEntries = scoreboardEntries;
            this.skinTexturesSupplier = skinTexturesSupplier;
        }

        public static Optional<SpectatorMenuCommand> create(MinecraftClient client, Team team) {
            ArrayList<PlayerListEntry> list = new ArrayList<PlayerListEntry>();
            for (String string : team.getPlayerList()) {
                PlayerListEntry playerListEntry = client.getNetworkHandler().getPlayerListEntry(string);
                if (playerListEntry == null || playerListEntry.getGameMode() == GameMode.SPECTATOR) continue;
                list.add(playerListEntry);
            }
            if (list.isEmpty()) {
                return Optional.empty();
            }
            GameProfile gameProfile = ((PlayerListEntry)list.get(Random.create().nextInt(list.size()))).getProfile();
            Supplier<SkinTextures> supplier = client.getSkinProvider().getSkinTexturesSupplier(gameProfile);
            return Optional.of(new TeleportToSpecificTeamCommand(team, list, supplier));
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.selectElement(new TeleportSpectatorMenu(this.scoreboardEntries));
        }

        @Override
        public Text getName() {
            return this.team.getDisplayName();
        }

        @Override
        public void renderIcon(DrawContext context, float brightness, float alpha) {
            Integer integer = this.team.getColor().getColorValue();
            if (integer != null) {
                float f = (float)(integer >> 16 & 0xFF) / 255.0f;
                float g = (float)(integer >> 8 & 0xFF) / 255.0f;
                float h = (float)(integer & 0xFF) / 255.0f;
                context.fill(1, 1, 15, 15, ColorHelper.fromFloats(alpha, f * brightness, g * brightness, h * brightness));
            }
            PlayerSkinDrawer.draw(context, this.skinTexturesSupplier.get(), 2, 2, 12, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}

