/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class IconWidget
extends ClickableWidget {
    IconWidget(int x, int y, int width, int height) {
        super(x, y, width, height, ScreenTexts.EMPTY);
    }

    public static IconWidget create(int width, int height, Identifier texture, int textureWidth, int textureHeight) {
        return new Texture(0, 0, width, height, texture, textureWidth, textureHeight);
    }

    public static IconWidget create(int width, int height, Identifier texture) {
        return new Simple(0, 0, width, height, texture);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public boolean isNarratable() {
        return false;
    }

    public abstract void setTexture(Identifier var1);

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture
    extends IconWidget {
        private Identifier texture;
        final private int textureWidth;
        final private int textureHeight;

        public Texture(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight) {
            super(x, y, width, height);
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), 0.0f, 0.0f, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
        }

        @Override
        public void setTexture(Identifier texture) {
            this.texture = texture;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Simple
    extends IconWidget {
        private Identifier texture;

        public Simple(int x, int y, int width, int height, Identifier texture) {
            super(x, y, width, height);
            this.texture = texture;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public void setTexture(Identifier texture) {
            this.texture = texture;
        }
    }
}

