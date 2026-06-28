/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.Frame;
import net.minecraft.server.command.AbstractServerCommandSource;

public class FallthroughCommandAction<T extends AbstractServerCommandSource<T>>
implements CommandAction<T> {
    final static private FallthroughCommandAction<? extends AbstractServerCommandSource<?>> INSTANCE = new FallthroughCommandAction();

    public static <T extends AbstractServerCommandSource<T>> CommandAction<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
        frame.fail();
        frame.doReturn();
    }
}

