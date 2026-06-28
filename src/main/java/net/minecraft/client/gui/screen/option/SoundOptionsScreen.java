/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SoundOptionsScreen
extends GameOptionsScreen {
    final static private Text TITLE_TEXT = Text.translatable("options.sounds.title");

    public SoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, TITLE_TEXT);
    }

    @Override
    protected void addOptions() {
        this.body.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
        this.body.addAll(this.getVolumeOptions());
        this.body.addSingleOptionEntry(this.gameOptions.getSoundDevice());
        this.body.addAll(this.gameOptions.getShowSubtitles(), this.gameOptions.getDirectionalAudio());
        this.body.addAll(this.gameOptions.getMusicFrequency(), this.gameOptions.getShowNowPlayingToast());
    }

    private SimpleOption<?>[] getVolumeOptions() {
        return (SimpleOption[])Arrays.stream(SoundCategory.values()).filter(category -> category != SoundCategory.MASTER).map(this.gameOptions::getSoundVolumeOption).toArray(SimpleOption[]::new);
    }
}

