/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class WhitelistCommand {
    final static private SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.whitelist.alreadyOn"));
    final static private SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.whitelist.alreadyOff"));
    final static private SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.whitelist.add.failed"));
    final static private SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.whitelist.remove.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("whitelist").requires(CommandManager.requirePermissionLevel(3))).then(CommandManager.literal("on").executes(context -> WhitelistCommand.executeOn((ServerCommandSource)context.getSource())))).then(CommandManager.literal("off").executes(context -> WhitelistCommand.executeOff((ServerCommandSource)context.getSource())))).then(CommandManager.literal("list").executes(context -> WhitelistCommand.executeList((ServerCommandSource)context.getSource())))).then(CommandManager.literal("add").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            PlayerManager playerManager = ((ServerCommandSource)context.getSource()).getServer().net_minecraft_server_PlayerManager_getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(player -> !playerManager.getWhitelist().isAllowed(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
        }).executes(context -> WhitelistCommand.executeAdd((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets")))))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getServer().net_minecraft_server_PlayerManager_getPlayerManager().getWhitelistedNames(), builder)).executes(context -> WhitelistCommand.executeRemove((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets")))))).then(CommandManager.literal("reload").executes(context -> WhitelistCommand.executeReload((ServerCommandSource)context.getSource()))));
    }

    private static int executeReload(ServerCommandSource source) {
        source.getServer().net_minecraft_server_PlayerManager_getPlayerManager().reloadWhitelist();
        source.sendFeedback(() -> Text.translatable("commands.whitelist.reloaded"), true);
        source.getServer().kickNonWhitelistedPlayers(source);
        return 1;
    }

    private static int executeAdd(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Whitelist whitelist = source.getServer().net_minecraft_server_PlayerManager_getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (whitelist.isAllowed(gameProfile)) continue;
            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.add(whitelistEntry);
            source.sendFeedback(() -> Text.translatable("commands.whitelist.add.success", Text.literal(gameProfile.getName())), true);
            ++i;
        }
        if (1 == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        return 1;
    }

    private static int executeRemove(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Whitelist whitelist = source.getServer().net_minecraft_server_PlayerManager_getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (!whitelist.isAllowed(gameProfile)) continue;
            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.remove(whitelistEntry);
            source.sendFeedback(() -> Text.translatable("commands.whitelist.remove.success", Text.literal(gameProfile.getName())), true);
            ++i;
        }
        if (1 == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        source.getServer().kickNonWhitelistedPlayers(source);
        return 1;
    }

    private static int executeOn(ServerCommandSource source) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().net_minecraft_server_PlayerManager_getPlayerManager();
        if (playerManager.isWhitelistEnabled()) {
            throw ALREADY_ON_EXCEPTION.create();
        }
        playerManager.setWhitelistEnabled(true);
        source.sendFeedback(() -> Text.translatable("commands.whitelist.enabled"), true);
        source.getServer().kickNonWhitelistedPlayers(source);
        return 1;
    }

    private static int executeOff(ServerCommandSource source) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().net_minecraft_server_PlayerManager_getPlayerManager();
        if (!playerManager.isWhitelistEnabled()) {
            throw ALREADY_OFF_EXCEPTION.create();
        }
        playerManager.setWhitelistEnabled(false);
        source.sendFeedback(() -> Text.translatable("commands.whitelist.disabled"), true);
        return 1;
    }

    private static int executeList(ServerCommandSource source) {
        String[] strings = source.getServer().net_minecraft_server_PlayerManager_getPlayerManager().getWhitelistedNames();
        if (strings.length == 0) {
            source.sendFeedback(() -> Text.translatable("commands.whitelist.none"), false);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.whitelist.list", strings.length, String.join((CharSequence)", ", strings)), false);
        }
        return strings.length;
    }
}

