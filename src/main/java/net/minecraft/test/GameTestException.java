/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import net.minecraft.test.TestException;
import net.minecraft.text.Text;

public class GameTestException
extends TestException {
    final protected Text message;
    final protected int tick;

    public GameTestException(Text message, int tick) {
        super(message.getString());
        this.message = message;
        this.tick = tick;
    }

    @Override
    public Text getText() {
        return Text.translatable("test.error.tick", this.message, this.tick);
    }

    @Override
    public String getMessage() {
        return this.getText().getString();
    }
}

