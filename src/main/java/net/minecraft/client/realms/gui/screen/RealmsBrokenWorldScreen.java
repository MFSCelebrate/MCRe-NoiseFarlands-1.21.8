/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBrokenWorldScreen
extends RealmsScreen {
    final static private Identifier SLOT_FRAME_TEXTURE = Identifier.ofVanilla("widget/slot_frame");
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int field_32120 = 80;
    final private Screen parent;
    @Nullable
    private RealmsServer serverData;
    final private long serverId;
    final private Text[] message = new Text[]{Text.translatable("mco.brokenworld.message.line1"), Text.translatable("mco.brokenworld.message.line2")};
    private int left_x;
    final private List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen parent, long serverId, boolean minigame) {
        super(minigame ? Text.translatable("mco.brokenworld.minigame.title") : Text.translatable("mco.brokenworld.title"));
        this.parent = parent;
        this.serverId = serverId;
    }

    @Override
    public void init() {
        this.left_x = this.width / 2 - 150;
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions((this.width - 150) / 2, RealmsBrokenWorldScreen.row(13) - 5, 150, 20).build());
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
    }

    @Override
    public Text getNarratedTitle() {
        return Texts.join(Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), ScreenTexts.SPACE);
    }

    private void addButtons() {
        for (Map.Entry<Integer, RealmsSlot> entry : this.serverData.slots.entrySet()) {
            ButtonWidget buttonWidget;
            boolean bl;
            int i = entry.getKey();
            boolean bl2 = bl = 1 != this.serverData.activeSlot || this.serverData.isMinigame();
            if (bl) {
                buttonWidget = ButtonWidget.builder(Text.translatable("mco.brokenworld.play"), button -> this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.serverData.id, 1, this::play)))).dimensions(this.getFramePositionX(1), RealmsBrokenWorldScreen.row(8), 80, 20).build();
                buttonWidget.active = !this.serverData.slots.get((Object)Integer.valueOf(1)).options.empty;
            } else {
                buttonWidget = ButtonWidget.builder(Text.translatable("mco.brokenworld.download"), button -> this.client.setScreen(RealmsPopups.createInfoPopup(this, Text.translatable("mco.configure.world.restore.download.question.line1"), popupScreen -> this.downloadWorld(1)))).dimensions(this.getFramePositionX(1), RealmsBrokenWorldScreen.row(8), 80, 20).build();
            }
            if (this.slotsThatHasBeenDownloaded.contains(1)) {
                buttonWidget.active = false;
                buttonWidget.setMessage(Text.translatable("mco.brokenworld.downloaded"));
            }
            this.addDrawableChild(buttonWidget);
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, Colors.WHITE);
        for (int i = 0; i < this.message.length; ++i) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.message[i], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + i * 12, Colors.LIGHT_GRAY);
        }
        if (this.serverData == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsSlot> entry : this.serverData.slots.entrySet()) {
            if (entry.getValue().options.templateImage != null && entry.getValue().options.templateId != -1L) {
                this.drawSlotFrame(context, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().options.getSlotName(entry.getKey()), entry.getKey(), entry.getValue().options.templateId, entry.getValue().options.templateImage, entry.getValue().options.empty);
                continue;
            }
            this.drawSlotFrame(context, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().options.getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().options.empty);
        }
    }

    private int getFramePositionX(int i) {
        return this.left_x + (i - 1) * 110;
    }

    public Screen method_72203(RealmsServiceException realmsServiceException) {
        return new RealmsGenericErrorScreen(realmsServiceException, this.parent);
    }

    private void fetchServerData(long worldId) {
        RealmsUtil.method_72217(realmsClient -> realmsClient.getOwnWorld(worldId), RealmsUtil.method_72221(this::method_72203, "Couldn't get own world")).thenAcceptAsync(realmsServer -> {
            this.serverData = realmsServer;
            this.addButtons();
        }, (Executor)this.client);
    }

    public void play() {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            if (this.serverData.state == RealmsServer.State.CLOSED) {
                this.client.execute(() -> this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, true, this.client))));
            } else {
                try {
                    RealmsServer realmsServer = realmsClient.getOwnWorld(this.serverId);
                    this.client.execute(() -> RealmsMainScreen.play(realmsServer, this));
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't get own world", (Throwable)realmsServiceException);
                    this.client.execute(() -> this.client.setScreen(this.method_72203(realmsServiceException)));
                }
            }
        }).start();
    }

    private void downloadWorld(int slotId) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            WorldDownload worldDownload = realmsClient.download(this.serverData.id, slotId);
            RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(this, worldDownload, this.serverData.getWorldName(slotId), successful -> {
                if (successful) {
                    this.slotsThatHasBeenDownloaded.add(slotId);
                    this.clearChildren();
                    this.addButtons();
                } else {
                    this.client.setScreen(this);
                }
            });
            this.client.setScreen(realmsDownloadLatestWorldScreen);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't download world data", (Throwable)realmsServiceException);
            this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.isMinigame();
    }

    private void drawSlotFrame(DrawContext context, int x, int y, int mouseX, int mouseY, boolean activeSlot, String slotName, int slotId, long templateId, @Nullable String templateImage, boolean empty) {
        Identifier identifier = empty ? RealmsWorldSlotButton.EMPTY_FRAME : (templateImage != null && templateId != -1L ? RealmsTextureManager.getTextureId(String.valueOf(templateId), templateImage) : (slotId == 1 ? RealmsWorldSlotButton.PANORAMA_0 : (slotId == 2 ? RealmsWorldSlotButton.PANORAMA_2 : (slotId == 3 ? RealmsWorldSlotButton.PANORAMA_3 : RealmsTextureManager.getTextureId(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage)))));
        if (activeSlot) {
            float f = 0.9f + 0.1f * MathHelper.cos((float)this.animTick * 0.2f);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74, 74, 74, ColorHelper.fromFloats(1.0f, f, f, f));
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_TEXTURE, x, y, 80, 80);
        } else {
            int i = ColorHelper.fromFloats(1.0f, 0.56f, 0.56f, 0.56f);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74, 74, 74, i);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_TEXTURE, x, y, 80, 80, i);
        }
        context.drawCenteredTextWithShadow(this.textRenderer, slotName, x + 40, y + 66, Colors.WHITE);
    }
}

