/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.function.Consumer;
import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.Frame;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.command.AbstractServerCommandSource;

public class IsolatedCommandAction<T extends AbstractServerCommandSource<T>>
implements CommandAction<T> {
    final private Consumer<ExecutionControl<T>> controlConsumer;
    final private ReturnValueConsumer returnValueConsumer;

    public IsolatedCommandAction(Consumer<ExecutionControl<T>> controlConsumer, ReturnValueConsumer returnValueConsumer) {
        this.controlConsumer = controlConsumer;
        this.returnValueConsumer = returnValueConsumer;
    }

    @Override
    public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
        int i = frame.depth() + 1;
        Frame frame2 = new Frame(1, this.returnValueConsumer, commandExecutionContext.getEscapeControl(1));
        this.controlConsumer.accept(ExecutionControl.of(commandExecutionContext, frame2));
    }
}

