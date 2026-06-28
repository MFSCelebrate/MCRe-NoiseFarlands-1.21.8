/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface Toast {
    final static public Object TYPE = new Object();
    final static public int BASE_WIDTH = 160;
    final static public int BASE_HEIGHT = 32;

    public Visibility getVisibility();

    public void update(ToastManager var1, long var2);

    @Nullable
    default public SoundEvent getSoundEvent() {
        return null;
    }

    public void draw(DrawContext var1, TextRenderer var2, long var3);

    default public Object java_lang_Object_getType() {
        return TYPE;
    }

    default public float getXPos(int scaledWindowWidth, float visibleWidthPortion) {
        return (float)scaledWindowWidth - (float)this.getWidth() * visibleWidthPortion;
    }

    default public float getYPos(int topIndex) {
        return topIndex * this.getHeight();
    }

    default public int getWidth() {
        return 160;
    }

    default public int getHeight() {
        return 32;
    }

    default public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), 32);
    }

    default public void onFinishedRendering() {
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Visibility
    extends Enum<Visibility> {
        final static public Visibility SHOW = new Visibility(SoundEvents.UI_TOAST_IN);
        final static public Visibility HIDE = new Visibility(SoundEvents.UI_TOAST_OUT);
        final private SoundEvent sound;
        final static private Visibility[] field_2212;

        public static Visibility[] values() {
            return (Visibility[])field_2212.clone();
        }

        public static Visibility valueOf(String string) {
            return Enum.valueOf(Visibility.class, string);
        }

        private Visibility(SoundEvent sound) {
            this.sound = sound;
        }

        public void playSound(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(this.sound, 1.0f, 1.0f));
        }

        private static Visibility[] method_36872() {
            return new Visibility[]{SHOW, HIDE};
        }

        static {
            field_2212 = Visibility.method_36872();
        }
    }
}

