/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setworldspawn").requires(CommandManager.requirePermissionLevel(2))).executes(context -> SetWorldSpawnCommand.execute((ServerCommandSource)context.getSource(), BlockPos.ofFloored(((ServerCommandSource)context.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> SetWorldSpawnCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getValidBlockPos((CommandContext<ServerCommandSource>)context, "pos"), 0.0f))).then(CommandManager.argument("angle", AngleArgumentType.angle()).executes(context -> SetWorldSpawnCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getValidBlockPos((CommandContext<ServerCommandSource>)context, "pos"), AngleArgumentType.getAngle((CommandContext<ServerCommandSource>)context, "angle"))))));
    }

    private static int execute(ServerCommandSource source, BlockPos pos, float angle) {
        ServerWorld serverWorld = source.getWorld();
        if (serverWorld.getRegistryKey() != World.OVERWORLD) {
            source.sendError(Text.translatable("commands.setworldspawn.failure.not_overworld"));
            return 0;
        }
        serverWorld.setSpawnPos(pos, angle);
        source.sendFeedback(() -> Text.translatable("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), Float.valueOf(angle)), true);
        return 1;
    }
}

