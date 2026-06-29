/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BuyRealmsScreen
extends RealmsScreen {
    final static private Text POPUP_TEXT = Text.translatable("mco.selectServer.popup");
    final static private Text CLOSE_TEXT = Text.translatable("mco.selectServer.close");
    final static private Identifier POPUP_BACKGROUND_TEXTURE = Identifier.ofVanilla("popup/background");
    final static private Identifier TRIAL_AVAILABLE_TEXTURE = Identifier.ofVanilla("icon/trial_available");
    final static private ButtonTextures CROSS_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/cross_button"), Identifier.ofVanilla("widget/cross_button_highlighted"));
    final static private int field_45258 = 195;
    final static private int field_45259 = 152;
    final static private int field_45257 = 6;
    final static private int field_45260 = 4;
    final static private int field_45261 = 10;
    final static private int field_45262 = 320;
    final static private int field_45263 = 172;
    final static private int field_45264 = 100;
    final static private int field_45265 = 99;
    final static private int field_45266 = 100;
    static private List<Identifier> realmsImages = List.of();
    final private Screen parent;
    final private boolean trialAvailable;
    @Nullable
    private ButtonWidget trialButton;
    private int realmsImageIndex;
    private int realmsImageDisplayTime;

    public BuyRealmsScreen(Screen parent, boolean trialAvailable) {
        super(POPUP_TEXT);
        this.parent = parent;
        this.trialAvailable = trialAvailable;
    }

    public static void refreshImages(ResourceManager resourceManager) {
        Set<Identifier> collection = resourceManager.findResources("textures/gui/images", id -> id.getPath().endsWith(".png")).keySet();
        realmsImages = collection.stream().filter(id -> id.getNamespace().equals("realms")).toList();
    }

    @Override
    protected void init() {
        this.parent.resize(this.client, this.width, this.height);
        if (this.trialAvailable) {
            this.trialButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.trial"), ConfirmLinkScreen.opening((Screen)this, Urls.JAVA_REALMS_TRIAL)).dimensions(this.getRight() - 10 - 99, this.getBottom() - 10 - 4 - 40, 99, 20).build());
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.buy"), ConfirmLinkScreen.opening((Screen)this, Urls.BUY_JAVA_REALMS)).dimensions(this.getRight() - 10 - 99, this.getBottom() - 10 - 20, 99, 20).build());
        TexturedButtonWidget texturedButtonWidget = this.addDrawableChild(new TexturedButtonWidget(this.getLeft() + 4, this.getTop() + 4, 14, 14, CROSS_BUTTON_TEXTURES, button -> this.close(), CLOSE_TEXT));
        texturedButtonWidget.setTooltip(Tooltip.of(CLOSE_TEXT));
        int i = 142 - (this.trialAvailable ? 40 : 20);
        ScrollableTextWidget scrollableTextWidget = new ScrollableTextWidget(this.getRight() - 10 - 100, this.getTop() + 10, 100, i, POPUP_TEXT, this.textRenderer);
        if (scrollableTextWidget.textOverflows()) {
            scrollableTextWidget.setWidth(94);
        }
        this.addDrawableChild(scrollableTextWidget);
    }

    @Override
    public void tick() {
        super.tick();
        if (++this.realmsImageDisplayTime > 100) {
            this.realmsImageDisplayTime = 0;
            this.realmsImageIndex = (this.realmsImageIndex + 1) % realmsImages.size();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.trialButton != null) {
            BuyRealmsScreen.drawTrialAvailableTexture(context, this.trialButton);
        }
    }

    public static void drawTrialAvailableTexture(DrawContext context, ButtonWidget button) {
        int i = 8;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TRIAL_AVAILABLE_TEXTURE, button.getX() + button.getWidth() - 8 - 4, button.getY() + button.getHeight() / 2 - 4, 8, 8);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.parent.renderBackground(context, -1, -1, deltaTicks);
        context.createNewRootLayer();
        this.parent.render(context, -1, -1, deltaTicks);
        context.createNewRootLayer();
        this.renderInGameBackground(context);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, POPUP_BACKGROUND_TEXTURE, this.getLeft(), this.getTop(), 320, 172);
        if (!realmsImages.isEmpty()) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, realmsImages.get(this.realmsImageIndex), this.getLeft() + 10, this.getTop() + 10, 0.0f, 0.0f, 195, 152, 195, 152);
        }
    }

    private int getLeft() {
        return (this.width - 320) / 2;
    }

    private int getTop() {
        return (this.height - 172) / 2;
    }

    private int getRight() {
        return this.getLeft() + 320;
    }

    private int getBottom() {
        return this.getTop() + 172;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}

