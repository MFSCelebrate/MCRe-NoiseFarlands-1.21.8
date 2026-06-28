/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class AttackIndicator
extends Enum<AttackIndicator>
implements TranslatableOption {
    final static public AttackIndicator OFF = new AttackIndicator(0, "options.off");
    final static public AttackIndicator CROSSHAIR = new AttackIndicator(1, "options.attack.crosshair");
    final static public AttackIndicator HOTBAR = new AttackIndicator(2, "options.attack.hotbar");
    final static private IntFunction<AttackIndicator> BY_ID;
    final private int id;
    final private String translationKey;
    final static private AttackIndicator[] field_18157;

    public static AttackIndicator[] values() {
        return (AttackIndicator[])field_18157.clone();
    }

    public static AttackIndicator valueOf(String string) {
        return Enum.valueOf(AttackIndicator.class, string);
    }

    private AttackIndicator(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static AttackIndicator byId(int id) {
        return BY_ID.apply(id);
    }

    private static AttackIndicator[] method_36858() {
        return new AttackIndicator[]{OFF, CROSSHAIR, HOTBAR};
    }

    static {
        field_18157 = AttackIndicator.method_36858();
        BY_ID = ValueLists.createIndexToValueFunction(AttackIndicator::getId, AttackIndicator.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

