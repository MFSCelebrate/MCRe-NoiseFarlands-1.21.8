/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class HangingSignEditScreen
extends AbstractSignEditScreen {
    final static public float BACKGROUND_SCALE = 4.5f;
    final static private Vector3f TEXT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    final static private int field_40433 = 16;
    final static private int field_40434 = 16;
    final private Identifier texture;

    public HangingSignEditScreen(SignBlockEntity signBlockEntity, boolean bl, boolean bl2) {
        super(signBlockEntity, bl, bl2, Text.translatable("hanging_sign.edit"));
        this.texture = Identifier.ofVanilla("textures/gui/hanging_signs/" + this.signType.name() + ".png");
    }

    @Override
    protected float getYOffset() {
        return 125.0f;
    }

    @Override
    protected void renderSignBackground(DrawContext context) {
        context.getMatrices().translate(0.0f, -13.0f);
        context.getMatrices().scale(4.5f, 4.5f);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.texture, -8, -8, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    @Override
    protected Vector3f getTextScale() {
        return TEXT_SCALE;
    }
}

