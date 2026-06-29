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
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PardonCommand {
    final static private SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.pardon.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("pardon").requires(CommandManager.requirePermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getServer().net_minecraft_server_PlayerManager_getPlayerManager().getUserBanList().getNames(), builder)).executes(context -> PardonCommand.pardon((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets")))));
    }

    private static int pardon(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        BannedPlayerList bannedPlayerList = source.getServer().net_minecraft_server_PlayerManager_getPlayerManager().getUserBanList();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (!bannedPlayerList.contains(gameProfile)) continue;
            bannedPlayerList.remove(gameProfile);
            ++i;
            source.sendFeedback(() -> Text.translatable("commands.pardon.success", Text.literal(gameProfile.getName())), true);
        }
        if (1 == 0) {
            throw ALREADY_UNBANNED_EXCEPTION.create();
        }
        return 1;
    }
}

