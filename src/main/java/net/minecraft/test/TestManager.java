/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestRunContext;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class TestManager {
    final static public TestManager INSTANCE = new TestManager();
    final private Collection<GameTestState> tests = Lists.newCopyOnWriteArrayList();
    @Nullable
    private TestRunContext runContext;
    private State state = State.IDLE;
    private volatile boolean shouldTick = false;

    private TestManager() {
    }

    public void start(GameTestState test) {
        this.tests.add(test);
    }

    public void clear() {
        if (this.state != State.IDLE) {
            this.state = State.HALTING;
            return;
        }
        this.tests.clear();
        if (this.runContext != null) {
            this.runContext.clear();
            this.runContext = null;
        }
    }

    public void setRunContext(TestRunContext runContext) {
        if (this.runContext != null) {
            Util.logErrorOrPause("The runner was already set in GameTestTicker");
        }
        this.runContext = runContext;
    }

    public void startTicking() {
        this.shouldTick = true;
    }

    public void tick() {
        if (this.runContext == null || !this.shouldTick) {
            return;
        }
        this.state = State.RUNNING;
        this.tests.forEach(test -> test.tick(this.runContext));
        this.tests.removeIf(GameTestState::isCompleted);
        State state = this.state;
        this.state = State.IDLE;
        if (state == State.HALTING) {
            this.clear();
        }
    }

    static final class State
    extends Enum<State> {
        final static public State IDLE = new State();
        final static public State RUNNING = new State();
        final static public State HALTING = new State();
        final static private State[] field_57045;

        public static State[] values() {
            return (State[])field_57045.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private static State[] method_68078() {
            return new State[]{IDLE, RUNNING, HALTING};
        }

        static {
            field_57045 = State.method_68078();
        }
    }
}

