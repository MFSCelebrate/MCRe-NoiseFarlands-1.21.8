/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.text2speech.Narrator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.GlException;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class NarratorManager {
    final static public Text EMPTY = ScreenTexts.EMPTY;
    final static private Logger LOGGER = LogUtils.getLogger();
    final private MinecraftClient client;
    final private Narrator narrator = Narrator.getNarrator();

    public NarratorManager(MinecraftClient client) {
        this.client = client;
    }

    public void narrateChatMessage(Text message) {
        if (this.getNarratorMode().shouldNarrateChat()) {
            this.narrateText(message);
        }
    }

    public void narrate(Text message) {
        if (this.getNarratorMode().shouldNarrate()) {
            this.narrateText(message);
        }
    }

    public void narrateSystemMessage(Text message) {
        if (this.getNarratorMode().shouldNarrateSystem()) {
            this.narrateText(message);
        }
    }

    private void narrateText(Text message) {
        String string = message.getString();
        if (!string.isEmpty()) {
            this.debugPrintMessage(string);
            this.say(string, false);
        }
    }

    public void narrateSystemImmediately(Text text) {
        this.narrateSystemImmediately(text.getString());
    }

    public void narrateSystemImmediately(String text) {
        if (this.getNarratorMode().shouldNarrateSystem() && !text.isEmpty()) {
            this.debugPrintMessage(text);
            if (this.narrator.active()) {
                this.narrator.clear();
                this.say(text, true);
            }
        }
    }

    private void say(String text, boolean interrupt) {
        this.narrator.say(text, interrupt, this.client.options.getCategorySoundVolume(SoundCategory.VOICE) * this.client.options.getCategorySoundVolume(SoundCategory.MASTER));
    }

    private NarratorMode getNarratorMode() {
        return this.client.options.getNarrator().getValue();
    }

    private void debugPrintMessage(String message) {
        if (SharedConstants.isDevelopment) {
            LOGGER.debug("Narrating: {}", (Object)message.replaceAll("\n", "\\\\n"));
        }
    }

    public void onModeChange(NarratorMode mode) {
        this.clear();
        this.say(Text.translatable("options.narrator").append(" : ").append(mode.getName()).getString(), true);
        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        if (this.narrator.active()) {
            if (mode == NarratorMode.OFF) {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), null);
            } else {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.enabled"), mode.getName());
            }
        } else {
            SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), Text.translatable("options.narrator.notavailable"));
        }
    }

    public boolean isActive() {
        return this.narrator.active();
    }

    public void clear() {
        if (this.getNarratorMode() == NarratorMode.OFF || !this.narrator.active()) {
            return;
        }
        this.narrator.clear();
    }

    public void destroy() {
        this.narrator.destroy();
    }

    public void checkNarratorLibrary(boolean narratorEnabled) {
        if (narratorEnabled && !this.isActive() && !TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)"Failed to initialize text-to-speech library. Do you want to continue?\nIf this problem persists, please report it at bugs.mojang.com", (CharSequence)"yesno", (CharSequence)"error", (boolean)true)) {
            throw new InactiveNarratorLibraryException("Narrator library is not active");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class InactiveNarratorLibraryException
    extends GlException {
        public InactiveNarratorLibraryException(String string) {
            super(string);
        }
    }
}

