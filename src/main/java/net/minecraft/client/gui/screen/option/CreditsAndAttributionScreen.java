/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;

@Environment(value=EnvType.CLIENT)
public class CreditsAndAttributionScreen
extends Screen {
    final static private int SPACING = 8;
    final static private int BUTTON_WIDTH = 210;
    final static private Text TITLE = Text.translatable("credits_and_attribution.screen.title");
    final static private Text CREDITS_TEXT = Text.translatable("credits_and_attribution.button.credits");
    final static private Text ATTRIBUTION_TEXT = Text.translatable("credits_and_attribution.button.attribution");
    final static private Text LICENSE_TEXT = Text.translatable("credits_and_attribution.button.licenses");
    final private Screen parent;
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

    public CreditsAndAttributionScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.layout.addHeader(TITLE, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical()).spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add(ButtonWidget.builder(CREDITS_TEXT, button -> this.openCredits()).width(210).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ATTRIBUTION_TEXT, ConfirmLinkScreen.opening((Screen)this, Urls.JAVA_ATTRIBUTION)).width(210).build());
        directionalLayoutWidget.add(ButtonWidget.builder(LICENSE_TEXT, ConfirmLinkScreen.opening((Screen)this, Urls.JAVA_LICENSES)).width(210).build());
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.layout.refreshPositions();
        this.layout.forEachChild(this::addDrawableChild);
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void openCredits() {
        this.client.setScreen(new CreditsScreen(false, () -> this.client.setScreen(this)));
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}

