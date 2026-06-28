/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    final private Screen parent;
    final private ErrorMessages errorMessages;
    private MultilineText description = MultilineText.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.errorMessages = RealmsGenericErrorScreen.getErrorMessages(realmsServiceException);
    }

    public RealmsGenericErrorScreen(Text description, Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.errorMessages = RealmsGenericErrorScreen.getErrorMessages(description);
    }

    public RealmsGenericErrorScreen(Text title, Text description, Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.errorMessages = RealmsGenericErrorScreen.getErrorMessages(title, description);
    }

    private static ErrorMessages getErrorMessages(RealmsServiceException exception) {
        RealmsError realmsError = exception.error;
        return RealmsGenericErrorScreen.getErrorMessages(Text.translatable("mco.errorMessage.realmsService.realmsError", realmsError.getErrorCode()), realmsError.getText());
    }

    private static ErrorMessages getErrorMessages(Text description) {
        return RealmsGenericErrorScreen.getErrorMessages(Text.translatable("mco.errorMessage.generic"), description);
    }

    private static ErrorMessages getErrorMessages(Text title, Text description) {
        return new ErrorMessages(title, description);
    }

    @Override
    public void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.OK, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 52, 200, 20).build());
        this.description = MultilineText.create(this.textRenderer, this.errorMessages.detail, this.width * 3 / 4);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public Text getNarratedTitle() {
        return Text.empty().append(this.errorMessages.title).append(": ").append(this.errorMessages.detail);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.errorMessages.title, this.width / 2, 80, Colors.WHITE);
        this.description.drawCenterWithShadow(context, this.width / 2, 100, this.client.textRenderer.fontHeight, -2142128);
    }

    @Environment(value=EnvType.CLIENT)
    static final class ErrorMessages
    extends Record {
        final Text title;
        final Text detail;

        ErrorMessages(Text text, Text text2) {
            this.title = text;
            this.detail = text2;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorMessages.class, "title;detail", "title", "detail"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorMessages.class, "title;detail", "title", "detail"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorMessages.class, "title;detail", "title", "detail"}, this, object);
        }

        public Text title() {
            return this.title;
        }

        public Text detail() {
            return this.detail;
        }
    }
}

