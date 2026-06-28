/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsAcceptRejectButton;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsPendingInvitesScreen
extends RealmsScreen {
    final static Identifier ACCEPT_HIGHLIGHTED_ICON_TEXTURE = Identifier.ofVanilla("pending_invite/accept_highlighted");
    final static Identifier ACCEPT_ICON_TEXTURE = Identifier.ofVanilla("pending_invite/accept");
    final static Identifier REJECT_HIGHLIGHTED_ICON_TEXTURE = Identifier.ofVanilla("pending_invite/reject_highlighted");
    final static Identifier REJECT_ICON_TEXTURE = Identifier.ofVanilla("pending_invite/reject");
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Text NO_PENDING_TEXT = Text.translatable("mco.invites.nopending");
    final static Text ACCEPT_TEXT = Text.translatable("mco.invites.button.accept");
    final static Text REJECT_TEXT = Text.translatable("mco.invites.button.reject");
    final private Screen parent;
    final private CompletableFuture<List<PendingInvite>> pendingInvites = CompletableFuture.supplyAsync(() -> {
        try {
            return RealmsClient.create().pendingInvites().pendingInvites;
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't list invites", (Throwable)realmsServiceException);
            return List.of();
        }
    }, Util.getIoWorkerExecutor());
    @Nullable
    Text tooltip;
    PendingInvitationSelectionList pendingInvitationSelectionList;
    private ButtonWidget acceptButton;
    private ButtonWidget rejectButton;

    public RealmsPendingInvitesScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void init() {
        RealmsMainScreen.resetPendingInvitesCount();
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        this.pendingInvites.thenAcceptAsync(pendingInvites -> {
            List<PendingInvitationSelectionListEntry> list = pendingInvites.stream().map(invite -> new PendingInvitationSelectionListEntry((PendingInvite)invite)).toList();
            this.pendingInvitationSelectionList.replaceEntries(list);
            if (list.isEmpty()) {
                this.client.getNarratorManager().narrateSystemMessage(NO_PENDING_TEXT);
            }
        }, this.executor);
        this.addDrawableChild(this.pendingInvitationSelectionList);
        this.acceptButton = this.addDrawableChild(ButtonWidget.builder(ACCEPT_TEXT, button -> this.handle(true)).dimensions(this.width / 2 - 174, this.height - 32, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 50, this.height - 32, 100, 20).build());
        this.rejectButton = this.addDrawableChild(ButtonWidget.builder(REJECT_TEXT, button -> this.handle(false)).dimensions(this.width / 2 + 74, this.height - 32, 100, 20).build());
        this.updateButtonStates();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    void handle(boolean accepted) {
        Object e = this.pendingInvitationSelectionList.getSelectedOrNull();
        if (e instanceof PendingInvitationSelectionListEntry) {
            PendingInvitationSelectionListEntry pendingInvitationSelectionListEntry = (PendingInvitationSelectionListEntry)e;
            String string = pendingInvitationSelectionListEntry.mPendingInvite.invitationId;
            CompletableFuture.supplyAsync(() -> {
                try {
                    RealmsClient realmsClient = RealmsClient.create();
                    if (accepted) {
                        realmsClient.acceptInvitation(string);
                    } else {
                        realmsClient.rejectInvitation(string);
                    }
                    return true;
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't handle invite", (Throwable)realmsServiceException);
                    return false;
                }
            }, Util.getIoWorkerExecutor()).thenAcceptAsync(result -> {
                if (result.booleanValue()) {
                    this.pendingInvitationSelectionList.remove(pendingInvitationSelectionListEntry);
                    this.updateButtonStates();
                    RealmsPeriodicCheckers realmsPeriodicCheckers = this.client.getRealmsPeriodicCheckers();
                    if (accepted) {
                        realmsPeriodicCheckers.serverList.reset();
                    }
                    realmsPeriodicCheckers.pendingInvitesCount.reset();
                }
            }, this.executor);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.tooltip = null;
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, Colors.WHITE);
        if (this.pendingInvites.isDone() && this.pendingInvitationSelectionList.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, NO_PENDING_TEXT, this.width / 2, this.height / 2 - 20, Colors.WHITE);
        }
        if (this.tooltip != null) {
            context.drawTooltip(this.textRenderer, this.tooltip, mouseX, mouseY);
        }
    }

    void updateButtonStates() {
        PendingInvitationSelectionListEntry pendingInvitationSelectionListEntry = (PendingInvitationSelectionListEntry)this.pendingInvitationSelectionList.getSelectedOrNull();
        this.acceptButton.visible = pendingInvitationSelectionListEntry != null;
        this.rejectButton.visible = pendingInvitationSelectionListEntry != null;
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitationSelectionList
    extends AlwaysSelectedEntryListWidget<PendingInvitationSelectionListEntry> {
        public PendingInvitationSelectionList() {
            super(MinecraftClient.getInstance(), RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height - 72, 32, 36);
        }

        @Override
        public int getRowWidth() {
            return 260;
        }

        @Override
        public void setSelected(int index) {
            super.setSelected(index);
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }

        public boolean isEmpty() {
            return this.getEntryCount() == 0;
        }

        public void remove(PendingInvitationSelectionListEntry invitation) {
            this.removeEntry(invitation);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitationSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<PendingInvitationSelectionListEntry> {
        final static private int field_32123 = 38;
        final PendingInvite mPendingInvite;
        final private List<RealmsAcceptRejectButton> buttons;

        PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
            this.mPendingInvite = pendingInvite;
            this.buttons = Arrays.asList(new AcceptButton(), new RejectButton());
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            this.renderPendingInvitationItem(context, this.mPendingInvite, x, y, mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            RealmsAcceptRejectButton.handleClick(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.buttons, button, mouseX, mouseY);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private void renderPendingInvitationItem(DrawContext context, PendingInvite invite, int x, int y, int mouseX, int mouseY) {
            context.drawTextWithShadow(RealmsPendingInvitesScreen.this.textRenderer, invite.worldName, x + 38, y + 1, Colors.WHITE);
            context.drawTextWithShadow(RealmsPendingInvitesScreen.this.textRenderer, invite.worldOwnerName, x + 38, y + 12, RealmsScreen.GRAY);
            context.drawTextWithShadow(RealmsPendingInvitesScreen.this.textRenderer, RealmsUtil.convertToAgePresentation(invite.date), x + 38, y + 24, RealmsScreen.GRAY);
            RealmsAcceptRejectButton.render(context, this.buttons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, x, y, mouseX, mouseY);
            RealmsUtil.drawPlayerHead(context, x, y, 32, invite.worldOwnerUuid);
        }

        @Override
        public Text getNarration() {
            Text text = ScreenTexts.joinLines(Text.literal(this.mPendingInvite.worldName), Text.literal(this.mPendingInvite.worldOwnerName), RealmsUtil.convertToAgePresentation(this.mPendingInvite.date));
            return Text.translatable("narrator.select", text);
        }

        @Environment(value=EnvType.CLIENT)
        class AcceptButton
        extends RealmsAcceptRejectButton {
            AcceptButton() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void render(DrawContext context, int x, int y, boolean showTooltip) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, showTooltip ? ACCEPT_HIGHLIGHTED_ICON_TEXTURE : ACCEPT_ICON_TEXTURE, x, y, 18, 18);
                if (showTooltip) {
                    RealmsPendingInvitesScreen.this.tooltip = ACCEPT_TEXT;
                }
            }

            @Override
            public void handleClick(int index) {
                RealmsPendingInvitesScreen.this.handle(true);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class RejectButton
        extends RealmsAcceptRejectButton {
            RejectButton() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void render(DrawContext context, int x, int y, boolean showTooltip) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, showTooltip ? REJECT_HIGHLIGHTED_ICON_TEXTURE : REJECT_ICON_TEXTURE, x, y, 18, 18);
                if (showTooltip) {
                    RealmsPendingInvitesScreen.this.tooltip = REJECT_TEXT;
                }
            }

            @Override
            public void handleClick(int index) {
                RealmsPendingInvitesScreen.this.handle(false);
            }
        }
    }
}

