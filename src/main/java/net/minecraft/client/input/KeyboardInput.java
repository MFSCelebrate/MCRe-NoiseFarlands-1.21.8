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
import net.minecraft.client.input.Input;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

@Environment(value=EnvType.CLIENT)
public class KeyboardInput
extends Input {
    final private GameOptions settings;

    public KeyboardInput(GameOptions settings) {
        this.settings = settings;
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    @Override
    public void tick() {
        this.playerInput = new PlayerInput(this.settings.forwardKey.isPressed(), this.settings.backKey.isPressed(), this.settings.leftKey.isPressed(), this.settings.rightKey.isPressed(), this.settings.jumpKey.isPressed(), this.settings.sneakKey.isPressed(), this.settings.sprintKey.isPressed());
        float f = KeyboardInput.getMovementMultiplier(this.playerInput.forward(), this.playerInput.backward());
        float g = KeyboardInput.getMovementMultiplier(this.playerInput.left(), this.playerInput.right());
        this.movementVector = new Vec2f(g, f).normalize();
    }
}

