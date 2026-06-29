/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class CreakingHeartState
extends Enum<CreakingHeartState>
implements StringIdentifiable {
    final static public CreakingHeartState UPROOTED = new CreakingHeartState("uprooted");
    final static public CreakingHeartState DORMANT = new CreakingHeartState("dormant");
    final static public CreakingHeartState AWAKE = new CreakingHeartState("awake");
    final private String id;
    final static private CreakingHeartState[] field_55835;

    public static CreakingHeartState[] values() {
        return (CreakingHeartState[])field_55835.clone();
    }

    public static CreakingHeartState valueOf(String string) {
        return Enum.valueOf(CreakingHeartState.class, string);
    }

    private CreakingHeartState(String id) {
        this.id = id;
    }

    public String toString() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static CreakingHeartState[] method_66479() {
        return new CreakingHeartState[]{UPROOTED, DORMANT, AWAKE};
    }

    static {
        field_55835 = CreakingHeartState.method_66479();
    }
}

