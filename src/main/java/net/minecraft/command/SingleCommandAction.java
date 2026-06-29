/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ContextChain
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.List;
import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandQueueEntry;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.FixedCommandAction;
import net.minecraft.command.Frame;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;
import net.minecraft.text.Text;

public class SingleCommandAction<T extends AbstractServerCommandSource<T>> {
    @VisibleForTesting
    final static public DynamicCommandExceptionType FORK_LIMIT_EXCEPTION = new DynamicCommandExceptionType(count -> Text.stringifiedTranslatable("command.forkLimit", count));
    final private String command;
    final private ContextChain<T> contextChain;

    public SingleCommandAction(String command, ContextChain<T> contextChain) {
        this.command = command;
        this.contextChain = contextChain;
    }

    /*
     * Exception decompiling
     */
    protected void execute(T baseSource, List<T> sources, CommandExecutionContext<T> context, Frame frame, ExecutionFlags flags) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 8[WHILELOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    protected void traceCommandStart(CommandExecutionContext<T> context, Frame frame) {
        Tracer tracer = context.getTracer();
        if (tracer != null) {
            tracer.traceCommandStart(frame.depth(), this.command);
        }
    }

    public String toString() {
        return this.command;
    }

    private static CommandQueueEntry method_54408(FixedCommandAction fixedCommandAction, Frame frame, AbstractServerCommandSource source) {
        return new CommandQueueEntry<AbstractServerCommandSource>(frame, fixedCommandAction.bind(source));
    }

    private static void method_54897(CommandContext context, boolean successful, int returnValue) {
    }

    private String method_54406() {
        return "prepare " + this.command;
    }

    public static class SingleSource<T extends AbstractServerCommandSource<T>>
    extends SingleCommandAction<T>
    implements CommandAction<T> {
        final private T source;

        public SingleSource(String command, ContextChain<T> contextChain, T source) {
            super(command, contextChain);
            this.source = source;
        }

        @Override
        public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
            this.traceCommandStart(commandExecutionContext, frame);
            this.execute(this.source, List.of(this.source), commandExecutionContext, frame, ExecutionFlags.NONE);
        }
    }

    public static class MultiSource<T extends AbstractServerCommandSource<T>>
    extends SingleCommandAction<T>
    implements CommandAction<T> {
        final private ExecutionFlags flags;
        final private T baseSource;
        final private List<T> sources;

        public MultiSource(String command, ContextChain<T> contextChain, ExecutionFlags flags, T baseSource, List<T> sources) {
            super(command, contextChain);
            this.baseSource = baseSource;
            this.sources = sources;
            this.flags = flags;
        }

        @Override
        public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
            this.execute(this.baseSource, this.sources, commandExecutionContext, frame, this.flags);
        }
    }

    public static class Sourced<T extends AbstractServerCommandSource<T>>
    extends SingleCommandAction<T>
    implements SourcedCommandAction<T> {
        public Sourced(String string, ContextChain<T> contextChain) {
            super(string, contextChain);
        }

        @Override
        public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, Frame frame) {
            this.traceCommandStart(commandExecutionContext, frame);
            this.execute(abstractServerCommandSource, List.of(abstractServerCommandSource), commandExecutionContext, frame, ExecutionFlags.NONE);
        }

        @Override
        public void execute(Object object, CommandExecutionContext commandExecutionContext, Frame frame) {
            this.execute((AbstractServerCommandSource)object, commandExecutionContext, frame);
        }
    }
}

