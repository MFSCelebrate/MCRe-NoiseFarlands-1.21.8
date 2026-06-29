/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.report.AbuseReportTypeScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SocialInteractionsPlayerListEntry
extends ElementListWidget.Entry<SocialInteractionsPlayerListEntry> {
    final static private Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla("icon/draft_report");
    final static private Duration TOOLTIP_DELAY = Duration.ofMillis(500L);
    final static private ButtonTextures REPORT_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("social_interactions/report_button"), Identifier.ofVanilla("social_interactions/report_button_disabled"), Identifier.ofVanilla("social_interactions/report_button_highlighted"));
    final static private ButtonTextures MUTE_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("social_interactions/mute_button"), Identifier.ofVanilla("social_interactions/mute_button_highlighted"));
    final static private ButtonTextures UNMUTE_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("social_interactions/unmute_button"), Identifier.ofVanilla("social_interactions/unmute_button_highlighted"));
    final private MinecraftClient client;
    final private List<ClickableWidget> buttons;
    final private UUID uuid;
    final private String name;
    final private Supplier<SkinTextures> skinSupplier;
    private boolean offline;
    private boolean sentMessage;
    final private boolean canSendReports;
    private boolean hasDraftReport;
    final private boolean reportable;
    @Nullable
    private ButtonWidget hideButton;
    @Nullable
    private ButtonWidget showButton;
    @Nullable
    private ButtonWidget reportButton;
    private float timeCounter;
    final static private Text HIDDEN_TEXT = Text.translatable("gui.socialInteractions.status_hidden").formatted(Formatting.ITALIC);
    final static private Text BLOCKED_TEXT = Text.translatable("gui.socialInteractions.status_blocked").formatted(Formatting.ITALIC);
    final static private Text OFFLINE_TEXT = Text.translatable("gui.socialInteractions.status_offline").formatted(Formatting.ITALIC);
    final static private Text HIDDEN_OFFLINE_TEXT = Text.translatable("gui.socialInteractions.status_hidden_offline").formatted(Formatting.ITALIC);
    final static private Text BLOCKED_OFFLINE_TEXT = Text.translatable("gui.socialInteractions.status_blocked_offline").formatted(Formatting.ITALIC);
    final static private Text REPORT_DISABLED_TEXT = Text.translatable("gui.socialInteractions.tooltip.report.disabled");
    final static private Text HIDE_TEXT = Text.translatable("gui.socialInteractions.tooltip.hide");
    final static private Text SHOW_TEXT = Text.translatable("gui.socialInteractions.tooltip.show");
    final static private Text REPORT_TEXT = Text.translatable("gui.socialInteractions.tooltip.report");
    final static private int field_32420 = 24;
    final static private int field_32421 = 4;
    final static public int BLACK_COLOR = ColorHelper.getArgb(190, 0, 0, 0);
    final static private int field_32422 = 20;
    final static public int GRAY_COLOR = ColorHelper.getArgb(255, 74, 74, 74);
    final static public int DARK_GRAY_COLOR = ColorHelper.getArgb(255, 48, 48, 48);
    final static public int WHITE_COLOR = ColorHelper.getArgb(255, 255, 255, 255);
    final static public int LIGHT_GRAY_COLOR = ColorHelper.getArgb(140, 255, 255, 255);

    public SocialInteractionsPlayerListEntry(MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<SkinTextures> skinTexture, boolean reportable) {
        boolean bl2;
        this.client = client;
        this.uuid = uuid;
        this.name = name;
        this.skinSupplier = skinTexture;
        AbuseReportContext abuseReportContext = client.getAbuseReportContext();
        this.canSendReports = abuseReportContext.getSender().canSendReports();
        this.reportable = reportable;
        this.updateHasDraftReport(abuseReportContext);
        MutableText text = Text.translatable("gui.socialInteractions.narration.hide", name);
        MutableText text2 = Text.translatable("gui.socialInteractions.narration.show", name);
        SocialInteractionsManager socialInteractionsManager = client.getSocialInteractionsManager();
        boolean bl = client.getChatRestriction().allowsChat(client.isInSingleplayer());
        boolean bl3 = bl2 = !client.player.getUuid().equals(uuid);
        if (bl2 && bl && !socialInteractionsManager.isPlayerBlocked(uuid)) {
            this.reportButton = new TexturedButtonWidget(0, 0, 20, 20, REPORT_BUTTON_TEXTURES, button -> abuseReportContext.tryShowDraftScreen(client, parent, () -> client.setScreen(new AbuseReportTypeScreen(parent, abuseReportContext, this)), false), Text.translatable("gui.socialInteractions.report")){

                @Override
                protected MutableText getNarrationMessage() {
                    return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
                }
            };
            this.reportButton.active = this.canSendReports;
            this.reportButton.setTooltip(this.getReportButtonTooltip());
            this.reportButton.setTooltipDelay(TOOLTIP_DELAY);
            this.hideButton = new TexturedButtonWidget(0, 0, 20, 20, MUTE_BUTTON_TEXTURES, button -> {
                socialInteractionsManager.hidePlayer(uuid);
                this.onButtonClick(true, Text.translatable("gui.socialInteractions.hidden_in_chat", name));
            }, Text.translatable("gui.socialInteractions.hide")){

                @Override
                protected MutableText getNarrationMessage() {
                    return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
                }
            };
            this.hideButton.setTooltip(Tooltip.of(HIDE_TEXT, text));
            this.hideButton.setTooltipDelay(TOOLTIP_DELAY);
            this.showButton = new TexturedButtonWidget(0, 0, 20, 20, UNMUTE_BUTTON_TEXTURES, button -> {
                socialInteractionsManager.showPlayer(uuid);
                this.onButtonClick(false, Text.translatable("gui.socialInteractions.shown_in_chat", name));
            }, Text.translatable("gui.socialInteractions.show")){

                @Override
                protected MutableText getNarrationMessage() {
                    return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
                }
            };
            this.showButton.setTooltip(Tooltip.of(SHOW_TEXT, text2));
            this.showButton.setTooltipDelay(TOOLTIP_DELAY);
            this.buttons = new ArrayList<ClickableWidget>();
            this.buttons.add(this.hideButton);
            this.buttons.add(this.reportButton);
            this.setShowButtonVisible(socialInteractionsManager.isPlayerHidden(this.uuid));
        } else {
            this.buttons = ImmutableList.of();
        }
    }

    public void updateHasDraftReport(AbuseReportContext context) {
        this.hasDraftReport = context.draftPlayerUuidEquals(this.uuid);
    }

    private Tooltip getReportButtonTooltip() {
        if (!this.canSendReports) {
            return Tooltip.of(REPORT_DISABLED_TEXT);
        }
        return Tooltip.of(REPORT_TEXT, Text.translatable("gui.socialInteractions.narration.report", this.name));
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int l;
        int i = x + 4;
        int j = y + (entryHeight - 24) / 2;
        int k = i + 24 + 4;
        Text text = this.getStatusText();
        if (text == ScreenTexts.EMPTY) {
            context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
            l = y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
        } else {
            context.fill(x, y, x + entryWidth, y + entryHeight, DARK_GRAY_COLOR);
            l = y + (entryHeight - (this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight)) / 2;
            context.drawTextWithShadow(this.client.textRenderer, text, k, l + 12, LIGHT_GRAY_COLOR);
        }
        PlayerSkinDrawer.draw(context, this.skinSupplier.get(), i, j, 24);
        context.drawTextWithShadow(this.client.textRenderer, this.name, k, l, WHITE_COLOR);
        if (this.offline) {
            context.fill(i, j, i + 24, j + 24, BLACK_COLOR);
        }
        if (this.hideButton != null && this.showButton != null && this.reportButton != null) {
            float f = this.timeCounter;
            this.hideButton.setX(x + (entryWidth - this.hideButton.getWidth() - 4) - 20 - 4);
            this.hideButton.setY(y + (entryHeight - this.hideButton.getHeight()) / 2);
            this.hideButton.render(context, mouseX, mouseY, tickProgress);
            this.showButton.setX(x + (entryWidth - this.showButton.getWidth() - 4) - 20 - 4);
            this.showButton.setY(y + (entryHeight - this.showButton.getHeight()) / 2);
            this.showButton.render(context, mouseX, mouseY, tickProgress);
            this.reportButton.setX(x + (entryWidth - this.showButton.getWidth() - 4));
            this.reportButton.setY(y + (entryHeight - this.showButton.getHeight()) / 2);
            this.reportButton.render(context, mouseX, mouseY, tickProgress);
            if (f == this.timeCounter) {
                this.timeCounter = 0.0f;
            }
        }
        if (this.hasDraftReport && this.reportButton != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.reportButton.getX() + 5, this.reportButton.getY() + 1, 15, 15);
        }
    }

    @Override
    public List<? extends Element> children() {
        return this.buttons;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.buttons;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Supplier<SkinTextures> getSkinSupplier() {
        return this.skinSupplier;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setSentMessage(boolean sentMessage) {
        this.sentMessage = sentMessage;
    }

    public boolean hasSentMessage() {
        return this.sentMessage;
    }

    public boolean isReportable() {
        return this.reportable;
    }

    private void onButtonClick(boolean showButtonVisible, Text chatMessage) {
        this.setShowButtonVisible(showButtonVisible);
        this.client.inGameHud.getChatHud().addMessage(chatMessage);
        this.client.getNarratorManager().narrateSystemImmediately(chatMessage);
    }

    private void setShowButtonVisible(boolean showButtonVisible) {
        this.showButton.visible = showButtonVisible;
        this.hideButton.visible = !showButtonVisible;
        this.buttons.set(0, showButtonVisible ? this.showButton : this.hideButton);
    }

    MutableText getNarrationMessage(MutableText text) {
        Text text2 = this.getStatusText();
        if (text2 == ScreenTexts.EMPTY) {
            return Text.literal(this.name).append(", ").append(text);
        }
        return Text.literal(this.name).append(", ").append(text2).append(", ").append(text);
    }

    private Text getStatusText() {
        boolean bl = this.client.getSocialInteractionsManager().isPlayerHidden(this.uuid);
        boolean bl2 = this.client.getSocialInteractionsManager().isPlayerBlocked(this.uuid);
        if (bl2 && this.offline) {
            return BLOCKED_OFFLINE_TEXT;
        }
        if (bl && this.offline) {
            return HIDDEN_OFFLINE_TEXT;
        }
        if (bl2) {
            return BLOCKED_TEXT;
        }
        if (bl) {
            return HIDDEN_TEXT;
        }
        if (this.offline) {
            return OFFLINE_TEXT;
        }
        return ScreenTexts.EMPTY;
    }
}

