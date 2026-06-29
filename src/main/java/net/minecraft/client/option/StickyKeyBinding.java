/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(value=EnvType.CLIENT)
public class StickyKeyBinding
extends KeyBinding {
    final private BooleanSupplier toggleGetter;

    public StickyKeyBinding(String id, int code, String category, BooleanSupplier toggleGetter) {
        super(id, InputUtil.Type.KEYSYM, code, category);
        this.toggleGetter = toggleGetter;
    }

    @Override
    public void setPressed(boolean pressed) {
        if (this.toggleGetter.getAsBoolean()) {
            if (pressed) {
                super.setPressed(!this.isPressed());
            }
        } else {
            super.setPressed(pressed);
        }
    }

    protected void untoggle() {
        super.setPressed(false);
    }
}

