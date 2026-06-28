/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.navigation.Navigable;

@Environment(value=EnvType.CLIENT)
public interface Selectable
extends Navigable,
Narratable {
    public SelectionType getType();

    default public boolean isNarratable() {
        return true;
    }

    default public Collection<? extends Selectable> getNarratedParts() {
        return List.of(this);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class SelectionType
    extends Enum<SelectionType> {
        final static public SelectionType NONE = new SelectionType();
        final static public SelectionType HOVERED = new SelectionType();
        final static public SelectionType FOCUSED = new SelectionType();
        final static private SelectionType[] field_33787;

        public static SelectionType[] values() {
            return (SelectionType[])field_33787.clone();
        }

        public static SelectionType valueOf(String string) {
            return Enum.valueOf(SelectionType.class, string);
        }

        public boolean isFocused() {
            return this == FOCUSED;
        }

        private static SelectionType[] method_37029() {
            return new SelectionType[]{NONE, HOVERED, FOCUSED};
        }

        static {
            field_33787 = SelectionType.method_37029();
        }
    }
}

