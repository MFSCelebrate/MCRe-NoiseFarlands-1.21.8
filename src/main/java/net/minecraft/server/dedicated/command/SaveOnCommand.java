/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SaveOnCommand {
    final static private SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.save.alreadyOn"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-on").requires(CommandManager.requirePermissionLevel(4))).executes(context -> {
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            boolean bl = false;
            for (ServerWorld serverWorld : serverCommandSource.getServer().getWorlds()) {
                if (serverWorld == null || !serverWorld.savingDisabled) continue;
                serverWorld.savingDisabled = false;
                bl = true;
            }
            if (!bl) {
                throw ALREADY_ON_EXCEPTION.create();
            }
            serverCommandSource.sendFeedback(() -> Text.translatable("commands.save.enabled"), true);
            return 1;
        }));
    }
}

