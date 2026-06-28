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
package net.minecraft.client.realms.gui.screen.tab;

import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Urls;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
class RealmsSubscriptionInfoTab
extends GridScreenTab
implements RealmsUpdatableTab {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int field_60284 = 200;
    final static private int field_60285 = 2;
    final static private int field_60286 = 6;
    final static Text SUBSCRIPTION_TITLE = Text.translatable("mco.configure.world.subscription.tab");
    final static private Text SUBSCRIPTION_START_LABEL_TEXT = Text.translatable("mco.configure.world.subscription.start");
    final static private Text TIME_LEFT_LABEL_TEXT = Text.translatable("mco.configure.world.subscription.timeleft");
    final static private Text DAYS_LEFT_LABEL_TEXT = Text.translatable("mco.configure.world.subscription.recurring.daysleft");
    final static private Text EXPIRED_TEXT = Text.translatable("mco.configure.world.subscription.expired").formatted(Formatting.GRAY);
    final static private Text EXPIRES_IN_LESS_THAN_A_DAY_TEXT = Text.translatable("mco.configure.world.subscription.less_than_a_day").formatted(Formatting.GRAY);
    final static private Text UNKNOWN_TEXT = Text.translatable("mco.configure.world.subscription.unknown");
    final static private Text RECURRING_INFO_TEXT = Text.translatable("mco.configure.world.subscription.recurring.info");
    final private RealmsConfigureWorldScreen screen;
    final private MinecraftClient client;
    final private ButtonWidget deleteWorldButton;
    final private NarratedMultilineTextWidget subscriptionInfoTextWidget;
    final private TextWidget startDateTextWidget;
    final private TextWidget timeLeftLabelTextWidget;
    final private TextWidget daysLeftTextWidget;
    private RealmsServer serverData;
    private Text daysLeft = UNKNOWN_TEXT;
    private Text startDate = UNKNOWN_TEXT;
    @Nullable
    private Subscription.SubscriptionType type;

    RealmsSubscriptionInfoTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer server) {
        super(SUBSCRIPTION_TITLE);
        this.screen = screen;
        this.client = client;
        this.serverData = server;
        GridWidget.Adder adder = this.grid.setRowSpacing(6).createAdder(1);
        TextRenderer textRenderer = screen.getTextRenderer();
        adder.add(new TextWidget(200, textRenderer.fontHeight, SUBSCRIPTION_START_LABEL_TEXT, textRenderer).alignLeft());
        this.startDateTextWidget = adder.add(new TextWidget(200, textRenderer.fontHeight, this.startDate, textRenderer).alignLeft());
        adder.add(EmptyWidget.ofHeight(2));
        this.timeLeftLabelTextWidget = adder.add(new TextWidget(200, textRenderer.fontHeight, TIME_LEFT_LABEL_TEXT, textRenderer).alignLeft());
        this.daysLeftTextWidget = adder.add(new TextWidget(200, textRenderer.fontHeight, this.daysLeft, textRenderer).alignLeft());
        adder.add(EmptyWidget.ofHeight(2));
        adder.add(ButtonWidget.builder(Text.translatable("mco.configure.world.subscription.extend"), button -> ConfirmLinkScreen.open((Screen)screen, Urls.getExtendJavaRealmsUrl(realmsServer.remoteSubscriptionId, client.getSession().getUuidOrNull()))).dimensions(0, 0, 200, 20).build());
        adder.add(EmptyWidget.ofHeight(2));
        this.deleteWorldButton = adder.add(ButtonWidget.builder(Text.translatable("mco.configure.world.delete.button"), button -> client.setScreen(RealmsPopups.createContinuableWarningPopup(screen, Text.translatable("mco.configure.world.delete.question.line1"), popupScreen -> this.onDeletionConfirmed()))).dimensions(0, 0, 200, 20).build());
        adder.add(EmptyWidget.ofHeight(2));
        this.subscriptionInfoTextWidget = adder.add(new NarratedMultilineTextWidget(200, Text.empty(), textRenderer, true, true, 4), Positioner.create().alignHorizontalCenter());
        this.subscriptionInfoTextWidget.setMaxWidth(200);
        this.subscriptionInfoTextWidget.setCentered(false);
        this.update(server);
    }

    private void onDeletionConfirmed() {
        RealmsUtil.method_72216(realmsClient -> realmsClient.deleteWorld(this.serverData.id), RealmsUtil.method_72221(this.screen::method_72205, "Couldn't delete world")).thenRunAsync(() -> this.client.setScreen(this.screen.getParent()), this.client);
        this.client.setScreen(this.screen);
    }

    private void getSubscription(long worldId) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            Subscription subscription = realmsClient.subscriptionFor(worldId);
            this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
            this.startDate = RealmsSubscriptionInfoTab.localPresentation(subscription.startDate);
            this.type = subscription.type;
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't get subscription", (Throwable)realmsServiceException);
            this.client.setScreen(this.screen.method_72205(realmsServiceException));
        }
    }

    private static Text localPresentation(long time) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(time);
        return Text.literal(DateFormat.getDateTimeInstance().format(calendar.getTime())).formatted(Formatting.GRAY);
    }

    private Text daysLeftPresentation(int daysLeft) {
        boolean bl2;
        if (daysLeft < 0 && this.serverData.expired) {
            return EXPIRED_TEXT;
        }
        if (daysLeft <= 1) {
            return EXPIRES_IN_LESS_THAN_A_DAY_TEXT;
        }
        int i = daysLeft / 30;
        int j = daysLeft % 30;
        boolean bl = i > 0;
        boolean bl3 = bl2 = j > 0;
        if (bl && bl2) {
            return Text.translatable("mco.configure.world.subscription.remaining.months.days", i, j).formatted(Formatting.GRAY);
        }
        if (bl) {
            return Text.translatable("mco.configure.world.subscription.remaining.months", i).formatted(Formatting.GRAY);
        }
        if (bl2) {
            return Text.translatable("mco.configure.world.subscription.remaining.days", j).formatted(Formatting.GRAY);
        }
        return Text.empty();
    }

    @Override
    public void update(RealmsServer server) {
        this.serverData = server;
        this.getSubscription(server.id);
        this.startDateTextWidget.setMessage(this.startDate);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.timeLeftLabelTextWidget.setMessage(TIME_LEFT_LABEL_TEXT);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.timeLeftLabelTextWidget.setMessage(DAYS_LEFT_LABEL_TEXT);
        }
        this.daysLeftTextWidget.setMessage(this.daysLeft);
        boolean bl = RealmsMainScreen.isSnapshotRealmsEligible() && server.parentWorldName != null;
        this.deleteWorldButton.active = server.expired;
        if (bl) {
            this.subscriptionInfoTextWidget.setMessage(Text.translatable("mco.snapshot.subscription.info", server.parentWorldName));
        } else {
            this.subscriptionInfoTextWidget.setMessage(RECURRING_INFO_TEXT);
        }
        this.grid.refreshPositions();
    }

    @Override
    public Text getNarratedHint() {
        return ScreenTexts.joinLines(SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL_TEXT, this.startDate, TIME_LEFT_LABEL_TEXT, this.daysLeft);
    }
}

