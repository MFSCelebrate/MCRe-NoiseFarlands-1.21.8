/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface PackStateChangeCallback {
    public void onStateChanged(UUID var1, State var2);

    public void onFinish(UUID var1, FinishState var2);

    @Environment(value=EnvType.CLIENT)
    public static final class FinishState
    extends Enum<FinishState> {
        final static public FinishState DECLINED = new FinishState();
        final static public FinishState APPLIED = new FinishState();
        final static public FinishState DISCARDED = new FinishState();
        final static public FinishState DOWNLOAD_FAILED = new FinishState();
        final static public FinishState ACTIVATION_FAILED = new FinishState();
        final static private FinishState[] field_47628;

        public static FinishState[] values() {
            return (FinishState[])field_47628.clone();
        }

        public static FinishState valueOf(String string) {
            return Enum.valueOf(FinishState.class, string);
        }

        private static FinishState[] method_55548() {
            return new FinishState[]{DECLINED, APPLIED, DISCARDED, DOWNLOAD_FAILED, ACTIVATION_FAILED};
        }

        static {
            field_47628 = FinishState.method_55548();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class State
    extends Enum<State> {
        final static public State ACCEPTED = new State();
        final static public State DOWNLOADED = new State();
        final static private State[] field_47701;

        public static State[] values() {
            return (State[])field_47701.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private static State[] method_55621() {
            return new State[]{ACCEPTED, DOWNLOADED};
        }

        static {
            field_47701 = State.method_55621();
        }
    }
}

