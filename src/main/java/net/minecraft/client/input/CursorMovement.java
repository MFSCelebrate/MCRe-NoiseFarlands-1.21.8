/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class CursorMovement
extends Enum<CursorMovement> {
    final static public CursorMovement ABSOLUTE = new CursorMovement();
    final static public CursorMovement RELATIVE = new CursorMovement();
    final static public CursorMovement END = new CursorMovement();
    final static private CursorMovement[] field_39538;

    public static CursorMovement[] values() {
        return (CursorMovement[])field_39538.clone();
    }

    public static CursorMovement valueOf(String string) {
        return Enum.valueOf(CursorMovement.class, string);
    }

    private static CursorMovement[] method_44446() {
        return new CursorMovement[]{ABSOLUTE, RELATIVE, END};
    }

    static {
        field_39538 = CursorMovement.method_44446();
    }
}

