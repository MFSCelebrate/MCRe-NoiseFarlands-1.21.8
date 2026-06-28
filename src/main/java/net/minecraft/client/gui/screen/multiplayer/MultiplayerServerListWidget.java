/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    final static Identifier INCOMPATIBLE_TEXTURE = Identifier.ofVanilla("server_list/incompatible");
    final static Identifier UNREACHABLE_TEXTURE = Identifier.ofVanilla("server_list/unreachable");
    final static Identifier PING_1_TEXTURE = Identifier.ofVanilla("server_list/ping_1");
    final static Identifier PING_2_TEXTURE = Identifier.ofVanilla("server_list/ping_2");
    final static Identifier PING_3_TEXTURE = Identifier.ofVanilla("server_list/ping_3");
    final static Identifier PING_4_TEXTURE = Identifier.ofVanilla("server_list/ping_4");
    final static Identifier PING_5_TEXTURE = Identifier.ofVanilla("server_list/ping_5");
    final static Identifier PINGING_1_TEXTURE = Identifier.ofVanilla("server_list/pinging_1");
    final static Identifier PINGING_2_TEXTURE = Identifier.ofVanilla("server_list/pinging_2");
    final static Identifier PINGING_3_TEXTURE = Identifier.ofVanilla("server_list/pinging_3");
    final static Identifier PINGING_4_TEXTURE = Identifier.ofVanilla("server_list/pinging_4");
    final static Identifier PINGING_5_TEXTURE = Identifier.ofVanilla("server_list/pinging_5");
    final static Identifier JOIN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("server_list/join_highlighted");
    final static Identifier JOIN_TEXTURE = Identifier.ofVanilla("server_list/join");
    final static Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("server_list/move_up_highlighted");
    final static Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("server_list/move_up");
    final static Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("server_list/move_down_highlighted");
    final static Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("server_list/move_down");
    final static Logger LOGGER = LogUtils.getLogger();
    final static ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER)).build());
    final static Text LAN_SCANNING_TEXT = Text.translatable("lanServer.scanning");
    final static Text CANNOT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").withColor(Colors.RED);
    final static Text CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").withColor(Colors.RED);
    final static Text INCOMPATIBLE_TEXT = Text.translatable("multiplayer.status.incompatible");
    final static Text NO_CONNECTION_TEXT = Text.translatable("multiplayer.status.no_connection");
    final static Text PINGING_TEXT = Text.translatable("multiplayer.status.pinging");
    final static Text ONLINE_TEXT = Text.translatable("multiplayer.status.online");
    final private MultiplayerScreen screen;
    final private List<ServerEntry> servers = Lists.newArrayList();
    final private Entry scanningEntry = new ScanningEntry();
    final private List<LanServerEntry> lanServers = Lists.newArrayList();

    public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom);
        this.screen = screen;
    }

    private void updateEntries() {
        this.clearEntries();
        this.servers.forEach(server -> this.addEntry(server));
        this.addEntry(this.scanningEntry);
        this.lanServers.forEach(lanServer -> this.addEntry(lanServer));
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.screen.updateButtonActivationStates();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Entry entry = (Entry)this.getSelectedOrNull();
        return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setServers(ServerList servers) {
        this.servers.clear();
        for (int i = 0; i < servers.size(); ++i) {
            this.servers.add(new ServerEntry(this.screen, servers.get(i)));
        }
        this.updateEntries();
    }

    public void setLanServers(List<LanServerInfo> lanServers) {
        int i = lanServers.size() - this.lanServers.size();
        this.lanServers.clear();
        for (LanServerInfo lanServerInfo : lanServers) {
            this.lanServers.add(new LanServerEntry(this.screen, lanServerInfo));
        }
        this.updateEntries();
        for (int j = this.lanServers.size() - i; j < this.lanServers.size(); ++j) {
            LanServerEntry lanServerEntry = this.lanServers.get(j);
            int k = j - this.lanServers.size() + this.children().size();
            int l = this.getRowTop(k);
            int m = this.getRowBottom(k);
            if (m < this.getY() || l > this.getBottom()) continue;
            this.client.getNarratorManager().narrateSystemMessage(Text.translatable("multiplayer.lan.server_found", lanServerEntry.getMotdNarration()));
        }
    }

    @Override
    public int getRowWidth() {
        return 305;
    }

    public void onRemoved() {
    }

    @Environment(value=EnvType.CLIENT)
    public static class ScanningEntry
    extends Entry {
        final private MinecraftClient client = MinecraftClient.getInstance();

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int i = y + entryHeight / 2 - this.client.textRenderer.fontHeight / 2;
            context.drawTextWithShadow(this.client.textRenderer, LAN_SCANNING_TEXT, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(LAN_SCANNING_TEXT) / 2, i, Colors.WHITE);
            String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
            context.drawTextWithShadow(this.client.textRenderer, string, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2, i + this.client.textRenderer.fontHeight, Colors.GRAY);
        }

        @Override
        public Text getNarration() {
            return LAN_SCANNING_TEXT;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry>
    implements AutoCloseable {
        @Override
        public void close() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class ServerEntry
    extends Entry {
        final static private int field_32387 = 32;
        final static private int field_32388 = 32;
        final static private int field_47852 = 5;
        final static private int field_47853 = 10;
        final static private int field_47854 = 8;
        final private MultiplayerScreen screen;
        final private MinecraftClient client;
        final private ServerInfo server;
        final private WorldIcon icon;
        @Nullable
        private byte[] favicon;
        private long time;
        @Nullable
        private List<Text> playerListSummary;
        @Nullable
        private Identifier statusIconTexture;
        @Nullable
        private Text statusTooltipText;

        protected ServerEntry(MultiplayerScreen screen, ServerInfo server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
            this.icon = WorldIcon.forServer(this.client.getTextureManager(), server.address);
            this.update();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            byte[] bs;
            int i;
            if (this.server.getStatus() == ServerInfo.Status.INITIAL) {
                this.server.setStatus(ServerInfo.Status.PINGING);
                this.server.label = ScreenTexts.EMPTY;
                this.server.playerCountLabel = ScreenTexts.EMPTY;
                SERVER_PINGER_THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getServerListPinger().add(this.server, () -> this.client.execute(this::saveFile), () -> {
                            this.server.setStatus(this.server.protocolVersion == SharedConstants.getGameVersion().protocolVersion() ? ServerInfo.Status.SUCCESSFUL : ServerInfo.Status.INCOMPATIBLE);
                            this.client.execute(this::update);
                        });
                    }
                    catch (UnknownHostException unknownHostException) {
                        this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                        this.server.label = CANNOT_RESOLVE_TEXT;
                        this.client.execute(this::update);
                    }
                    catch (Exception exception) {
                        this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                        this.server.label = CANNOT_CONNECT_TEXT;
                        this.client.execute(this::update);
                    }
                });
            }
            context.drawTextWithShadow(this.client.textRenderer, this.server.name, x + 32 + 3, y + 1, Colors.WHITE);
            List<OrderedText> list = this.client.textRenderer.wrapLines(this.server.label, entryWidth - 32 - 2);
            for (i = 0; i < Math.min(list.size(), 2); ++i) {
                context.drawTextWithShadow(this.client.textRenderer, list.get(i), x + 32 + 3, y + 12 + this.client.textRenderer.fontHeight * i, -8355712);
            }
            this.draw(context, x, y, this.icon.getTextureId());
            if (this.server.getStatus() == ServerInfo.Status.PINGING) {
                i = (int)(Util.getMeasuringTimeMs() / 100L + (long)(index * 2) & 7L);
                if (i > 4) {
                    i = 8 - i;
                }
                this.statusIconTexture = switch (i) {
                    default -> PINGING_1_TEXTURE;
                    case 1 -> PINGING_2_TEXTURE;
                    case 2 -> PINGING_3_TEXTURE;
                    case 3 -> PINGING_4_TEXTURE;
                    case 4 -> PINGING_5_TEXTURE;
                };
            }
            i = x + entryWidth - 10 - 5;
            if (this.statusIconTexture != null) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.statusIconTexture, i, y, 10, 8);
            }
            if (!Arrays.equals(bs = this.server.getFavicon(), this.favicon)) {
                if (this.uploadFavicon(bs)) {
                    this.favicon = bs;
                } else {
                    this.server.setFavicon(null);
                    this.saveFile();
                }
            }
            Text text = this.server.getStatus() == ServerInfo.Status.INCOMPATIBLE ? this.server.version.copy().formatted(Formatting.RED) : this.server.playerCountLabel;
            int j = this.client.textRenderer.getWidth(text);
            int k = i - j - 5;
            context.drawTextWithShadow(this.client.textRenderer, text, k, y + 1, Colors.GRAY);
            if (this.statusTooltipText != null && mouseX >= i && mouseX <= i + 10 && mouseY >= y && mouseY <= y + 8) {
                context.drawTooltip(this.statusTooltipText, mouseX, mouseY);
            } else if (this.playerListSummary != null && mouseX >= k && mouseX <= k + j && mouseY >= y && mouseY <= y - 1 + this.client.textRenderer.fontHeight) {
                context.drawTooltip(Lists.transform(this.playerListSummary, Text::asOrderedText), mouseX, mouseY);
            }
            if (this.client.options.getTouchscreen().getValue().booleanValue() || hovered) {
                context.fill(x, y, x + 32, y + 32, -1601138544);
                int l = mouseX - x;
                int m = mouseY - y;
                if (this.canConnect()) {
                    if (l < 32 && l > 16) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JOIN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JOIN_TEXTURE, x, y, 32, 32);
                    }
                }
                if (index > 0) {
                    if (l < 16 && m < 16) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_UP_TEXTURE, x, y, 32, 32);
                    }
                }
                if (index < this.screen.getServerList().size() - 1) {
                    if (l < 16 && m > 16) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_TEXTURE, x, y, 32, 32);
                    }
                }
            }
        }

        private void update() {
            this.playerListSummary = null;
            switch (this.server.getStatus()) {
                case INITIAL: 
                case PINGING: {
                    this.statusIconTexture = PING_1_TEXTURE;
                    this.statusTooltipText = PINGING_TEXT;
                    break;
                }
                case INCOMPATIBLE: {
                    this.statusIconTexture = INCOMPATIBLE_TEXTURE;
                    this.statusTooltipText = INCOMPATIBLE_TEXT;
                    this.playerListSummary = this.server.playerListSummary;
                    break;
                }
                case UNREACHABLE: {
                    this.statusIconTexture = UNREACHABLE_TEXTURE;
                    this.statusTooltipText = NO_CONNECTION_TEXT;
                    break;
                }
                case SUCCESSFUL: {
                    this.statusIconTexture = this.server.ping < 150L ? PING_5_TEXTURE : (this.server.ping < 300L ? PING_4_TEXTURE : (this.server.ping < 600L ? PING_3_TEXTURE : (this.server.ping < 1000L ? PING_2_TEXTURE : PING_1_TEXTURE)));
                    this.statusTooltipText = Text.translatable("multiplayer.status.ping", this.server.ping);
                    this.playerListSummary = this.server.playerListSummary;
                }
            }
        }

        public void saveFile() {
            this.screen.getServerList().saveFile();
        }

        protected void draw(DrawContext context, int x, int y, Identifier textureId) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, textureId, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
        }

        private boolean canConnect() {
            return true;
        }

        private boolean uploadFavicon(@Nullable byte[] bytes) {
            if (bytes == null) {
                this.icon.destroy();
            } else {
                try {
                    this.icon.load(NativeImage.read(bytes));
                }
                catch (Throwable throwable) {
                    LOGGER.error("Invalid icon for server {} ({})", new Object[]{this.server.name, this.server.address, throwable});
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (Screen.hasShiftDown()) {
                MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
                int i = multiplayerServerListWidget.children().indexOf(this);
                if (i == -1) {
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_DOWN && i < this.screen.getServerList().size() - 1 || keyCode == GLFW.GLFW_KEY_UP && i > 0) {
                    this.swapEntries(i, keyCode == GLFW.GLFW_KEY_DOWN ? i + 1 : i - 1);
                    return true;
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void swapEntries(int i, int j) {
            this.screen.getServerList().swapEntries(i, j);
            this.screen.serverListWidget.setServers(this.screen.getServerList());
            Entry entry = (Entry)this.screen.serverListWidget.children().get(j);
            this.screen.serverListWidget.setSelected(entry);
            MultiplayerServerListWidget.this.ensureVisible(entry);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double d = mouseX - (double)MultiplayerServerListWidget.this.getRowLeft();
            double e = mouseY - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
            if (d <= 32.0) {
                if (d < 32.0 && d > 16.0 && this.canConnect()) {
                    this.screen.select(this);
                    this.screen.connect();
                    return true;
                }
                int i = this.screen.serverListWidget.children().indexOf(this);
                if (d < 16.0 && e < 16.0 && i > 0) {
                    this.swapEntries(i, i - 1);
                    return true;
                }
                if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().size() - 1) {
                    this.swapEntries(i, i + 1);
                    return true;
                }
            }
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public ServerInfo getServer() {
            return this.server;
        }

        @Override
        public Text getNarration() {
            MutableText mutableText = Text.empty();
            mutableText.append(Text.translatable("narrator.select", this.server.name));
            mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
            switch (this.server.getStatus()) {
                case INCOMPATIBLE: {
                    mutableText.append(INCOMPATIBLE_TEXT);
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.version.narration", this.server.version));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
                    break;
                }
                case UNREACHABLE: {
                    mutableText.append(NO_CONNECTION_TEXT);
                    break;
                }
                case PINGING: {
                    mutableText.append(PINGING_TEXT);
                    break;
                }
                default: {
                    mutableText.append(ONLINE_TEXT);
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.ping.narration", this.server.ping));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
                    if (this.server.players == null) break;
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.player_count.narration", this.server.players.online(), this.server.players.max()));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Texts.join(this.server.playerListSummary, Text.literal(", ")));
                }
            }
            return mutableText;
        }

        @Override
        public void close() {
            this.icon.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LanServerEntry
    extends Entry {
        final static private int field_32386 = 32;
        final static private Text TITLE_TEXT = Text.translatable("lanServer.title");
        final static private Text HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress");
        final private MultiplayerScreen screen;
        final protected MinecraftClient client;
        final protected LanServerInfo server;
        private long time;

        protected LanServerEntry(MultiplayerScreen screen, LanServerInfo server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            context.drawTextWithShadow(this.client.textRenderer, TITLE_TEXT, x + 32 + 3, y + 1, Colors.WHITE);
            context.drawTextWithShadow(this.client.textRenderer, this.server.getMotd(), x + 32 + 3, y + 12, Colors.GRAY);
            if (this.client.options.hideServerAddress) {
                context.drawTextWithShadow(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, x + 32 + 3, y + 12 + 11, -13619152);
            } else {
                context.drawTextWithShadow(this.client.textRenderer, this.server.getAddressPort(), x + 32 + 3, y + 12 + 11, -13619152);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public LanServerInfo getLanServerEntry() {
            return this.server;
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.getMotdNarration());
        }

        public Text getMotdNarration() {
            return Text.empty().append(TITLE_TEXT).append(ScreenTexts.SPACE).append(this.server.getMotd());
        }
    }
}

