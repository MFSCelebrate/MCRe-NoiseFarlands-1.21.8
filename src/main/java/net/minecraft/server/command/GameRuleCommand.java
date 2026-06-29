/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        final LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamerule").requires(CommandManager.requirePermissionLevel(2));
        new GameRules(commandRegistryAccess.getEnabledFeatures()).accept(new GameRules.Visitor(){

            @Override
            public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = CommandManager.literal(key.getName());
                literalArgumentBuilder.then(((LiteralArgumentBuilder)literalArgumentBuilder2.executes(context -> GameRuleCommand.executeQuery((ServerCommandSource)context.getSource(), key))).then(type.argument("value").executes(context -> GameRuleCommand.executeSet((CommandContext<ServerCommandSource>)context, key))));
            }
        });
        dispatcher.register(literalArgumentBuilder);
    }

    static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> context, GameRules.Key<T> key) {
        ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
        Object rule = serverCommandSource.getServer().getGameRules().get(key);
        ((GameRules.Rule)rule).set(context, "value");
        serverCommandSource.sendFeedback(() -> Text.translatable("commands.gamerule.set", key.getName(), rule.toString()), true);
        return ((GameRules.Rule)rule).getCommandResult();
    }

    static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource source, GameRules.Key<T> key) {
        Object rule = source.getServer().getGameRules().get(key);
        source.sendFeedback(() -> Text.translatable("commands.gamerule.query", key.getName(), rule.toString()), false);
        return ((GameRules.Rule)rule).getCommandResult();
    }
}

