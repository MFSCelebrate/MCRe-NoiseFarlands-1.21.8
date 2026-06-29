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
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

@Environment(value=EnvType.CLIENT)
public class Input {
    public PlayerInput playerInput = PlayerInput.DEFAULT;
    protected Vec2f movementVector = Vec2f.ZERO;

    public void tick() {
    }

    public Vec2f getMovementInput() {
        return this.movementVector;
    }

    public boolean hasForwardMovement() {
        return this.movementVector.y > 1.0E-5f;
    }

    public void jump() {
        this.playerInput = new PlayerInput(this.playerInput.forward(), this.playerInput.backward(), this.playerInput.left(), this.playerInput.right(), true, this.playerInput.sneak(), this.playerInput.sprint());
    }
}

