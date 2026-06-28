/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen.tab;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.client.realms.gui.screen.RealmsInviteScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
class RealmsPlayerTab
extends GridScreenTab
implements RealmsUpdatableTab {
    final static Logger LOGGER = LogUtils.getLogger();
    final static Text TITLE = Text.translatable("mco.configure.world.players.title");
    final static Text QUESTION_TEXT = Text.translatable("mco.question");
    final static private int field_49462 = 8;
    final RealmsConfigureWorldScreen screen;
    final MinecraftClient client;
    RealmsServer serverData;
    final private InvitedObjectSelectionList playerList;

    RealmsPlayerTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer server) {
        super(TITLE);
        this.screen = screen;
        this.client = client;
        this.serverData = server;
        GridWidget.Adder adder = this.grid.setSpacing(8).createAdder(1);
        this.playerList = adder.add(new InvitedObjectSelectionList(screen.width, this.getPlayerListHeight()), Positioner.create().alignTop().alignHorizontalCenter());
        adder.add(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.invite"), button -> client.setScreen(new RealmsInviteScreen(screen, server))).build(), Positioner.create().alignBottom().alignHorizontalCenter());
        this.update(server);
    }

    public int getPlayerListHeight() {
        return this.screen.getContentHeight() - 20 - 16;
    }

    @Override
    public void refreshGrid(ScreenRect tabArea) {
        this.playerList.setDimensions(this.screen.width, this.getPlayerListHeight());
        super.refreshGrid(tabArea);
    }

    @Override
    public void update(RealmsServer server) {
        this.serverData = server;
        this.playerList.children().clear();
        for (PlayerInfo playerInfo : server.players) {
            this.playerList.children().add(new InvitedObjectSelectionListEntry(playerInfo));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionList
    extends ElementListWidget<InvitedObjectSelectionListEntry> {
        final static private int field_49472 = 36;

        public InvitedObjectSelectionList(int width, int height) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            int n = RealmsPlayerTab.this.screen.getHeaderHeight();
            Objects.requireNonNull(RealmsPlayerTab.this.screen.getTextRenderer());
            super(minecraftClient, width, height, n, 36, 13);
        }

        @Override
        protected void renderHeader(DrawContext context, int x, int y) {
            String string = RealmsPlayerTab.this.serverData.players != null ? Integer.toString(RealmsPlayerTab.this.serverData.players.size()) : "0";
            MutableText text = Text.translatable("mco.configure.world.invited.number", string).formatted(Formatting.UNDERLINE);
            context.drawTextWithShadow(RealmsPlayerTab.this.screen.getTextRenderer(), text, x + this.getRowWidth() / 2 - RealmsPlayerTab.this.screen.getTextRenderer().getWidth(text) / 2, y, Colors.WHITE);
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {
        }

        @Override
        protected void drawHeaderAndFooterSeparators(DrawContext context) {
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionListEntry
    extends ElementListWidget.Entry<InvitedObjectSelectionListEntry> {
        final static protected int field_60252 = 32;
        final static private Text NORMAL_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.normal.tooltip");
        final static private Text OPS_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.ops.tooltip");
        final static private Text REMOVE_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.remove.tooltip");
        final static private Identifier MAKE_OPERATOR_TEXTURE = Identifier.ofVanilla("player_list/make_operator");
        final static private Identifier REMOVE_OPERATOR_TEXTURE = Identifier.ofVanilla("player_list/remove_operator");
        final static private Identifier REMOVE_PLAYER_TEXTURE = Identifier.ofVanilla("player_list/remove_player");
        final static private int field_49470 = 8;
        final static private int field_49471 = 7;
        final private PlayerInfo playerInfo;
        final private ButtonWidget uninviteButton;
        final private ButtonWidget opButton;
        final private ButtonWidget deopButton;

        public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
            this.playerInfo = playerInfo;
            int i = RealmsPlayerTab.this.serverData.players.indexOf(this.playerInfo);
            this.opButton = TextIconButtonWidget.builder(NORMAL_TOOLTIP_TEXT, button -> this.op(1), false).texture(MAKE_OPERATOR_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth(NORMAL_TOOLTIP_TEXT)).narration(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)textSupplier.get(), Text.translatable("narration.cycle_button.usage.focused", OPS_TOOLTIP_TEXT))).build();
            this.deopButton = TextIconButtonWidget.builder(OPS_TOOLTIP_TEXT, button -> this.deop(1), false).texture(REMOVE_OPERATOR_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth(OPS_TOOLTIP_TEXT)).narration(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)textSupplier.get(), Text.translatable("narration.cycle_button.usage.focused", NORMAL_TOOLTIP_TEXT))).build();
            this.uninviteButton = TextIconButtonWidget.builder(REMOVE_TOOLTIP_TEXT, button -> this.uninvite(1), false).texture(REMOVE_PLAYER_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth(REMOVE_TOOLTIP_TEXT)).narration(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)textSupplier.get())).build();
            this.refreshOpButtonsVisibility();
        }

        private void op(int index) {
            UUID uUID = RealmsPlayerTab.this.serverData.players.get(index).getUuid();
            RealmsUtil.method_72217(realmsClient -> realmsClient.op(RealmsPlayerTab.this.serverData.id, uUID), realmsServiceException -> LOGGER.error("Couldn't op the user", (Throwable)realmsServiceException)).thenAcceptAsync(ops -> {
                this.setOps((Ops)ops);
                this.refreshOpButtonsVisibility();
                this.setFocused(this.deopButton);
            }, (Executor)RealmsPlayerTab.this.client);
        }

        private void deop(int index) {
            UUID uUID = RealmsPlayerTab.this.serverData.players.get(index).getUuid();
            RealmsUtil.method_72217(realmsClient -> realmsClient.deop(RealmsPlayerTab.this.serverData.id, uUID), realmsServiceException -> LOGGER.error("Couldn't deop the user", (Throwable)realmsServiceException)).thenAcceptAsync(ops -> {
                this.setOps((Ops)ops);
                this.refreshOpButtonsVisibility();
                this.setFocused(this.opButton);
            }, (Executor)RealmsPlayerTab.this.client);
        }

        private void uninvite(int index) {
            if (index >= 0 && index < RealmsPlayerTab.this.serverData.players.size()) {
                PlayerInfo playerInfo = RealmsPlayerTab.this.serverData.players.get(index);
                RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(confirmed -> {
                    if (confirmed) {
                        RealmsUtil.method_72216(realmsClient -> realmsClient.uninvite(RealmsPlayerTab.this.serverData.id, playerInfo.getUuid()), realmsServiceException -> LOGGER.error("Couldn't uninvite user", (Throwable)realmsServiceException));
                        RealmsPlayerTab.this.serverData.players.remove(index);
                        RealmsPlayerTab.this.update(RealmsPlayerTab.this.serverData);
                    }
                    RealmsPlayerTab.this.client.setScreen(RealmsPlayerTab.this.screen);
                }, QUESTION_TEXT, Text.translatable("mco.configure.world.uninvite.player", playerInfo.getName()));
                RealmsPlayerTab.this.client.setScreen(realmsConfirmScreen);
            }
        }

        private void setOps(Ops ops) {
            for (PlayerInfo playerInfo : RealmsPlayerTab.this.serverData.players) {
                playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
            }
        }

        private void refreshOpButtonsVisibility() {
            this.opButton.visible = !this.playerInfo.isOperator();
            this.deopButton.visible = !this.opButton.visible;
        }

        private ButtonWidget getOpButton() {
            if (this.opButton.visible) {
                return this.opButton;
            }
            return this.deopButton;
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of((Object)this.getOpButton(), (Object)this.uninviteButton);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of((Object)this.getOpButton(), (Object)this.uninviteButton);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int i = !this.playerInfo.isAccepted() ? -6250336 : (this.playerInfo.isOnline() ? -16711936 : Colors.WHITE);
            int j = y + entryHeight / 2 - 16;
            RealmsUtil.drawPlayerHead(context, x, j, 32, this.playerInfo.getUuid());
            int k = y + entryHeight / 2 - RealmsPlayerTab.this.screen.getTextRenderer().fontHeight / 2;
            context.drawTextWithShadow(RealmsPlayerTab.this.screen.getTextRenderer(), this.playerInfo.getName(), x + 8 + 32, k, i);
            int l = y + entryHeight / 2 - 10;
            int m = x + entryWidth - this.uninviteButton.getWidth();
            this.uninviteButton.setPosition(m, l);
            this.uninviteButton.render(context, mouseX, mouseY, tickProgress);
            int n = m - this.getOpButton().getWidth() - 8;
            this.opButton.setPosition(n, l);
            this.opButton.render(context, mouseX, mouseY, tickProgress);
            this.deopButton.setPosition(n, l);
            this.deopButton.render(context, mouseX, mouseY, tickProgress);
        }
    }
}

