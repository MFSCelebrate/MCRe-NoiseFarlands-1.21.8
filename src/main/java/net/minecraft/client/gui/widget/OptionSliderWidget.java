/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;

@Environment(value=EnvType.CLIENT)
public abstract class OptionSliderWidget
extends SliderWidget {
    final protected GameOptions options;

    protected OptionSliderWidget(GameOptions options, int x, int y, int width, int height, double value) {
        super(x, y, width, height, ScreenTexts.EMPTY, value);
        this.options = options;
    }
}

